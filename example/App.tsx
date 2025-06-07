import React, { useState, useCallback, Children } from "react";
import { useEvent } from "expo";
import ExpoWechat from "expo-wechat";
import {
  Button,
  FlatList,
  SafeAreaView,
  ScrollView,
  Text,
  View,
  StyleSheet,
} from "react-native";

export default function App() {
  const [initialized, setInitialized] = useState(false);
  const onAuthResult = useEvent(ExpoWechat, "onAuthResult");
  const wechatAppId = process.env.EXPO_PUBLIC_WECHAT_APP_ID;
  const universalLink = process.env.EXPO_PUBLIC_UNIVERSAL_LINK;
  const isParametersValid = Boolean(wechatAppId) && Boolean(universalLink);

  const initializeSDK = useCallback(async () => {
    if (!isParametersValid) {
      return;
    }
    const result = await ExpoWechat.registerApp(wechatAppId, universalLink);
    setInitialized(result);
  }, [wechatAppId, universalLink, isParametersValid]);

  const onWeChatLogin = useCallback(async () => {
    if (!initialized) {
      return;
    }
    const result = await ExpoWechat.sendAuthRequest("snsapi_userinfo", "123");
    console.log('Send auth request result:', result);
  }, [initialized]);

  return (
    <ScrollView
      style={styles.scrollView}
      contentContainerStyle={styles.contentContainer}
    >
      <Group title="初始化微信SDK">
        <Text>微信App ID: {wechatAppId}</Text>
        <Text>通用链接: {universalLink}</Text>
        <Text>
          {isParametersValid
            ? "参数存在，请进行初始化。"
            : "参数不足，请在.env文件中配置微信应用ID和通用链接。"}
        </Text>
        {initialized && <Text>微信SDK已初始化成功，请体验以下功能！</Text>}
        {!initialized && (
          <Button title="初始化微信SDK" onPress={initializeSDK} />
        )}
      </Group>
      <Group title="微信登录">
        <Button title="点击登录" onPress={onWeChatLogin} />
        <Text>
          微信登陆结果：{'\n'}
          {JSON.stringify(onAuthResult, null, 2)}
        </Text>
      </Group>
    </ScrollView>
  );
}

type GroupProps = {
  title: string;
  children: React.ReactNode;
};

const Group: React.FC<GroupProps> = React.memo((props) => {
  return (
    <View style={styles.group}>
      <Text style={styles.groupTitle}>{props.title}</Text>
      {props.children}
    </View>
  );
});

const styles = StyleSheet.create({
  scrollView: {
    flex: 1,
    backgroundColor: "#F5F5F5",
  },
  contentContainer: {
    padding: 16,
  },

  group: {
    backgroundColor: "#FFF",
    borderRadius: 12,
    padding: 16,
    marginBottom: 16,
    gap: 12,
  },
  groupTitle: {
    fontSize: 16,
    fontWeight: "600",
    color: "#222",
  },
});
