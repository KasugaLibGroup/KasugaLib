# 初始化 Javascript Addons 开发环境
对于 Kasuga Javascript Addons，我们使用 Node JS 的 yarn(npm) 包管理器来管理项目依赖，同时使用 `package.json` 文件来描述项目的基本信息和依赖。

在开始开发 Javascript Addons 之前，我们强烈建议您安装 Node JS 和 yarn 包管理器，
在接下来的示例中，我们将使用`yarn`作为管理您的开发环境的工具，
您可以在 [Node JS 官网](https://nodejs.org/) 下载安装 Node JS。

如果您不想使用这么笨重的开发环境，我们也提供了一种轻量的方式开始您的开发，但请注意，这种方式存在一定的限制
（如:其他 Addons 不能调用您的 Addons 提供的 API，且您无法追踪对其他接口的调用）

您可以查看 [单文件Addons开发](./sfa.md) 了解更多信息。

## 下载模板项目

我们提供了一个模板项目，您可以直接下载并开始开发。

```bash
git clone https://github.com/KasugaLibGroup/AddonsTemplate.git
cd AddonsTemplate
yarn
``` 

## 从命令行初始化项目

首先，我们需要创建一个新的项目目录，然后在项目目录中初始化一个新的 Node JS 项目。

```bash
mkdir my-addon
cd my-addon
yarn init
```

在初始化项目时，您需要填写一些基本信息，如项目名称，版本，描述等。
```bash
yarn init v1.22.10
question name (my-addon): my-addon
question version (1.0.0): 1.0.0
question description: My first Kasuga Addon
question entry point (index.js):
question repository url:
question author: Your Name
question license (MIT):
question private: 
success Saved package.json
Done in 20.41s.
```

接着我们需要创建基本项目架构
```bash
mkdir src # 存储编译前的源代码文件
mkdir lib # 存储编译后的代码文件
mkdir client # 存储客户端代码
mkdir server # 存储服务端代码
mkdir assets # 存储资源文件
mkdir dist
mkdir dist/client # 存储客户端编译后的代码
mkdir dist/server # 存储服务端编译后的代码
touch src/index.ts # 创建一个入口文件
touch client/index.ts # 创建客户端入口文件
```

然后你需要在package.json中添加一些基本的配置
```json5
{
  // ... names 等信息
  "minecraft": {
    "server": ["server/index.js"],
    "client": ["client/index.js"],
    "assets": "assets/"
  }
}
```

## 安装依赖

接着我们需要安装用于开发的基本依赖项
```bash
yarn add -D @kasugalib/core # 导入 KasugaLib 核心库
# 如果您需要使用 TypeScript 开发，您还需要安装 TypeScript, ESBuild 和 TypeScript 的类型定义，并配置tsconfig.json
yarn add -D typescript esbuild @types/node
```

并参照模板项目的`tsconfig.json`,`tsconfig.client.json`,`tsconfig.server.json`文件配置合适的TypeScript配置。