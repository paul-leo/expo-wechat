import { NativeModule, requireNativeModule } from 'expo';

import { ExpoWechatModuleEvents } from './ExpoWechat.types';

declare class ExpoWechatModule extends NativeModule<ExpoWechatModuleEvents> {

  isWXAppInstalled: boolean
  isWXAppSupportApi: boolean
  isWXAppSupportStateAPI: boolean
  isWXAppSupportQRCodePayAPI: boolean
  getWXAppInstallUrl: string;
  getApiVersion: string;
  
  /**
   * Initialize Wechat SDK. Returns true if succeed.
   * @param appId 微信App ID
   * @param universalLink 通用链接地址
   */
  registerApp(appId: string, universalLink: string): Promise<boolean>

  /**
   * Open Wechat App. Returns true if succeed.
   */
  openWXApp(): Promise<boolean>

}

// This call loads the native module object from the JSI.
export default requireNativeModule<ExpoWechatModule>('ExpoWechat');
