[![Github Release](https://github.com/BreakZero/EasyWallet-KMP/actions/workflows/Release.yml/badge.svg)](https://github.com/BreakZero/EasyWallet-KMP/actions/workflows/Release.yml)

## 关于EasyWallet - (WIP)
EasyWallet是一个去中心化钱包，计划所有关于钱包方面的资产都将本地化，使用用户自定义RPC节点达到更高的可信度。
当然RPC接口有一定的局限性，所以有些功能比如交易记录等就只能通过浏览器查看了。用户亦可自定义添加Token支持(仅支持EVM链)。
接入[BlockChair](https://blockchair.com)讯息接口提供消息浏览，关注时文时事。[coingecko](https://www.coingecko.com/)查看行情，了解行情。

### 功能和进度
目前支持助剂词导入和生成方式创建钱包，已支持Ethereum链资产使用
在钱包方面，所有数据都是本地化实现。目前数据库方面设计已完成多链支持基本工作。更多功能计划中...

### 结构和架构
这是一个Kotlin Multiplatform Project, 支持iOS和Android端。结构上由3大部分组成，分别是 _**platform**_, _**Wallet-Android**_, _**Wallet-iOS**_
* #### platform
  _platform_ 是双端共享业务逻辑处理代码，其主要职责是将服务端接口数据通过逻辑处理包装成业务领域Model给UI层使用或者接收用户操作后触发更改云端/本地存储数据.
  在实现上符合Clean Architecture架构，主要包含Model、Data、Domain层
* #### Wallet-Android
  _Wallet-Android_ 是Android界面实现, 使用Jetpack Compose + ViewModel开发
  实现上使用MVI架构模式，其中Model来自 _platform_ 拿到数据结果
* #### Wallet-iOS (Planning)
  _Wallet-iOS_ 是iOS端UI界面实现，使用SwiftUI开发，目前iOS只有简单的基础UI界面，支持功能不多，在架构上使用的是MVVM架构模式

基本架构图如下：
![architecture.png](screenshots%2Farchitecture.png)

### 需要配置

由于项目中依赖[wallet-core](https://github.com/trustwallet/wallet-core)，
其使用`GitHub packages`进行包管理，所以想拉取依赖库，需要使用到`github token`。
为了保证所有依赖都能成功下载，需要创建一个文件命名为`package_read.properties`放到`configs`目录下，并填入以下内容：

```properties
gpr.name=Your Github Name
gpr.key=Your Github token
```

更多请看[library-dependency](https://developer.trustwallet.com/developer/wallet-core/integration-guide/android-guide#adding-library-dependency)

### 各链数据接口

- 旧版本：
  ~~不同链的数据来源也不同，所以需要不同apikey，可以查看[NetworkModule.kt](platform%2Fnetwork%2Fsrc%2FcommonMain%2Fkotlin%2Fcom%2Feasy%2Fwallet%2Fnetwork%2Fdi%2FNetworkModule.kt)
  中补上。~~

- 新版本：
  使用本地配置来存储apikey，需要在`configs`文件夹下新建一个名为`apikeys.properties`的文件
  _目前使用到3个平台的apikey_

```properties
etherscan=XWNRRIQY******K7YP4F6
coingecko=CG-8******AwuyrcgDPL
opensea=008e**********91dd6564880a
```

准备好之后，可执行下面Task生成BuildKonfig类

```shell
./gradlew -p platform generateBuildKonfig
```

### 开源依赖库

- [Skie](https://github.com/touchlab/SKIE)
- [Wallet-Core](https://github.com/trustwallet/wallet-core)
- [Coroutine](https://github.com/Kotlin/kotlinx.coroutines) + [Flow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
  用于异步
- [koin](https://github.com/InsertKoinIO/koin) 依赖注入
- [ktor](https://github.com/ktorio/ktor)
- [sqldelight](https://github.com/cashapp/sqldelight)
- [coil](https://github.com/coil-kt/coil)
- [Paging3](https://github.com/cashapp/multiplatform-paging)
- [BuildKonfig](https://github.com/yshrsmz/BuildKonfig)

### UI展示

- Light Theme
  |                                                                             |                                                                             |                                                                             |
  | --------------------------------------------------------------------------- | --------------------------------------------------------------------------- | --------------------------------------------------------------------------: |
  | ![Screenshot_20240517_100227.png](screens%2FScreenshot_20240517_100227.png) | ![Screenshot_20240517_100249.png](screens%2FScreenshot_20240517_100249.png) | ![Screenshot_20240517_100302.png](screens%2FScreenshot_20240517_100302.png) |
  | ![Screenshot_20240517_100718.png](screens%2FScreenshot_20240517_100718.png) | ![Screenshot_20240517_100728.png](screens%2FScreenshot_20240517_100728.png) | ![Screenshot_20240517_100800.png](screens%2FScreenshot_20240517_100800.png) |
  | ![Screenshot_20240517_100819.png](screens%2FScreenshot_20240517_100819.png) | ![Screenshot_20240517_100847.png](screens%2FScreenshot_20240517_100847.png) | ![Screenshot_20240517_100923.png](screens%2FScreenshot_20240517_100923.png) |
  | ![Screenshot_20240517_101027.png](screens%2FScreenshot_20240517_101027.png) | ![Screenshot_20240517_101219.png](screens%2FScreenshot_20240517_101219.png) |                                                                             |

- iOS
  |                                                                       |                                                                       |                                                                       |
  | --------------------------------------------------------------------- | --------------------------------------------------------------------- | --------------------------------------------------------------------: |
  | ![Screenshot_01_iPhone_15.png](screens%2FScreenshot_01_iPhone_15.png) | ![Screenshot_02_iPhone_15.png](screens%2FScreenshot_02_iPhone_15.png) | ![Screenshot_03_iPhone_15.png](screens%2FScreenshot_03_iPhone_15.png) |

### 界面参考资源

- [Crypto-Wallet-Mobile-Ui-(Free)-(Community)](https://www.figma.com/file/sLqrdLp6vOedEnZgW1E3ze/Cryptooly---Crypto-Wallet-Mobile-Ui-(Free)-(Community)?type=design&mode=design)
- [CoinTrend](https://github.com/CoinTrend/CoinTrend)
