#!/usr/bin/env python3
"""
ECDICT Database Downloader and Converter
自动下载并转换 ECDICT 词典数据库为 SQLite 格式
"""

import os
import sys
import sqlite3
import csv
import urllib.request
import zipfile
import ssl
from pathlib import Path

# 禁用 SSL 证书验证（仅用于下载）
ssl._create_default_https_context = ssl._create_unverified_context

# 配置
ECDICT_CSV_URL = "https://github.com/skywind3000/ECDICT/releases/download/1.0.28/ecdict-csv-28.zip"
OUTPUT_DB = "dictionary.db"
TARGET_DIR = "app/src/main/assets"

def download_file(url, filename):
    """下载文件并显示进度"""
    print(f"正在下载 {filename}...")
    print(f"URL: {url}")

    def report_progress(block_num, block_size, total_size):
        downloaded = block_num * block_size
        percent = min(downloaded * 100 / total_size, 100)
        sys.stdout.write(f"\r进度: {percent:.1f}% ({downloaded / 1024 / 1024:.1f}MB / {total_size / 1024 / 1024:.1f}MB)")
        sys.stdout.flush()

    try:
        urllib.request.urlretrieve(url, filename, report_progress)
        print("\n下载完成！")
        return True
    except Exception as e:
        print(f"\n下载失败: {e}")
        return False

def extract_zip(zip_path, extract_to="."):
    """解压 ZIP 文件"""
    print(f"正在解压 {zip_path}...")
    try:
        with zipfile.ZipFile(zip_path, 'r') as zip_ref:
            zip_ref.extractall(extract_to)
        print("解压完成！")
        return True
    except Exception as e:
        print(f"解压失败: {e}")
        return False

def convert_csv_to_sqlite(csv_file, db_file):
    """将 CSV 转换为 SQLite 数据库"""
    print(f"正在转换 {csv_file} 为 SQLite 数据库...")

    conn = sqlite3.connect(db_file)
    c = conn.cursor()

    # 创建表
    c.execute('''CREATE TABLE IF NOT EXISTS stardict (
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
    )''')

    # 读取并插入数据
    try:
        with open(csv_file, 'r', encoding='utf-8') as f:
            reader = csv.reader(f)
            header = next(reader)  # 跳过标题行
            print(f"CSV 列: {header}")

            count = 0
            batch = []
            batch_size = 1000

            for row in reader:
                if len(row) >= 4:  # 至少需要 word, phonetic, definition, translation
                    # 填充缺失的列
                    while len(row) < 13:
                        row.append('')

                    batch.append((
                        row[0].lower().strip() if row[0] else '',  # word
                        row[1] if row[1] else '',  # phonetic
                        row[2] if row[2] else '',  # definition
                        row[3] if row[3] else '',  # translation
                        row[4] if len(row) > 4 and row[4] else '',  # pos
                        int(row[5]) if len(row) > 5 and row[5].strip().isdigit() else 0,  # collins
                        int(row[6]) if len(row) > 6 and row[6].strip().isdigit() else 0,  # oxford
                        row[7] if len(row) > 7 and row[7] else '',  # tag
                        int(row[8]) if len(row) > 8 and row[8].strip().isdigit() else 0,  # bnc
                        int(row[9]) if len(row) > 9 and row[9].strip().isdigit() else 0,  # frq
                        row[10] if len(row) > 10 and row[10] else '',  # exchange
                        row[11] if len(row) > 11 and row[11] else '',  # detail
                        row[12] if len(row) > 12 and row[12] else '',  # audio
                    ))

                    count += 1

                    if len(batch) >= batch_size:
                        c.executemany(
                            'INSERT OR REPLACE INTO stardict VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)',
                            batch
                        )
                        conn.commit()
                        batch = []
                        sys.stdout.write(f"\r已插入 {count:,} 条记录...")
                        sys.stdout.flush()

            # 插入剩余的记录
            if batch:
                c.executemany(
                    'INSERT OR REPLACE INTO stardict VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)',
                    batch
                )
                conn.commit()

            print(f"\n转换完成！总共插入 {count:,} 条记录")

    except Exception as e:
        print(f"\n转换失败: {e}")
        conn.close()
        return False

    # 创建索引以提高查询速度
    print("正在创建索引...")
    c.execute('CREATE INDEX IF NOT EXISTS idx_word ON stardict(word)')
    conn.commit()
    conn.close()

    print("数据库创建完成！")
    return True

def move_to_assets(db_file, target_dir):
    """将数据库移动到 assets 目录"""
    target_path = Path(target_dir)
    target_path.mkdir(parents=True, exist_ok=True)

    target_file = target_path / db_file
    print(f"正在移动数据库到 {target_file}...")

    try:
        import shutil
        shutil.move(db_file, str(target_file))
        print("移动完成！")
        return True
    except Exception as e:
        print(f"移动失败: {e}")
        return False

def main():
    print("=" * 60)
    print("ECDICT 词典数据库自动下载和转换工具")
    print("=" * 60)
    print()

    # 检查是否已存在数据库
    target_db = Path(TARGET_DIR) / OUTPUT_DB
    if target_db.exists():
        response = input(f"{target_db} 已存在，是否覆盖？(y/n): ")
        if response.lower() != 'y':
            print("操作已取消")
            return

    # 步骤 1: 下载
    zip_file = "ecdict.zip"
    if not os.path.exists(zip_file):
        if not download_file(ECDICT_CSV_URL, zip_file):
            print("下载失败，请手动下载并放置在当前目录")
            print(f"下载地址: {ECDICT_CSV_URL}")
            return
    else:
        print(f"{zip_file} 已存在，跳过下载")

    # 步骤 2: 解压
    csv_file = "ecdict.csv"
    if not os.path.exists(csv_file):
        if not extract_zip(zip_file):
            return
    else:
        print(f"{csv_file} 已存在，跳过解压")

    # 步骤 3: 转换
    if not convert_csv_to_sqlite(csv_file, OUTPUT_DB):
        return

    # 步骤 4: 移动到 assets
    if not move_to_assets(OUTPUT_DB, TARGET_DIR):
        return

    # 清理临时文件
    print("\n正在清理临时文件...")
    try:
        if os.path.exists(zip_file):
            os.remove(zip_file)
        if os.path.exists(csv_file):
            os.remove(csv_file)
        print("清理完成！")
    except Exception as e:
        print(f"清理失败: {e}")

    print("\n" + "=" * 60)
    print("✅ 所有步骤完成！")
    print(f"数据库已放置在: {target_db}")
    print("现在可以构建并运行应用了！")
    print("=" * 60)

if __name__ == "__main__":
    main()
