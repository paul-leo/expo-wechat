import { registerWebModule, NativeModule } from 'expo';

import { ExpoWechatModuleEvents } from './ExpoWechat.types';

class ExpoWechatModule extends NativeModule<ExpoWechatModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
}

export default registerWebModule(ExpoWechatModule, 'ExpoWechatModule');
