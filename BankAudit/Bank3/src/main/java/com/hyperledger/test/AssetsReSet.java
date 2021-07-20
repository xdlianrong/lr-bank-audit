package com.hyperledger.test;

import java.io.FileWriter;
import java.io.IOException;

public interface AssetsReSet {

    /**
     * 资产重置
     */
    static void assetsReset(String str1, String str2){
        FileWriter file1 = null;
        try {
            file1 = new FileWriter(str1);
            file1.write(str2+"\n");
            file1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
