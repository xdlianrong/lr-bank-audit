package com.hyperledger.test;

import java.io.FileWriter;
import java.io.IOException;

public interface FlushFile {
    static void flushFile(String str){
        try {
            FileWriter file = new FileWriter(str);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
