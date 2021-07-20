package com.hyperledger.rsum;

import com.alibaba.fastjson.JSON;
import com.hyperledger.bank.AssetsInformation;
import com.hyperledger.bank.RandomClass;
import com.hyperledger.commit.Commit;
import com.hyperledger.commit.ReadBigInteger;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @ Author：lxgxgxgxg
 * @ Date：Created in 17:15 2021/5/11
 * @ Description：
 * @ Version: 1.0
 */
public class SendAssetsInformation {
    public static String send() throws IOException {
        /**
         * 读取每个资产
         */
        BigInteger comSum = ReadBigInteger.readBigInteger("bank2AssetsRecord.txt");
        int time = ReadBigInteger.readBigInteger("bank2TransactionTime.txt").intValue();
        BigInteger doubleComSum = ReadBigInteger.readBigInteger("bank2DoubleComSum.txt");
        BigInteger tripleComSum = ReadBigInteger.readBigInteger("bank2TripleComSum.txt");
        BigInteger quadraComSum = ReadBigInteger.readBigInteger("bank2QuadraComSum.txt");
        BigInteger[] arr = Commit.readBase();
        String temp = randomSum.sum(arr);
        RandomClass randomClass = JSON.parseObject(temp, RandomClass.class);
        BigInteger r1 = randomClass.getR1();
        BigInteger r2 = randomClass.getR2();
        BigInteger x = randomClass.getX();
        BigInteger y = randomClass.getY();
        BigInteger z = randomClass.getZ();
        AssetsInformation infor = new AssetsInformation(comSum, r1, time, r2, doubleComSum, x,
                tripleComSum, y, quadraComSum, z);
        String strInfor = JSON.toJSONString(infor);
        /**
         * 保存到当地
         */
        try {
            FileWriter file = new FileWriter("randomSum.txt");
            file.write(strInfor+"\n");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strInfor;
    }
}
