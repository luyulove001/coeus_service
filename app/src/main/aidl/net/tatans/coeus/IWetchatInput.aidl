// IMyAidlInterface.aidl
package net.tatans.coeus;

// Declare any non-default types here with import statements

interface IWetchatInput {
        void inputPassword(String num);
        boolean canSimulatorKeyEvent();//是否能用adb 模拟按键事件
}
