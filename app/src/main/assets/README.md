# 词典数据库文件

请将下载的词典数据库文件放在此目录。

## 推荐词典

### ECDICT (推荐 - 英中双语)
- **下载地址**: https://github.com/skywind3000/ECDICT
- **文件格式**: SQLite
- **词条数**: 377万+
- **许可证**: MIT
- **使用方法**:
  1. 从 GitHub 下载 ECDICT 的 SQLite 版本
  2. 将文件重命名为 `dictionary.db`
  3. 放在 `app/src/main/assets/` 目录下

### 备选方案

如果您想使用其他词典，只需将数据库文件命名为 `dictionary.db` 并放在此目录即可。

## 数据库结构要求

如果您的自定义数据库不是 ECDICT 格式，请确保包含以下字段：

```sql
CREATE TABLE IF NOT EXISTS stardict (
    word TEXT PRIMARY KEY,      -- 单词（小写）
    definition TEXT,            -- 定义/释义
    translation TEXT,           -- 翻译（可选）
    phonetic TEXT,              -- 音标（可选）
    tag TEXT,                   -- 标签（如 CET-4, GRE 等）
    bnc INTEGER,                -- 词频排名（可选）
    frq INTEGER,                -- 词频（可选）
    exchange TEXT               -- 词形变化（可选）
);
```

## 如何获取 ECDICT SQLite

1. 访问: https://github.com/skywind3000/ECDICT
2. 下载 `ecdict.csv` 或使用提供的转换工具生成 SQLite
3. 或者下载社区提供的现成 SQLite 版本

## 手动转换 CSV 为 SQLite

如果您只有 CSV 文件，可以使用以下 Python 脚本转换：

```python
import sqlite3
import csv

conn = sqlite3.connect('dictionary.db')
c = conn.cursor()

c.execute('''CREATE TABLE stardict
             (word TEXT PRIMARY KEY, definition TEXT, translation TEXT,
              phonetic TEXT, tag TEXT, bnc INTEGER, frq INTEGER, exchange TEXT)''')

with open('ecdict.csv', 'r', encoding='utf-8') as f:
    reader = csv.reader(f)
    next(reader)  # Skip header
    for row in reader:
        if len(row) >= 8:
            c.execute('INSERT INTO stardict VALUES (?,?,?,?,?,?,?,?)',
                     (row[0].lower(), row[1], row[2], row[3], row[4],
                      int(row[5]) if row[5] else None,
                      int(row[6]) if row[6] else None, row[7]))

conn.commit()
conn.close()
```
