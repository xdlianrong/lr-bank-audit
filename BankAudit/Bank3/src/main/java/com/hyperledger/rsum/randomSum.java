package com.hyperledger.rsum;

import com.alibaba.fastjson.JSON;
import com.hyperledger.bank.RandomClass;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

/**
 * @ Author：lxgxgxgxg
 * @ Date：Created in 15:05 2021/4/29
 * @ Description：在面对审计员的审计时，该银行直接计算每个致盲因子的和，然后发送给审计员
 * @ Version: 1.0
 */
public class randomSum {

    public static String sum(BigInteger[] arr){
        /**
         * 1、首先读取bankAllRandom.txt文件中的数据，保存到string[]
         */
        String[] oneInfor = ReadStringArray.ReadStrArray("bankAllRandom.txt");

        /**
         * 处理每一条数据，把信息转换成RandomClass,然后进行数据操作
         */
        BigInteger r1 = new BigInteger("0");
        BigInteger r2 = new BigInteger("0");
        BigInteger x = new BigInteger("0");
        BigInteger y = new BigInteger("0");
        BigInteger z = new BigInteger("0");
        for (int i = 0; i < oneInfor.length; i++) {
            RandomClass rc = JSON.parseObject(oneInfor[i], RandomClass.class);
            r1 = r1.add(rc.getR1());
            r2 = r2.add(rc.getR2());
            x = x.add(rc.getX());
            y = y.add(rc.getY());
            z = z.add(rc.getZ());
        }
        /**
         * 对最后的结果进行取模
         */
        r1 = r1.mod(arr[1]);
        r2 = r2.mod(arr[1]);
        x = x.mod(arr[1]);
        y = y.mod(arr[1]);
        z = z.mod(arr[1]);

        /**
         * 保存最后的值到本地，并且发送给审计员
         */
        RandomClass randomClass = new RandomClass(r1, r2, x, y, z);
        String str = JSON.toJSONString(randomClass);
        try {
            FileWriter file = new FileWriter("randomSum.txt");
            file.write(str+"\n");
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
}
