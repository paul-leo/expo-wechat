import { ConfigPlugin, withInfoPlist } from "expo/config-plugins";

const withExpoWechat: ConfigPlugin = (config) => {

    config = withInfoPlist(config, config => {

        let queriesSchemes = config.modResults.LSApplicationQueriesSchemes ?? []
        queriesSchemes.unshift("weixin", "weixinULAPI", "weixinURLParamsAPI", "weixinQRCodePayAPI")
        config.modResults.LSApplicationQueriesSchemes = queriesSchemes
        return config
    })

  return config;
};

export default withExpoWechat;
