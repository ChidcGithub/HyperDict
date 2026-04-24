# 离线词典数据库集成指南

## 快速开始

### 步骤 1: 下载 ECDICT 数据库

访问以下链接下载词典数据：

**推荐来源: ECDICT**
- GitHub: https://github.com/skywind3000/ECDICT
- 词条数: 377万+
- 许可证: MIT
- 语言: 英中双语

### 步骤 2: 准备数据库文件

#### 选项 A: 使用现成的 SQLite 版本（如果有）

如果项目提供了 SQLite 版本的文件，直接下载使用。

#### 选项 B: 从 CSV 转换为 SQLite

1. 下载 `ecdict.csv` 文件
2. 使用以下 Python 脚本转换为 SQLite：

```python
import sqlite3
import csv

# 创建数据库连接
conn = sqlite3.connect('dictionary.db')
c = conn.cursor()

# 创建表
c.execute('''CREATE TABLE stardict
             (word TEXT PRIMARY KEY,
              definition TEXT,
              translation TEXT,
              phonetic TEXT,
              tag TEXT,
              bnc INTEGER,
              frq INTEGER,
              exchange TEXT)''')

# 读取 CSV 并插入数据
with open('ecdict.csv', 'r', encoding='utf-8') as f:
    reader = csv.reader(f)
    header = next(reader)  # 跳过标题行

    count = 0
    for row in reader:
        if len(row) >= 8:
            c.execute('INSERT INTO stardict VALUES (?,?,?,?,?,?,?,?)',
                     (row[0].lower().strip(),  # word
                      row[1],                   # definition
                      row[2],                   # translation
                      row[3],                   # phonetic
                      row[4],                   # tag
                      int(row[5]) if row[5].strip() else None,  # bnc
                      int(row[6]) if row[6].strip() else None,  # frq
                      row[7]))                  # exchange
            count += 1
            if count % 10000 == 0:
                print(f'Inserted {count} entries...')

conn.commit()
print(f'Done! Total: {count} entries')
conn.close()
```

### 步骤 3: 放入项目

将生成的 `dictionary.db` 文件复制到：

```
app/src/main/assets/dictionary.db
```

### 步骤 4: 构建和测试

1. 打开 Android Studio
2. 同步 Gradle
3. 运行应用
4. 尝试查词 - 应该能立即显示中文翻译！

## 验证集成

启动应用后：

1. 输入一个英文单词（如 "hello"）
2. 应该能看到：
   - 搜索建议列表
   - 中文翻译
   - 顶部显示离线图标（云朵带斜杠）

## 优化数据库大小

如果您想减小数据库大小，可以：

### 选项 1: 只保留常用词汇

```sql
-- 删除词频排名 50000 之后的词汇
DELETE FROM stardict WHERE bnc > 50000;

-- 或只保留特定标签的词汇
DELETE FROM stardict WHERE tag NOT LIKE '%CET-4%' AND tag NOT LIKE '%CET-6%';
```

### 选项 2: 使用精简版

ECDICT 提供了不同大小的数据集，选择适合您需求的版本。

## 故障排除

### 问题: 应用崩溃，提示找不到数据库

**解决方案**:
- 确保文件名为 `dictionary.db`
- 确保文件放在 `app/src/main/assets/` 目录下
- 检查文件大小是否合理（应该 > 1MB）

### 问题: 查词无结果

**解决方案**:
- 检查数据库表名是否为 `stardict`
- 检查是否有 `word` 和 `translation` 字段
- 确保单词字段为小写

### 问题: 数据库太大导致 APK 过大

**解决方案**:
- 使用上述 SQL 命令精简数据库
- 或将数据库放在外部服务器，首次使用时下载

## 数据库结构详情

### stardict 表字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| word | TEXT | 单词（小写），主键 |
| definition | TEXT | 英文定义 |
| translation | TEXT | 中文翻译 |
| phonetic | TEXT | 音标（如 /həˈloʊ/） |
| tag | TEXT | 标签（如 CET-4, GRE, IELTS） |
| bnc | INTEGER | 英国国家语料库词频排名 |
| frq | INTEGER | 词频统计 |
| exchange | TEXT | 词形变化（如复数、过去式） |

## 自定义词典

如果您想使用其他词典数据源：

1. 确保表名为 `stardict`
2. 至少包含 `word` 和 `translation` 字段
3. 单词字段应为小写
4. 将文件命名为 `dictionary.db`

## 性能优化建议

1. **创建索引**: 数据库已自动为主键创建索引
2. **预加载**: 可在应用启动时预加载常用词
3. **缓存**: 应用已有 7 天在线缓存机制

## 许可证说明

- ECDICT 采用 MIT 许可证，可自由用于商业和非商业项目
- 使用时请遵守许可证条款
