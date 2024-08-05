# 编写你的第一个 Addon
在本节中，我们将会教你如何编写你的第一个 Addon。

在上一个章节中，我们已经创建了一个新的项目目录，并且初始化了一个新的 KasugaLib 项目。

接下来，我们将会创建一个简单的 Addon，这个 Addon 将会在启动时向控制台输出一段文字。

```typescript
// src/index.ts
import { EventBus } from '@kasugalib/core';
EventBus.on('start', () => {
  console.log('Hello, Kasuga!');
});
```

然后使用以下命令编译你的代码并生成一个 Addon Pack。

```bash
yarn build --pack
```

生成的 Addon Pack 将会被保存在 `build/index.zip`，将其复制到`.minecraft/addons`目录下，
然后启动游戏，你将会在游戏日志中看到`Hello, Kasuga!`的输出。

恭喜你，你已经成功编写了你的第一个 Addon！
