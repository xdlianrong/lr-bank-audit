package com.hyperledger.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;

public interface ReadString {

    /**
     * 读取银行给的审计参数
     */
    static String readBigInteger(String str){
        String num = null;
        try {
            FileReader file = new FileReader(str);
            BufferedReader br = new BufferedReader(file);
            num = br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }
}
