#!/bin/bash

# 定时答题应用 APK 构建脚本
# 这是一个演示脚本，展示了如何构建Android APK的步骤

echo "=========================================="
echo "定时答题应用 (TimerQuiz) APK 构建脚本"
echo "=========================================="

# 检查项目结构
echo "1. 检查项目结构..."
if [ -f "app/build.gradle" ]; then
    echo "✓ 找到 app/build.gradle"
else
    echo "✗ 未找到 app/build.gradle"
    exit 1
fi

if [ -f "app/src/main/AndroidManifest.xml" ]; then
    echo "✓ 找到 AndroidManifest.xml"
else
    echo "✗ 未找到 AndroidManifest.xml"
    exit 1
fi

# 检查源代码文件
echo "2. 检查源代码文件..."
SOURCE_FILES=(
    "app/src/main/java/com/example/timerquiz/MainActivity.kt"
    "app/src/main/java/com/example/timerquiz/QuizActivity.kt"
    "app/src/main/java/com/example/timerquiz/TimerService.kt"
    "app/src/main/java/com/example/timerquiz/Question.kt"
    "app/src/main/java/com/example/timerquiz/QuestionAdapter.kt"
)

for file in "${SOURCE_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "✓ $file"
    else
        echo "✗ $file"
        exit 1
    fi
done

# 检查资源文件
echo "3. 检查资源文件..."
RESOURCE_FILES=(
    "app/src/main/res/layout/activity_main.xml"
    "app/src/main/res/layout/activity_quiz.xml"
    "app/src/main/res/layout/item_question.xml"
    "app/src/main/res/values/strings.xml"
    "app/src/main/res/values/colors.xml"
    "app/src/main/res/values/themes.xml"
)

for file in "${RESOURCE_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo "✓ $file"
    else
        echo "✗ $file"
        exit 1
    fi
done

echo "4. 项目验证完成！"
echo ""
echo "=========================================="
echo "APK 构建说明"
echo "=========================================="
echo ""
echo "要构建实际的APK文件，请按照以下步骤操作："
echo ""
echo "1. 安装 Android Studio 和 Android SDK"
echo "   - 下载地址: https://developer.android.com/studio"
echo ""
echo "2. 在 Android Studio 中打开此项目"
echo "   - File -> Open -> 选择此项目目录"
echo ""
echo "3. 等待 Gradle 同步完成"
echo "   - Android Studio 会自动下载依赖"
echo ""
echo "4. 构建 APK"
echo "   - 方法1: Build -> Build Bundle(s) / APK(s) -> Build APK(s)"
echo "   - 方法2: 在终端运行: ./gradlew assembleDebug"
echo ""
echo "5. APK 文件位置"
echo "   - Debug APK: app/build/outputs/apk/debug/app-debug.apk"
echo "   - Release APK: app/build/outputs/apk/release/app-release.apk"
echo ""
echo "=========================================="
echo "应用功能说明"
echo "=========================================="
echo ""
echo "✨ 主要功能:"
echo "  • 倒计时定时器 - 设置小时、分钟、秒"
echo "  • 指定时间定时器 - 选择具体的日期和时间"
echo "  • 题目管理 - 添加、删除题目"
echo "  • 全屏答题界面 - 时间到达时弹出"
echo "  • 锁屏显示 - 即使锁屏也会显示"
echo "  • 答案保存 - 自动保存用户答案"
echo ""
echo "🔧 技术特性:"
echo "  • Material Design 3 界面"
echo "  • Kotlin 编程语言"
echo "  • 前台服务确保后台运行"
echo "  • 通知栏显示倒计时状态"
echo "  • 支持 Android 7.0+ (API 24+)"
echo ""
echo "📱 使用场景:"
echo "  • 学习提醒和复习"
echo "  • 工作汇报和记录"
echo "  • 健身打卡"
echo "  • 冥想记录"
echo "  • 目标追踪"
echo ""
echo "=========================================="
echo "安装说明"
echo "=========================================="
echo ""
echo "1. 在手机上启用开发者选项"
echo "   - 设置 -> 关于手机 -> 连续点击版本号7次"
echo ""
echo "2. 启用USB调试"
echo "   - 设置 -> 开发者选项 -> USB调试"
echo ""
echo "3. 允许安装未知来源应用"
echo "   - 设置 -> 安全 -> 未知来源"
echo ""
echo "4. 安装APK"
echo "   - 将APK文件传输到手机"
echo "   - 点击APK文件进行安装"
echo ""
echo "5. 授予必要权限"
echo "   - 悬浮窗权限（必需）"
echo "   - 通知权限"
echo ""
echo "构建脚本执行完成！"
echo "项目已准备就绪，可以在Android Studio中构建APK。"
