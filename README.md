# expo-wechat
React Native Expo版本的微信SDK。
本框架旨在让你所有原生代码配置都在RN侧以及json文件中进行，真正做到0原生代码配置，充分利用expo的优势来做到简单好用。

⚠️项目测试中，敬请期待⚠️

# 安装
```shell
npx expo install @aron/expo-wechat

# 不带支付版本
# npx expo install @aron/expo-wechat-no-pay
```

# 配置

## iOS
iOS需要配置通用链接和URL Scheme。

URL Scheme用于给你的应用注册一个独一无二的链接，使别的软件可以通过这个链接直接唤起你的App。
是微信回调起你的App的保底方案，当通用链接唤起失败后，微信会尝试使用URL Scheme来唤起你的App。这个URL Scheme就是微信开放平台给你的微信id，类似于`wx1234567890`这种格式的。

通用链接是微信首推的唤起微信和你的App的方案，当通用链接没有配置好的时候，才会回退到URL Scheme方案。
通用链接允许你向苹果注册一个URL地址，当访问这个地址的时候，系统优先唤起你的App，而不是网页。简单来说，它是一种比URL Scheme更好的唤起App的解决方案。

使用Expo官方提供的方式来添加URL Scheme，以及配置通用链接。在`app.json`中添加以下字段：
```json
...
"ios": {
    "scheme": [
        "wx1234567890"
    ],
    "associatedDomains": [
        "applinks:example.com"
    ]
}
...
```
这里的通用链接如何创建，以及如何向苹果注册，也许你需要参照一下[苹果官方文档](https://developer.apple.com/documentation/xcode/supporting-associated-domains)。

URL Scheme白名单，也就是`LSApplicationQueriesSchemes`字段，因为是固定不变的，所以已经自动帮你配置好。

## 安卓

```text
-keep class com.tencent.mm.opensdk.** {
    *;
}

-keep class com.tencent.wxop.** {
    *;
}

-keep class com.tencent.mm.sdk.** {
    *;
}
```

需要配置proguard文件，在`app.json`中添加以下字段：
```json
"android": {
    "extraProguardRules": ""
}
...


## 总结
配置部分，iOS需要配置URL Scheme和通用链接，安卓需要配置混淆规则，这些都可以通过expo的`app.json`来完成，配置部分完成后，就可以正常使用微信SDK了。

# 使用

```typescript
import * as ExpoWeChat from '@aron/expo-wechat'

const result = await ExpoWeChat.registerApp(wechatAppId, universalLink);
```

# Example

克隆本仓库，进入example文件夹，执行`npm i`，再启动项目。
启动之前，请在`.env`文件内配置微信AppId和Key，通用链接。

# 鸣谢
本框架参考了大量的[react-native-wechat-lib](https://github.com/little-snow-fox/react-native-wechat-lib)的代码，实现了基本上所有的API的功能，在此基础上，极大的简化了配置流程，并使用了最新的微信SDK，感谢前人！

