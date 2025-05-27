package expo.modules.wechat

import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.SendReqCallback
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import expo.modules.kotlin.Promise
import expo.modules.kotlin.exception.CodedException
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import java.net.URL

val apiNotRegisteredException = CodedException("-1", "Please call registerApp to initialize WX api first! ", null)

class ExpoWechatModule : Module() {
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
    Events("onChange")

    AsyncFunction("registerApp") { appId: String, universalLink: String ->
      wxAppId = appId;
      api = WXAPIFactory.createWXAPI(appContext.reactContext, appId, true)
      api?.registerApp(appId)
      return@AsyncFunction true
    }

    AsyncFunction("isWXAppInstalled") { promise: Promise ->
      if (api != null) {
        promise.resolve(api!!.isWXAppInstalled)
      } else {
        promise.reject(apiNotRegisteredException)
      }
    }

    AsyncFunction("getApiVersion") { promise: Promise ->
      if (api != null) {
        promise.resolve(api!!.wxAppSupportAPI)
      } else {
        promise.reject(apiNotRegisteredException)
      }
    }

    AsyncFunction("openWXApp") { promise: Promise ->
      if (api != null) {
        promise.resolve(api!!.openWXApp())
      } else {
        promise.reject(apiNotRegisteredException)
      }
    }

    AsyncFunction("sendAuthRequest") { scope: String, state: String, promise: Promise ->
      if (api != null) {
        val authRequest = SendAuth.Req()
        authRequest.scope = scope
        authRequest.state = state
        api?.sendReq(authRequest, object : SendReqCallback {
          override fun onSendFinish(p0: Boolean) {

          }
        })
      } else {
        promise.reject(apiNotRegisteredException)
      }
    }



  }
}
