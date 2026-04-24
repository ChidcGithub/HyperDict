# ECDICT 词典数据库配置指南

由于网络限制，自动下载可能失败。请按以下步骤手动配置：

## 方法 1: 手动下载（推荐）

### 步骤 1: 下载数据库文件

访问以下任一地址下载 ECDICT 数据库：

**官方 GitHub Releases**:
- https://github.com/skywind3000/ECDICT/releases
- 下载 `ecdict-csv-28.zip` 或最新版本

**备用下载地址**:
- 百度网盘、阿里云盘等（搜索 "ECDICT 下载"）
- 国内镜像站点

### 步骤 2: 解压并转换

1. 将下载的 ZIP 文件放到项目根目录
2. 解压得到 `ecdict.csv` 文件
3. 运行转换脚本：

**Windows**:
```bash
python download_ecdict.py
```

**Linux/Mac**:
```bash
python3 download_ecdict.py
```

脚本会自动检测已存在的 CSV 文件并跳过下载步骤。

### 步骤 3: 验证

检查文件是否存在：
```
app/src/main/assets/dictionary.db
```

文件大小应该在 500MB 左右。

## 方法 2: 使用预构建的 SQLite 数据库

如果您找到了预构建的 SQLite 版本：

1. 下载 `.db` 或 `.sqlite` 文件
2. 重命名为 `dictionary.db`
3. 直接放到 `app/src/main/assets/` 目录

## 方法 3: 使用简化版数据库

如果完整数据库太大，可以使用精简版：

1. 下载完整数据库后
2. 使用 SQLite 工具删除不常用词汇：

```sql
-- 只保留常用词汇（词频排名前 50000）
DELETE FROM stardict WHERE bnc > 50000;

-- 或只保留特定标签的词汇
DELETE FROM stardict WHERE tag NOT LIKE '%CET%' AND tag NOT LIKE '%TOEFL%';

-- 压缩数据库
VACUUM;
```

## 数据库结构要求

确保数据库包含以下表结构：

```sql
CREATE TABLE stardict (
    word TEXT PRIMARY KEY,
    phonetic TEXT,
    definition TEXT,
    translation TEXT,
    pos TEXT,
    collins INTEGER,
    oxford INTEGER,
    tag TEXT,
    bnc INTEGER,
    frq INTEGER,
    exchange TEXT,
    detail TEXT,
    audio TEXT
);
```

最少需要包含：`word`, `translation`, `definition` 字段。

## 故障排除

### 问题 1: SSL 证书错误
- 使用浏览器手动下载
- 或使用下载工具（IDM、迅雷等）

### 问题 2: 下载速度慢
- 使用国内镜像或网盘
- 使用代理或 VPN

### 问题 3: 转换失败
- 确保 Python 3.7+ 已安装
- 检查 CSV 文件是否完整
- 查看错误信息并修复

### 问题 4: 应用崩溃
- 确保文件名为 `dictionary.db`
- 确保文件在 `app/src/main/assets/` 目录
- 检查数据库表结构是否正确

## 联系方式

如果遇到问题，请在 GitHub 提交 Issue：
https://github.com/ChidcGithub/HyperDict/issues

## 相关链接

- ECDICT 官方仓库: https://github.com/skywind3000/ECDICT
- SQLite 官网: https://www.sqlite.org/
- DB Browser for SQLite: https://sqlitebrowser.org/
