package com.hyperledger.commit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public interface ReadFileLxAndRx {
    /**
     * 读文件的函数
     */
    public static String readFile(String str){
        FileReader file = null;
        String str1 = null;
        try {
            file = new FileReader(str);
            BufferedReader br = new BufferedReader(file);
            str1 = br.readLine();
            br.close();
            file.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str1;
    }
}
