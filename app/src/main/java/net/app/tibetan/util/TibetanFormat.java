package net.app.tibetan.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by like on 15-4-19.
 */
public class TibetanFormat {
    //写文件
    public static void writeSDFile(String fileName, String write_str) throws IOException {
        File file = new File(fileName);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = write_str.getBytes();
        fos.write(bytes);
        fos.close();
    }

}