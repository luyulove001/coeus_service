package net.tatans.coeus.util;

/**
 * Created by Yuriy on 2016/8/26.
 */

import android.os.Environment;

import net.tatans.coeus.network.tools.TatansApplication;
import net.tatans.coeus.network.tools.TatansLog;

import java.io.*;

import static android.content.res.AssetManager.ACCESS_BUFFER;
import static android.content.res.AssetManager.ACCESS_RANDOM;
import static android.content.res.AssetManager.ACCESS_UNKNOWN;

/**
 * 复制文件夹或文件夹
 */
public class CopyDirectory {

    // 源文件夹
    private static String url1 = TatansApplication.getContext().getPackageResourcePath()+"data/";

    // 目标文件夹
    private static String url2 = Environment.getExternalStorageDirectory().toString()  + "/tatans/data/";

    public static void  copyFile(){
        File[] file = (new File(url1)).listFiles();
        try {
            for (int i = 0; i < file.length; i++) {
                if (file[i].isFile()) {
                    // 复制文件
                    copyFile(file[i],new File(url2+file[i].getName()));
                }
                if (file[i].isDirectory()) {
                    // 复制目录
                    String sourceDir=url1+File.separator+file[i].getName();
                    String targetDir=url2+File.separator+file[i].getName();
                    copyDirectiory(sourceDir, targetDir);
                }
            }
        }catch (IOException e){
            TatansLog.e(e.toString());
        }

    }
    public static void copyBigDataToSD(String strOutFileName)
    {
        InputStream myInput;
        try {
            OutputStream myOutput = new FileOutputStream(url2);
            myInput = TatansApplication.getContext().getAssets().open("data",ACCESS_RANDOM );
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while(length > 0)
            {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        }catch (IOException e){
            TatansLog.e(e.toString());
        }
    }
    // 复制文件
    private static void copyFile(File sourceFile,File targetFile)
            throws IOException{
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff=new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff=new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len =inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();

        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }
    // 复制文件夹
    private static void copyDirectiory(String sourceDir, String targetDir)
            throws IOException {
        // 新建目标目录
        (new File(targetDir)).mkdirs();
        // 获取源文件夹当前下的文件或目录
        File[] file = (new File(sourceDir)).listFiles();
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                // 源文件
                File sourceFile=file[i];
                // 目标文件
                File targetFile=new
                        File(new File(targetDir).getAbsolutePath()
                        +File.separator+file[i].getName());
                copyFile(sourceFile,targetFile);
            }
            if (file[i].isDirectory()) {
                // 准备复制的源文件夹
                String dir1=sourceDir + "/" + file[i].getName();
                // 准备复制的目标文件夹
                String dir2=targetDir + "/"+ file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }
    }
}


