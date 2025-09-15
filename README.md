# EasyWallet - 多平台去中心化钱包

[![Github Release](https://github.com/BreakZero/EasyWallet-Multiplatform/actions/workflows/Release.yml/badge.svg)](https://github.com/BreakZero/EasyWallet-Multiplatform/actions/workflows/Release.yml)
[![Check Code Style](https://github.com/BreakZero/EasyWallet-Multiplatform/actions/workflows/CheckCodeStyle.yml/badge.svg)](https://github.com/BreakZero/EasyWallet-Multiplatform/actions/workflows/CheckCodeStyle.yml)

## 项目简介

EasyWallet是一个基于Kotlin Multiplatform开发的去中心化钱包应用，支持Android和iOS平台。项目采用Clean Architecture架构设计，所有钱包相关数据都本地化存储，使用用户自定义RPC节点确保更高的可信度和隐私性。

### 核心特性

- 🔐 **助记词管理**: 支持助记词导入和生成方式创建钱包
- 🌐 **多链支持**: 目前支持Ethereum链，架构设计支持多链扩展
- 📱 **跨平台**: 基于Kotlin Multiplatform，支持Android和iOS
- 🔒 **本地化存储**: 所有敏感数据本地存储，保护用户隐私
- 🎨 **现代UI**: 使用Jetpack Compose (Android) 和 SwiftUI (iOS)
- 📊 **行情数据**: 集成CoinGecko API查看加密货币行情
- 📰 **资讯浏览**: 接入BlockChair接口提供区块链资讯

## 项目架构

### 整体架构

项目采用Clean Architecture + MVI/MVVM架构模式，分为以下主要模块：

```
EasyWallet-Multiplatform/
├── composeApp/              # Android应用主模块
├── iosApp/                  # iOS应用主模块
├── platform/                # 共享业务逻辑层
│   ├── model/              # 数据模型层
│   ├── domain/             # 业务逻辑层
│   ├── data/               # 数据访问层
│   ├── network/            # 网络请求层
│   ├── database/           # 本地数据库层
│   └── datastore/          # 数据存储层
├── build-logic/            # 构建逻辑配置
└── configs/                # 配置文件
```

### 架构设计图

![architecture.png](screenshots%2Farchitecture.png)

### 模块详细说明

#### 1. Platform层 (共享业务逻辑)

**model模块**
- 定义所有数据模型和实体类
- 包含网络请求、数据库、UI展示等各层的数据模型
- 使用Kotlinx Serialization进行序列化

**domain模块**
- 包含业务逻辑和用例(Use Cases)
- 定义Repository接口
- 处理业务规则和数据转换

**data模块**
- 实现Repository接口
- 协调网络层、数据库层、数据存储层
- 处理数据源切换和缓存策略

**network模块**
- 使用Ktor进行网络请求
- 支持Android (OkHttp) 和 iOS (Darwin) 平台
- 集成API密钥管理(BuildKonfig)

**database模块**
- 使用SQLDelight进行本地数据库管理
- 支持多链数据存储
- 提供协程扩展支持

**datastore模块**
- 使用DataStore进行轻量级数据存储
- 存储用户偏好设置和应用配置

#### 2. ComposeApp模块 (Android应用)

**架构模式**: MVI (Model-View-Intent)
- **Model**: 来自platform层的数据模型
- **View**: Jetpack Compose UI组件
- **Intent**: 用户操作和状态管理

**主要特性**:
- 使用Jetpack Compose构建现代化UI
- 集成Koin进行依赖注入
- 支持Material Design 3
- 使用Navigation Compose进行页面导航

#### 3. iOSApp模块 (iOS应用)

**架构模式**: MVVM (Model-View-ViewModel)
- 使用SwiftUI构建用户界面
- 通过Kotlin Multiplatform共享业务逻辑
- 目前处于开发阶段，功能有限

## 技术栈与第三方库

### 核心框架

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin Multiplatform | 2.2.10 | 跨平台开发框架 |
| Jetpack Compose | 1.8.2 | Android UI框架 |
| SwiftUI | - | iOS UI框架 |
| Kotlin Coroutines | 1.10.2 | 异步编程 |

### 网络与数据

| 库名 | 版本 | 用途 |
|------|------|------|
| Ktor | 3.3.0 | HTTP客户端 |
| Kotlinx Serialization | 1.9.0 | JSON序列化 |
| SQLDelight | 2.1.0 | 本地数据库 |
| DataStore | 1.1.7 | 轻量级数据存储 |
| Paging3 | 3.3.0-alpha02 | 分页加载 |

### 依赖注入与架构

| 库名 | 版本 | 用途 |
|------|------|------|
| Koin | 4.1.1 | 依赖注入框架 |
| Navigation Compose | 2.9.0-rc02 | 页面导航 |
| Lifecycle ViewModel | 2.9.3 | 生命周期管理 |

### UI与图像

| 库名 | 版本 | 用途 |
|------|------|------|
| Coil | 3.3.0 | 图像加载 |
| Vico | 2.1.3 | 图表绘制 |
| QR Kit | 3.1.3 | 二维码生成/扫描 |
| Haze | 1.6.10 | 视觉效果 |

### 区块链与加密

| 库名 | 版本 | 用途 |
|------|------|------|
| Wallet Core | 4.3.9 | 区块链钱包核心功能 |
| BigNum | 0.3.10 | 大数运算 |

### 开发工具

| 工具 | 版本 | 用途 |
|------|------|------|
| Ktlint | 13.1.0 | 代码格式化 |
| BuildKonfig | 0.17.1 | 构建配置生成 |
| Kermit | 2.0.8 | 日志记录 |

## 项目结构详解

### 目录结构

```
EasyWallet-Multiplatform/
├── .github/                 # GitHub Actions配置
├── .githooks/              # Git钩子脚本
├── .gradle/                # Gradle缓存
├── .idea/                  # IDE配置
├── build-logic/            # 自定义构建逻辑
│   ├── convention/         # Gradle约定插件
│   └── building.versions.toml  # 版本管理
├── composeApp/             # Android应用
│   ├── src/
│   │   ├── androidMain/    # Android特定代码
│   │   ├── commonMain/     # 共享代码
│   │   └── iosMain/        # iOS特定代码
│   └── build.gradle.kts
├── iosApp/                 # iOS应用
│   ├── iosApp/            # iOS项目文件
│   ├── Podfile            # CocoaPods依赖
│   └── iosApp.xcodeproj/  # Xcode项目
├── platform/              # 共享业务逻辑
│   ├── model/             # 数据模型
│   ├── domain/            # 业务逻辑
│   ├── data/              # 数据访问
│   ├── network/           # 网络请求
│   ├── database/          # 数据库
│   └── datastore/         # 数据存储
├── configs/               # 配置文件
│   ├── package_read.properties  # GitHub包认证
│   └── apikeys.properties      # API密钥配置
├── screenshots/           # 应用截图
├── scripts/               # 构建脚本
├── keystore/              # 签名密钥
├── build.gradle.kts       # 根构建脚本
├── settings.gradle.kts    # 项目设置
├── gradle.properties      # Gradle属性
└── libs.versions.toml     # 依赖版本管理
```

### 关键配置文件

#### 1. 依赖版本管理 (libs.versions.toml)
统一管理所有第三方库的版本，确保依赖一致性。

#### 2. 构建逻辑 (build-logic/)
自定义Gradle插件，简化各模块的构建配置。

#### 3. API密钥配置
- `configs/package_read.properties`: GitHub包认证
- `configs/apikeys.properties`: 第三方API密钥

## 开发环境配置

### 前置要求

- Android Studio Hedgehog 2023.1.1+
- Xcode 15.0+
- Kotlin 2.2.10+
- Gradle 8.12.2+

### 配置步骤

1. **克隆项目**
```bash
git clone https://github.com/BreakZero/EasyWallet-KMP.git
cd EasyWallet-Multiplatform
```

2. **配置GitHub认证**
在`configs/`目录下创建`package_read.properties`文件：
```properties
gpr.name=Your Github Name
gpr.key=Your Github token
```

3. **配置API密钥**
在`configs/`目录下创建`apikeys.properties`文件：
```properties
etherscan=YOUR_ETHERSCAN_API_KEY
coingecko=YOUR_COINGECKO_API_KEY
opensea=YOUR_OPENSEA_API_KEY
```

4. **生成构建配置**
```bash
./gradlew -p platform generateBuildKonfig
```

5. **构建项目**
```bash
# Android
./gradlew :composeApp:assembleDebug

# iOS
cd iosApp && pod install
# 然后在Xcode中打开iosApp.xcworkspace
```

## 功能模块

### 已实现功能

- ✅ 助记词导入/生成钱包
- ✅ Ethereum链资产管理
- ✅ 本地数据存储
- ✅ 多链数据库架构
- ✅ 现代UI界面
- ✅ 依赖注入架构

### 开发中/计划的功能

- 🚧 iOS端完整功能
- 🚧 更多区块链支持
- 🚧 交易记录查看
- 🚧 自定义Token添加
- 🚧 高级安全功能

## 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 致谢

- [Trust Wallet Core](https://github.com/trustwallet/wallet-core) - 区块链钱包核心功能
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) - 跨平台开发框架
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - 现代Android UI框架
- [CoinGecko](https://www.coingecko.com/) - 加密货币行情数据
- [BlockChair](https://blockchair.com/) - 区块链数据服务

## 联系方式

- 项目链接: [https://github.com/BreakZero/EasyWallet-KMP](https://github.com/BreakZero/EasyWallet-KMP)
- 问题反馈: [Issues](https://github.com/BreakZero/EasyWallet-KMP/issues)

---

**注意**: 本项目仍在开发中，部分功能可能不稳定。请在生产环境中谨慎使用。
EOF