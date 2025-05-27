import { NativeModule, requireNativeModule } from 'expo';

import { ExpoWechatModuleEvents, ExpoWeChatShareScene } from './ExpoWechat.types';

declare class ExpoWechatModule extends NativeModule<ExpoWechatModuleEvents> {
  isWXAppInstalled(): Promise<boolean>
  // isWXAppSupportApi(): Promise<number>
  // getWXAppInstallUrl(): Promise<string>
  getApiVersion(): Promise<string>
  
  /**
   * 初始化微信SDK。返回初始化结果。
   * @param appId 微信App ID
   * @param universalLink 通用链接地址
   */
  registerApp(appId: string, universalLink: string): Promise<boolean>

  /**
   * 打开微信App。返回打开结果。
   */
  openWXApp(): Promise<boolean>

  /**
   * 发送微信授权登录请求。返回**发送**结果，注意是发送结果不是授权结果，授权结果要从事件中获取。
   * @param scope 微信scope字段。
   * @param state 微信state字段。
   */
  sendAuthRequest(scope: string, state: string): Promise<boolean>

  /**
   * 发送微信扫码登录请求。返回微信登录二维码。
   * @param appId 微信App ID
   * @param appSecret 微信App Secret
   */
  sendAuthByQRRequest(appId: string, appSecret: string, scope: string): Promise<string>

  /**
   * 分享文字到微信。返回分享结果。
   * @param text 要分享的文字内容。
   * @param scene 分享目标场景。
   */
  shareText(text: string, scene: ExpoWeChatShareScene): Promise<any>

  /**
   * 分享图片到微信。返回分享结果。
   * @param base64OrImageUri 图片内容，可以是远程网络图片，或者base64编码的图片数据。
   * @param scene 要分享的目标场景。
   */
  shareImage(base64OrImageUri: string, scene: ExpoWeChatShareScene): Promise<any>

  /**
   * 分享文件到微信。返回分享结果。
   * @param uri 文件URI。
   * @param scene 要分享的目标场景。
   */
  shareFile(uri: string, scene: ExpoWeChatShareScene): Promise<any>

  // shareMusic()
  // shareVideo()
  // shareWebpage()
  // shareMiniProgram()
  // launchMiniProgram()
  // pay()
  // chooseInvoice()
}

// This call loads the native module object from the JSI.
export default requireNativeModule<ExpoWechatModule>('ExpoWechat');
