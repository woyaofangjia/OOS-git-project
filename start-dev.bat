@echo off
rem 设置UTF-8编码
chcp 65001 > nul

setlocal enabledelayedexpansion

rem 项目根目录
set PROJECT_DIR=%~dp0

rem 切换到项目目录
cd /d %PROJECT_DIR%

echo ============================
echo 校园二手交易系统 - 开发环境启动脚本
echo ============================

rem 检查Maven是否安装
where mvn > nul 2> nul
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven，请确保Maven已正确安装并添加到系统PATH中。
    pause
    exit /b 1
)

rem 检查Java是否安装
where java > nul 2> nul
if %errorlevel% neq 0 (
    echo 错误: 未找到Java，请确保JDK 21已正确安装并添加到系统PATH中。
    pause
    exit /b 1
)

rem 检查8080端口是否被占用
netstat -ano | findstr :8080 > nul
if %errorlevel% equ 0 (
    echo 警告: 8080端口已被占用，可能是其他服务正在运行。
    echo 正在尝试关闭占用端口的进程...
    for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
        taskkill /F /PID %%a > nul 2> nul
        echo 已终止进程 %%a
    )
)

echo 正在编译项目...
mvn clean compile > compile.log 2>&1
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败，请查看compile.log文件获取详细信息。
    pause
    exit /b 1
)

echo 编译成功，正在启动Jetty...
echo 提示: 使用Ctrl+C可以停止服务

echo 启动命令: mvn jetty:run
mvn jetty:run

endlocal