package expo.modules.wechat.wxapi

import android.app.Activity
import android.os.Bundle
import expo.modules.wechat.ExpoWechatModule

class WXEntryActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ExpoWechatModule.moduleInstance?.handleIntent(intent);
        finish()
    }
}