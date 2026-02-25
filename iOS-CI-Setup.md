# iOS CI 配置说明

本文档说明如何配置 iOS 测试包打包的 GitHub Actions CI。

## 前置要求

1. Apple Developer 账号
2. iOS 开发证书（Development Certificate）
3. Provisioning Profile（开发或 Ad-hoc）
4. 已配置好的 Xcode 项目

## GitHub Secrets 配置

需要在 GitHub 仓库设置中添加以下 Secrets（Settings → Secrets and variables → Actions）：

### 1. IOS_CERTIFICATE_BASE64

iOS 签名证书（.p12 格式）的 Base64 编码。

**生成步骤：**

```bash
# 从 Keychain 导出证书为 .p12 文件（需要设置密码）
# 打开 Keychain Access → 我的证书 → 右键点击证书 → 导出

# 转换为 Base64
base64 -i Certificates.p12 | pbcopy
```

将复制的内容粘贴到 GitHub Secret `IOS_CERTIFICATE_BASE64` 中。

### 2. IOS_CERTIFICATE_PASSWORD

导出 .p12 证书时设置的密码。

### 3. KEYCHAIN_PASSWORD

用于 CI 临时 Keychain 的密码，可以设置为任意强密码（如随机生成的字符串）。

### 4. IOS_PROVISIONING_PROFILE_BASE64

Provisioning Profile 文件的 Base64 编码。

**生成步骤：**

```bash
# 从 Apple Developer 下载 .mobileprovision 文件后
base64 -i YourProfile.mobileprovision | pbcopy
```

将复制的内容粘贴到 GitHub Secret `IOS_PROVISIONING_PROFILE_BASE64` 中。

### 5. IOS_EXPORT_OPTIONS_PLIST

ExportOptions.plist 文件的 Base64 编码。

**生成步骤：**

1. 编辑 `.github/ExportOptions.plist` 文件：
   - 替换 `YOUR_TEAM_ID` 为你的 Apple Team ID
   - 替换 `org.easy.wallet` 为你的应用 Bundle ID
   - 替换 `YOUR_PROVISIONING_PROFILE_NAME` 为你的 Provisioning Profile 名称
   - 根据需要修改 `method` 字段（development、ad-hoc、app-store、enterprise）

2. 转换为 Base64：

```bash
base64 -i .github/ExportOptions.plist | pbcopy
```

将复制的内容粘贴到 GitHub Secret `IOS_EXPORT_OPTIONS_PLIST` 中。

### 6. APIKEY_PROPERTIES

项目已有的 API keys 配置（与 Android CI 共用）。

## ExportOptions.plist 配置说明

### method 字段说明

- `development`: 开发测试版本，可安装到注册的开发设备
- `ad-hoc`: Ad-hoc 分发版本，可安装到注册的测试设备
- `app-store`: App Store 上架版本
- `enterprise`: 企业分发版本

### 获取必要信息

**Team ID:**
- 登录 [Apple Developer](https://developer.apple.com/account)
- 查看 Membership → Team ID

**Bundle ID:**
- 打开 Xcode 项目
- 选择 Target → General → Bundle Identifier

**Provisioning Profile 名称:**
- 登录 [Apple Developer](https://developer.apple.com/account)
- Certificates, IDs & Profiles → Profiles
- 查看 Profile 的名称

## Xcode 版本配置

CI 脚本默认使用 Xcode 15.4。如果需要修改版本：

1. 查看 GitHub Actions macOS runner 可用的 Xcode 版本：
   - [GitHub Actions Runner Images](https://github.com/actions/runner-images/blob/main/images/macos/macos-14-Readme.md)

2. 修改 `.github/workflows/iOS-Build.yml` 中的版本号：

```yaml
- name: Select Xcode version
  run: sudo xcode-select -s /Applications/Xcode_15.4.app/Contents/Developer
```

## 触发 CI

CI 将在以下情况自动触发：

- Push 到 `develop` 分支
- Push 到 `feature/*`、`bugfix/*`、`hotfix/*` 分支
- 手动触发（Actions → iOS Test Build → Run workflow）

## 构建产物

成功构建后，IPA 文件将作为 Artifact 上传，可在 Actions 页面下载：

- Artifact 名称：`iosApp-{run_number}`
- 保留天数：7 天
- 文件类型：`.ipa`

## 常见问题

### 1. 签名失败

检查：
- 证书是否过期
- Provisioning Profile 是否包含正确的证书
- Bundle ID 是否匹配
- Team ID 是否正确

### 2. CocoaPods 依赖安装失败

可能需要：
- 更新 Podfile.lock
- 检查 Pod 仓库可访问性
- 在 workflow 中添加 `--verbose` 标志查看详细日志

### 3. Kotlin 编译失败

检查：
- Gradle 配置是否正确
- JDK 版本是否匹配（当前使用 JDK 17）
- API keys 配置是否正确

### 4. Archive 失败

检查：
- Xcode 项目配置是否正确
- Code signing 设置
- Build settings 中的部署目标版本

## 调试建议

1. 在本地先测试完整的构建流程：

```bash
# 构建 Kotlin 共享模块
./gradlew :composeApp:compileKotlinIosArm64

# 安装 CocoaPods 依赖
cd iosApp && pod install

# Archive
xcodebuild -workspace iosApp.xcworkspace \
  -scheme iosApp \
  -configuration Debug \
  -destination 'generic/platform=iOS' \
  -archivePath build/iosApp.xcarchive \
  archive

# Export
xcodebuild -exportArchive \
  -archivePath build/iosApp.xcarchive \
  -exportOptionsPlist ExportOptions.plist \
  -exportPath build/output
```

2. 查看 GitHub Actions 日志获取详细错误信息

3. 使用 `workflow_dispatch` 手动触发测试 CI

## 进一步优化

### 1. 添加版本号自动递增

可以在 CI 中自动更新 `CFBundleVersion`：

```yaml
- name: Increment build number
  run: |
    cd iosApp
    agvtool new-version -all ${{ github.run_number }}
```

### 2. 使用 Fastlane

对于更复杂的打包流程，建议使用 Fastlane 自动化工具。

### 3. 分发到测试平台

可以添加步骤将 IPA 上传到 TestFlight 或其他分发平台（如 Firebase App Distribution）。

## 参考资料

- [Apple Developer Documentation](https://developer.apple.com/documentation/)
- [GitHub Actions - Building iOS apps](https://docs.github.com/en/actions/deployment/deploying-xcode-applications/installing-an-apple-certificate-on-macos-runners-for-xcode-development)
- [xcodebuild Manual](https://developer.apple.com/library/archive/technotes/tn2339/_index.html)
