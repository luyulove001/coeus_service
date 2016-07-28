TatansService回调方法说明
=============

 - onInit(){}:当开启无障碍服务开关时候自动调用该方法。

 - onInterrupt(){}: 当其他应用调用TatansToast.cancel()会自动调用该方法。

 -  onUnbind(Intent intent){}:回调参数为Intent，当开启无障碍服务关闭时候自动调用该方法。

 - onAccessibilityEvent(AccessibilityService accessibilityService, AccessibilityEvent acbEvent,  AccessibilityNodeInfo acbNodeInfo){}: acbNodeInfo，可以获取该界面下的节点信息，当对应的无障碍服务包被调用时候自动调用该方法。
 - startHomeKeyPressed(AccessibilityService accessibilityService){ }：开启该界面下的Home键的监听。

 - stopHomeKeyPressed(){}：关闭该界面下的Home键监听，一般在onHomeKeyPress(OnHomeKeyEven onHomeKeyEven)的最后使用。

 - onHomeKeyPressed(OnHomeKeyEven onHomeKeyEven) { }:

 - -回调参数为onHomeKeyEven.toString()==OnHomeKeyEven.KEYCODE_LONG_PRESS 为Home键长按事件

 --回调参数为onHomeKeyEven.toString()==OnHomeKeyEven.KEYCODE_SHORT_PRESS 为Home键短按事件。
 --回调Home键方法。

