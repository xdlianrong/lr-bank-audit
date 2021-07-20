package com.hyperledger.connection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;

public interface ReadBase {
    //读取椭圆曲线的生成元
    static BigInteger[] readBase() throws IOException {
        FileReader file = new FileReader("ecc.txt");
        BufferedReader br = new BufferedReader(file);

        String pInput = br.readLine();
        String qInput = br.readLine();
        String gInput = br.readLine();
        String hInput = br.readLine();

        BigInteger p = null;
        BigInteger q = null;
        BigInteger g = null;
        BigInteger h = null;

        try{
            p = new BigInteger(pInput);
            q = new BigInteger(qInput);
            g = new BigInteger(gInput);
            h = new BigInteger(hInput);
            br.close();
            file.close();
        } catch(NumberFormatException | NullPointerException e){
            System.out.println("读取失败！");
//            break;
//            e.printStackTrace();
        }


        BigInteger[] biginteger = {p, q, g, h};
        return biginteger;
    }
}
