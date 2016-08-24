package net.tatans.coeus.util;
import java.io.File;
import android.os.Environment;

/**
 * Created by Yuriy on 2016/8/11.
 */

class DataCleanManager {
    public static final String FILE_NAME = Environment.getExternalStorageDirectory().toString()  + "/tatans/data/";
   
    protected static void cleanInternalCache(String packageName) {
        File file = new File(FILE_NAME+packageName+  "/cache");
        delete(file);
    }
    private static void delete(File file) {
         if (file.isFile()) {
              file.delete();
                  return;
         }
         if(file.isDirectory()&&!"lib".equals(file.getName())){
             File[] childFiles = file.listFiles();
             if (childFiles == null || childFiles.length == 0) {
                             file.delete();
                                return;
             }

             for (int i = 0; i < childFiles.length; i++) {
                               delete(childFiles[i]);
             }
             file.delete();
         }
     }
    protected static void cleanUserData(String packageName) {
        File directory =new File(FILE_NAME +packageName);
        delete(directory);
    }
}


