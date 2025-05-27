package expo.modules.wechat

import android.content.Intent
import android.util.Base64
import com.tencent.mm.opensdk.diffdev.DiffDevOAuthFactory
import com.tencent.mm.opensdk.diffdev.OAuthErrCode
import com.tencent.mm.opensdk.diffdev.OAuthListener
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage
import com.tencent.mm.opensdk.modelmsg.WXTextObject
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import expo.modules.kotlin.Promise
import expo.modules.kotlin.exception.CodedException
import expo.modules.kotlin.functions.Coroutine
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition


val apiNotRegisteredException =
    CodedException("-1", "Please call registerApp to initialize WX api first! ", null)

class ExpoWechatModule : Module(), IWXAPIEventHandler {

    companion object {
        var moduleInstance: ExpoWechatModule? = null

        fun sendEventToJS(eventName: String, params: Map<String, Any?>) {
            moduleInstance?.sendEvent(eventName, params)
        }
    }

    var api: IWXAPI? = null;
    var wxAppId: String? = null;

    // Each module class must implement the definition function. The definition consists of components
    // that describes the module's functionality and behavior.
    // See https://docs.expo.dev/modules/module-api for more details about available components.
    override fun definition() = ModuleDefinition {
        // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
        // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
        // The module will be accessible from `requireNativeModule('ExpoWechat')` in JavaScript.
        Name("ExpoWechat")

        // Sets constant properties on the module. Can take a dictionary or a closure that returns a dictionary.
        Constants(
            "PI" to Math.PI
        )

        // Defines event names that the module can send to JavaScript.
        Events("onQRCodeAuthGotQRCode", "onQRCodeAuthUserScanned", "onQRCodeAuthFinished")

        OnCreate {
            moduleInstance = this@ExpoWechatModule
        }

        AsyncFunction("registerApp") { appId: String, universalLink: String ->
            wxAppId = appId;
            api = WXAPIFactory.createWXAPI(appContext.reactContext, appId, true)
            api?.registerApp(appId)
            return@AsyncFunction true
        }

        AsyncFunction("isWXAppInstalled") {
            if (api != null) {
                return@AsyncFunction api!!.isWXAppInstalled
            } else {
                throw apiNotRegisteredException
            }
        }

        AsyncFunction("getApiVersion") {
            if (api != null) {
                return@AsyncFunction api!!.wxAppSupportAPI
            } else {
                throw apiNotRegisteredException
            }
        }

        AsyncFunction("openWXApp") {
            if (api != null) {
                return@AsyncFunction api!!.openWXApp()
            } else {
                throw apiNotRegisteredException
            }
        }

        AsyncFunction("sendAuthRequest") { scope: String, state: String, promise: Promise ->
            if (api != null) {
                val authRequest = SendAuth.Req()
                authRequest.scope = scope
                authRequest.state = state
                api?.sendReq(
                    authRequest
                ) { p0 -> promise.resolve(p0) }
            } else {
                promise.reject(apiNotRegisteredException)
            }
        }

        AsyncFunction("sendAuthByQRRequest") Coroutine { appId: String, appSecret: String, scope: String ->
            if (api != null) {

                val accessToken = WeChatSDKUtils.getAccessToken(appId, appSecret);
                if (accessToken != null) {
                    val ticket = WeChatSDKUtils.getSDKTicket(accessToken)
                    if (ticket != null) {
                        val nonceString = WeChatSDKUtils.generateObjectId()
                        val timestamp = System.currentTimeMillis().toString()
                        val signature =
                            WeChatSDKUtils.createSignature(appId, nonceString, ticket, timestamp)
                        val oauth = DiffDevOAuthFactory.getDiffDevOAuth();
                        val result = oauth.auth(
                            appId, scope, nonceString, timestamp, signature,
                            object : OAuthListener {
                                override fun onAuthGotQrcode(p0: String?, p1: ByteArray?) {
                                    val base64 = Base64.encodeToString(p1, Base64.DEFAULT)
                                    sendEvent("onQRCodeAuthGotQRCode", mapOf("image" to base64))
                                }

                                override fun onQrcodeScanned() {
                                    sendEvent("onQRCodeAuthUserScanned")
                                }

                                override fun onAuthFinish(p0: OAuthErrCode?, p1: String?) {
                                    sendEvent(
                                        "onQRCodeAuthFinished",
                                        mapOf("errorCode" to p0, "authCode" to p1)
                                    )
                                }
                            })
                        return@Coroutine result
                    } else {
                        throw CodedException(
                            "ERR_TICKET",
                            "Cannot get wechat ticket, please check networking log to find out why",
                            null
                        )
                    }
                } else {
                    throw CodedException(
                        "ERR_ACCESS_TOKEN",
                        "Cannot get wechat access token, please check networking log to find out why",
                        null
                    )
                }
            } else {
                throw apiNotRegisteredException
            }
        }

        AsyncFunction("shareText") { text: String, scene: String, promise: Promise ->
            if (api != null) {
                val textObject = WXTextObject()
                textObject.text = text

                val mediaMessage = WXMediaMessage()
                mediaMessage.mediaObject = textObject
                mediaMessage.description = text

                val req = SendMessageToWX.Req()
                req.transaction = "text"
                req.message = mediaMessage
                req.scene = WeChatSDKUtils.getWeChatShareScene(scene)
                api?.sendReq(
                    req
                ) { p0 -> promise.resolve(p0) }
            } else {
                promise.reject(apiNotRegisteredException)
            }
        }
    }

    fun handleIntent(intent: Intent) {
        api?.handleIntent(intent, this);
    }

    override fun onReq(p0: BaseReq?) {

    }

    override fun onResp(p0: BaseResp?) {
        
    }
}
