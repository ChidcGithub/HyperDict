# HyperDict - 超级词典

<div align="center">

![HyperDict Logo](https://img.shields.io/badge/HyperDict-超级词典-6750A4?style=for-the-badge)

[![Android CI/CD](https://github.com/ChidcGithub/HyperDict/actions/workflows/android-build.yml/badge.svg)](https://github.com/ChidcGithub/HyperDict/actions/workflows/android-build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=26)

为语言学习者、翻译工作者、阅读爱好者打造的新一代词典应用。

[下载 APK](https://github.com/ChidcGithub/HyperDict/releases) | [功能特性](#功能特性) | [构建说明](#构建说明)

</div>

---

## ✨ 功能特性

### 📚 离线词典
- ✅ 内建英中双语词典（ECDICT，377万+词条）
- ✅ 无需网络即可查词
- ✅ 实时搜索建议和自动补全
- ✅ 快速响应，毫秒级查询

### 🌐 在线增强
- ✅ 在线 API 补充详细释义（英文）
- ✅ 智能本地缓存（7 天有效期）
- ✅ 离线降级（网络失败时使用缓存）
- ✅ 数据源指示（在线/离线）

### 🎨 用户体验
- ✅ Material Expressive 3 设计语言
- ✅ 流畅的动画和过渡效果
- ✅ 深色模式支持
- ✅ 动态色彩系统
- ✅ 清晰的卡片化布局
- ✅ 一键清除搜索

## 🏗️ 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **设计规范**: Material Design 3 (Expressive)
- **架构模式**: MVVM
- **最低支持版本**: Android 8.0 (API 26)
- **依赖注入**: Service Locator 模式
- **数据库**: SQLite + Room
- **网络请求**: Retrofit + OkHttp
- **异步处理**: Kotlin Coroutines

## 📐 架构

采用 MVVM (Model-View-ViewModel) 架构模式：

```
UI Layer (Compose) → ViewModel → Repository → Data Layer
                                              ├─ Offline DB (SQLite)
                                              ├─ Room Cache
                                              └─ Retrofit API
```

### 数据流

```
用户查词 → ViewModel → Repository
                        ├─ 1. 离线词典 (优先)
                        ├─ 2. Room 缓存 (次选)
                        ├─ 3. 在线 API (最后)
                        └─ 4. 过期缓存 (降级)
```

## 📦 数据源

### 离线词典（主）
- **ECDICT** - 免费英中词典数据库
- 下载地址: https://github.com/skywind3000/ECDICT
- 许可证: MIT
- 词条数: 377万+

### 在线 API（辅）
- [Free Dictionary API](https://dictionaryapi.dev/) - 免费在线词典

## 🚀 构建说明

### 前置要求

- Android Studio Hedgehog (2023.1.1) 或更高版本
- JDK 17
- Android SDK (API 26+)

### 步骤 1: 克隆项目

```bash
git clone https://github.com/ChidcGithub/HyperDict.git
cd HyperDict
```

### 步骤 2: 下载离线词典数据库

从以下来源获取词典数据库：

- **ECDICT** (推荐): https://github.com/skywind3000/ECDICT
  - 下载 SQLite 版本或 CSV 转换
  - 包含 377万+ 英中双语词条

详细说明请参考 [DICTIONARY_SETUP.md](DICTIONARY_SETUP.md)

### 步骤 3: 放入项目

将数据库文件重命名为 `dictionary.db` 并放到：
```
app/src/main/assets/dictionary.db
```

### 步骤 4: 构建运行

1. 使用 Android Studio 打开项目
2. 等待 Gradle 同步完成
3. 运行到模拟器或真机

或使用命令行：

```bash
# Debug 版本
./gradlew assembleDebug

# Release 版本
./gradlew assembleRelease
```

## 📱 截图

> 待添加应用截图

## 🔄 CI/CD

项目使用 GitHub Actions 自动构建和发布：

- ✅ 自动构建 Debug 和 Release APK
- ✅ 推送 tag 时自动创建 Release
- ✅ 上传 APK 到 GitHub Releases

### 创建新版本

```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

## 🗺️ 后续规划

- [ ] 生词本/收藏夹
- [ ] 搜索历史记录
- [ ] 发音播放（TTS）
- [ ] 更多词典数据源
- [ ] 单词卡片学习模式
- [ ] 导出/导入生词本
- [ ] 桌面小部件
- [ ] 分享功能

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

## 🙏 致谢

- [ECDICT](https://github.com/skywind3000/ECDICT) - 提供离线词典数据
- [Free Dictionary API](https://dictionaryapi.dev/) - 提供在线词典 API
- [Material Design 3](https://m3.material.io/) - 设计规范

---

<div align="center">

Made with ❤️ by [ChidcGithub](https://github.com/ChidcGithub)

</div>
