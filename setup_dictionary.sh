#!/bin/bash
# ECDICT 数据库下载和转换脚本 (Linux/Mac)

echo "========================================"
echo "ECDICT 词典数据库自动配置工具"
echo "========================================"
echo

# 检查 Python 是否安装
if ! command -v python3 &> /dev/null; then
    echo "错误: 未找到 Python 3，请先安装 Python 3.7+"
    exit 1
fi

echo "正在运行下载脚本..."
echo

python3 download_ecdict.py

if [ $? -ne 0 ]; then
    echo
    echo "脚本执行失败！"
    exit 1
fi

echo
echo "完成！"
