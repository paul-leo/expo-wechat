import type { StyleProp, ViewStyle } from 'react-native';

export type OnLoadEventPayload = {
  url: string;
};

export type ExpoWechatModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;
};

export type ChangeEventPayload = {
  value: string;
};

/**
 * 微信分享目标场景。发送到聊天、朋友圈、收藏。
 */
export type ExpoWeChatShareScene = 'session' | 'timeline' | 'favorite' | 'status' | 'specifiedContact';
