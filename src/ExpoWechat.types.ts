import type { StyleProp, ViewStyle } from 'react-native';

export type OnLoadEventPayload = {
  url: string;
};

export type ExpoWechatModuleEvents = {
  onQRCodeAuthGotQRCode: (params: QRCodeAuthGotQRCodePayload) => void;
  onQRCodeAuthFinished: (params: QRCodeAuthFinishedPayload) => void;
};

export type QRCodeAuthGotQRCodePayload = {
  /**
   * 二维码base64编码的图片数据。
   */
  image: string;
};

export type QRCodeAuthFinishedPayload = {
  /**
   * 错误码。0表示授权成功。
   */
  errorCode: number;
  /**
   * 授权登录结果。
   */
  authCode: string;
}

/**
 * 微信分享目标场景。发送到聊天、朋友圈、收藏。
 */
export type ExpoWeChatShareScene = 'session' | 'timeline' | 'favorite' | 'status' | 'specifiedContact';
