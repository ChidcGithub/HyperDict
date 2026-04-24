@echo off
REM ECDICT 数据库下载和配置脚本 (Windows)

echo ========================================
echo ECDICT 词典数据库自动配置工具
echo ========================================
echo.

REM 检查 Python 是否安装
python --version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到 Python，请先安装 Python 3.7+
    echo 下载地址: https://www.python.org/downloads/
    pause
    exit /b 1
)

echo 正在运行配置脚本...
echo.

python setup_sqlite.py

if errorlevel 1 (
    echo.
    echo 脚本执行失败！
    pause
    exit /b 1
)

echo.
echo 按任意键退出...
pause >nul
