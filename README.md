# 定时答题应用 (TimerQuiz)

这是一个Android应用，可以设置定时器，时间到达时会弹出全屏窗口显示题目，要求用户输入答案。

## 功能特性

- ⏰ **自定义定时器**: 可以设置小时、分钟、秒的倒计时
- 📝 **题目管理**: 支持添加多道题目，可以删除不需要的题目
- 🔔 **后台运行**: 使用前台服务确保定时器在后台正常运行
- 📱 **全屏显示**: 时间到达时弹出全屏窗口，占满整个屏幕
- 🔒 **锁屏显示**: 即使在锁屏状态下也会显示答题界面
- ➡️ **题目切换**: 支持多道题目的顺序显示和切换
- 💾 **答案保存**: 自动保存用户的答案到本地存储

## 使用方法

### 1. 设置定时器
- 在主界面设置倒计时时间（小时、分钟、秒）
- 默认设置为1分钟

### 2. 添加题目
- 在"添加题目"区域输入题目内容
- 点击"添加题目"按钮将题目加入列表
- 可以通过删除按钮移除不需要的题目

### 3. 启动定时器
- 确保至少添加了一道题目
- 点击"开始定时"按钮启动倒计时
- 应用会在通知栏显示剩余时间

### 4. 答题过程
- 时间到达时会自动弹出全屏答题界面
- 界面显示当前题目和进度（第X题/共Y题）
- 在文本框中输入答案
- 点击"确认"或"下一题"按钮继续
- 最后一题完成后点击"完成"结束答题

### 5. 权限要求
- **悬浮窗权限**: 用于在其他应用上方显示答题界面
- **通知权限**: 用于显示定时器状态通知
- **前台服务权限**: 用于后台运行定时器

## 技术实现

### 架构组件
- **MainActivity**: 主界面，负责设置和管理
- **QuizActivity**: 全屏答题界面
- **TimerService**: 后台定时器服务
- **Question**: 题目数据模型
- **QuestionAdapter**: 题目列表适配器

### 关键特性
- 使用 `CountDownTimer` 实现精确的倒计时
- 使用 `ForegroundService` 确保后台运行
- 使用 `FLAG_SHOW_WHEN_LOCKED` 实现锁屏显示
- 使用 `SYSTEM_UI_FLAG_IMMERSIVE_STICKY` 实现真正的全屏
- 使用 `SharedPreferences` 保存答案数据

### 权限说明
```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## 构建和安装

### 环境要求
- Android Studio Arctic Fox 或更高版本
- Android SDK API 24 (Android 7.0) 或更高版本
- Kotlin 1.9.10

### 构建步骤
1. 克隆或下载项目代码
2. 在Android Studio中打开项目
3. 等待Gradle同步完成
4. 连接Android设备或启动模拟器
5. 点击"Run"按钮构建并安装应用

### 注意事项
- 首次运行时需要手动授予悬浮窗权限
- 建议在真实设备上测试锁屏显示功能
- Android 6.0以上需要动态申请权限

## 使用场景

- 📚 **学习提醒**: 设置学习间隔，定时进行知识点复习
- 💪 **健身打卡**: 设置运动间隔，记录锻炼感受
- 🧘 **冥想记录**: 定时冥想后记录心得体会
- 📊 **工作汇报**: 定时提醒填写工作日志
- 🎯 **目标追踪**: 定期检查和记录目标进展

## 后续改进

- [ ] 支持题目分类和标签
- [ ] 添加语音输入功能
- [ ] 支持图片题目
- [ ] 数据导出功能
- [ ] 云端同步
- [ ] 统计分析功能
