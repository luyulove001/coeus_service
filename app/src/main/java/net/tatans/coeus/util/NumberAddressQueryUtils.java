package net.tatans.coeus.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.tatans.coeus.service.activity.TatansServiceApplication;

public class NumberAddressQueryUtils {

    /**
     * 传一个号码进来，返回一归属地回去
     *
     * @param number
     * @return
     */
    public static String queryNumber(String number) {
        String path = TatansServiceApplication.getContext().getFilesDir()
                .getAbsolutePath()+ "address.db";   //data/data目录
        String address = "";
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
                SQLiteDatabase.OPEN_READONLY);
        // 手机号码 13 14 15 16 18
        // 手机号码的正则表达式
        if (number.matches("^1[345678]\\d{9}$")) {
            // 手机号码

            Cursor cursor = database
                    .rawQuery(
                            "select location from data2 where id = (select outkey from data1 where id = ?)",
                            new String[]{number.substring(0, 7)});

            while (cursor.moveToNext()) {

                String location = cursor.getString(0);
                address = location;
            }
            cursor.close();

        } else if (number.matches("^400[016789]\\d{6}$")) {
            address = "服务热线";
        } else {
            // 其他的电话号码
            switch (number.length()) {
                case 3:
                    // 110
                    address = "亲情网";
                    break;
                case 4:
                    // 5554
                    address = "模拟器";
                    break;
                case 5:
                    // 10086
                    address = "客服电话";
                    break;
                case 6:
                    address = "集团短号";
                    break;
                case 7:
                    //
                    address = "本地号码";
                    break;

                case 8:
                    address = "本地号码";
                    break;
                case 14:
                    address = "";
                    break;

                default:
                    // /处理长途电话 10
                    if (number.length() > 10 && number.startsWith("0")) {
                        // 010-59790386
                        Cursor cursor = database.rawQuery(
                                "select location from data2 where area = ?",
                                new String[]{number.substring(1, 3)});

                        while (cursor.moveToNext()) {
                            String location = cursor.getString(0);
                            address = location.substring(0, location.length() - 2);
                        }
                        cursor.close();

                        // 0855-59790386
                        cursor = database.rawQuery(
                                "select location from data2 where area = ?",
                                new String[]{number.substring(1, 4)});
                        while (cursor.moveToNext()) {
                            String location = cursor.getString(0);
                            address = location.substring(0, location.length() - 2);

                        }
                    }

                    break;
            }

        }

        return address;
    }

}
