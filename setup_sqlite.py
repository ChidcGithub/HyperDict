#!/usr/bin/env python3
"""
ECDICT SQLite Database Setup
直接使用预构建的 SQLite 数据库
"""

import os
import sys
import zipfile
import shutil
import urllib.request
import ssl
from pathlib import Path

# 禁用 SSL 证书验证
ssl._create_default_https_context = ssl._create_unverified_context

# 配置
ECDICT_SQLITE_URL = "https://github.com/skywind3000/ECDICT/releases/download/1.0.28/ecdict-sqlite-28.zip"
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

def find_db_file():
    """查找解压后的数据库文件"""
    possible_names = ['stardict.db', 'ecdict.db', 'dictionary.db']
    for name in possible_names:
        if os.path.exists(name):
            return name

    # 在子目录中查找
    for root, dirs, files in os.walk('.'):
        for file in files:
            if file.endswith('.db'):
                return os.path.join(root, file)

    return None

def setup_database():
    """设置数据库"""
    print("=" * 60)
    print("ECDICT SQLite 数据库配置工具")
    print("=" * 60)
    print()

    # 检查是否已存在数据库
    target_path = Path(TARGET_DIR)
    target_path.mkdir(parents=True, exist_ok=True)
    target_db = target_path / OUTPUT_DB

    if target_db.exists():
        response = input(f"{target_db} 已存在，是否覆盖？(y/n): ")
        if response.lower() != 'y':
            print("操作已取消")
            return

    # 步骤 1: 检查是否已有 ZIP 文件
    zip_file = "ecdict-sqlite-28.zip"

    if not os.path.exists(zip_file):
        print("未找到 ZIP 文件，开始下载...")
        if not download_file(ECDICT_SQLITE_URL, zip_file):
            print("\n" + "=" * 60)
            print("⚠️  自动下载失败！")
            print("=" * 60)
            print("\n请手动下载：")
            print(f"1. 访问: https://github.com/skywind3000/ECDICT/releases")
            print(f"2. 下载: ecdict-sqlite-28.zip")
            print(f"3. 放到项目根目录: {os.getcwd()}")
            print(f"4. 重新运行此脚本")
            print("\n或者使用浏览器/下载工具下载后放到当前目录")
            return
    else:
        print(f"✓ 找到 {zip_file}")

    # 步骤 2: 解压
    print("\n正在解压...")
    if not extract_zip(zip_file):
        return

    # 步骤 3: 查找数据库文件
    print("\n正在查找数据库文件...")
    db_file = find_db_file()

    if not db_file:
        print("❌ 未找到数据库文件！")
        print("请检查 ZIP 文件内容")
        return

    print(f"✓ 找到数据库: {db_file}")

    # 步骤 4: 复制到 assets
    print(f"\n正在复制到 {target_db}...")
    try:
        shutil.copy2(db_file, str(target_db))
        print("✓ 复制完成！")
    except Exception as e:
        print(f"❌ 复制失败: {e}")
        return

    # 步骤 5: 验证
    db_size = target_db.stat().st_size / 1024 / 1024
    print(f"\n✓ 数据库大小: {db_size:.1f} MB")

    # 步骤 6: 清理
    response = input("\n是否清理临时文件？(y/n): ")
    if response.lower() == 'y':
        print("正在清理...")
        try:
            if os.path.exists(zip_file):
                os.remove(zip_file)
            if db_file and os.path.exists(db_file):
                os.remove(db_file)
            # 清理可能的解压目录
            for item in os.listdir('.'):
                if os.path.isdir(item) and 'ecdict' in item.lower():
                    shutil.rmtree(item)
            print("✓ 清理完成！")
        except Exception as e:
            print(f"清理失败: {e}")

    print("\n" + "=" * 60)
    print("✅ 配置完成！")
    print("=" * 60)
    print(f"\n数据库位置: {target_db}")
    print(f"数据库大小: {db_size:.1f} MB")
    print("\n现在可以构建并运行应用了！")
    print("\n构建命令:")
    print("  ./gradlew assembleDebug")
    print("\n或在 Android Studio 中直接运行")

if __name__ == "__main__":
    try:
        setup_database()
    except KeyboardInterrupt:
        print("\n\n操作已取消")
    except Exception as e:
        print(f"\n\n❌ 发生错误: {e}")
        import traceback
        traceback.print_exc()
