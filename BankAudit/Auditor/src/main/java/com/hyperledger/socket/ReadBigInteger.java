package com.hyperledger.socket;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;

public interface ReadBigInteger {
    /**
     * 读取一个大数的函数
     */
    static BigInteger readBigInteger(String str){
        try {
            FileReader file = new FileReader(str);
            BufferedReader br = new BufferedReader(file);
            BigInteger num = new BigInteger(br.readLine());
            return num;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigInteger.valueOf(0);
    }
}
