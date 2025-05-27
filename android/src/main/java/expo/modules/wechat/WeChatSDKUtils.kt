package expo.modules.wechat

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.security.MessageDigest

class WeChatSDKUtils {
    companion object {
        suspend fun getAccessToken(weiXinId: String, weiXinSecret: String): String? {
            val url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential" +
                    "&appid=$weiXinId&secret=$weiXinSecret"

            return withContext(Dispatchers.IO) {
                try {
                    val responseText = URL(url).readText()
                    val jsonResponse = JSONObject(responseText)
                    jsonResponse.getString("access_token")
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        // 1. 获取SDK Ticket
        suspend fun getSDKTicket(accessToken: String): String? {
            val url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=2&access_token=$accessToken"

            return withContext(Dispatchers.IO) {
                try {
                    val response = URL(url).readText()
                    JSONObject(response).getString("ticket")
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        // 2. 创建签名 (使用Java原生SHA1)
        fun createSignature(
            weiXinId: String,
            nonceStr: String,
            sdkTicket: String,
            timestamp: String
        ): String {
            val origin = "appid=$weiXinId&noncestr=$nonceStr&sdk_ticket=$sdkTicket&timestamp=$timestamp"

            val bytes = MessageDigest.getInstance("SHA-1")
                .digest(origin.toByteArray(Charsets.UTF_8))
            return bytes.joinToString("") { "%02x".format(it) }
        }

        // 3. 获取用户信息 (使用协程避免回调地狱)
        suspend fun getUserInfo(
            weiXinId: String,
            weiXinSecret: String,
            code: String
        ): Map<String, Any?>? {
            return withContext(Dispatchers.IO) {
                try {
                    // 第一步：获取access_token
                    val tokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                            "appid=$weiXinId&secret=$weiXinSecret&code=$code&grant_type=authorization_code"

                    val tokenResponse = URL(tokenUrl).readText()
                    val tokenJson = JSONObject(tokenResponse)
                    val accessToken = tokenJson.getString("access_token")
                    val openid = tokenJson.getString("openid")

                    // 第二步：获取用户信息
                    val userInfoUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=$accessToken&openid=$openid"
                    val userInfoResponse = URL(userInfoUrl).readText()
                    val userJson = JSONObject(userInfoResponse)

                    mapOf(
                        "nickname" to userJson.getString("nickname"),
                        "headimgurl" to userJson.getString("headimgurl"),
                        "openid" to openid,
                        "unionid" to userJson.optString("unionid")
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }

        // 4. 生成ObjectID (使用Kotlin原生随机数)
        fun generateObjectId(): String {
            val timestamp = (System.currentTimeMillis() / 1000).toInt().toString(16)
            val randomPart = buildString {
                repeat(16) {
                    append(((0..15).random()).toString(16))
                }
            }
            return timestamp + randomPart
        }

        fun getWeChatShareScene(scene: String): Int {
            return when (scene) {
                "session" -> SendMessageToWX.Req.WXSceneSession
                "timeline" -> SendMessageToWX.Req.WXSceneTimeline
                "favorite" -> SendMessageToWX.Req.WXSceneFavorite
                "status" -> SendMessageToWX.Req.WXSceneStatus
                "contact" -> SendMessageToWX.Req.WXSceneSpecifiedContact
                else -> SendMessageToWX.Req.WXSceneSession
            }
        }
    }
}