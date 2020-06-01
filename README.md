# TestRecorder

安卓 录音功能的Demo，实现了APP在后台和手机息屏长时间录音的功能(在Android9.0和Android 10.0手机测试通过)。

录音功能 用了zhaolewei大佬的源码，源码地址：https://github.com/zhaolewei/ZlwAudioRecorder

项目框架用的是Lodz大佬的AgileDev，源码地址：https://github.com/LZ9/AgileDev


# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
2020.05.29 更新
这次提交解决了Android 9.0系统和Android 10.0系统禁止闲置APP在后台和息屏使用麦克风的问题。

思路：
系统只对后台闲置服务进行闲置，不让后台服务偷偷使用麦克风。目前Android9.0的前台服务中，必须弹出一个通知，告知用户，某个前台服务正在运行。那么只要把后台服务RecordService改成前台服务，就可以解决这个问题。

操作：        
1.迁移了录音库recorderlib的部分文件：RecordService，RecordManager，RecordHelper，Mp3EncodeThread，RecordStateListener。

2.将录音服务RecordService转成前台服务（将录音通知栏Notification相关代码和迁移到RecordService里，添加后台服务转成前台服务的代码：startForeground(NOTIFI_RECORDER_ID,notification);

缺陷： 
此次代码还不完善:通知栏暂停录音和继续录音功能未完成，录音主页MainActivity的线程UI更新录音时间相关代码还未修改，预计改为在RecordService发送eventbus更新MainActivity的UI，减少性能损耗。

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # 
2020.05.30 更新
通知栏暂停录音和继续录音功能完成
