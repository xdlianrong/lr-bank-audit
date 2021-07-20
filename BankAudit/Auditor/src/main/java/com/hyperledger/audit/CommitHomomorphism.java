package com.hyperledger.audit;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyperledger.bank.BankAudit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;


public interface CommitHomomorphism {

    //利用Pedersen承诺的同态性，计算账本上承诺的和，返回值应该是一个大整数
    static BigInteger comHomomorphismSum(String str, BigInteger[] arr) throws FileNotFoundException {
        BigInteger cHSum = new BigInteger("1");

        /**
         * 先从交易文件中读取每一行交易，然后在计算
         */
        FileReader file1 = new FileReader("allTransactions.txt");
        BufferedReader br = new BufferedReader(file1);
        String tempInput = null;
        //循环读取每一行
        try {
            while ((tempInput = br.readLine()) != null) {
                //读进来的字符串转换去掉前面的时间字段
                String str1 = tempInput.replaceFirst("\\{\"time\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\",", "{");
                System.out.println("str1:"+str1);
                JSONObject jsonObject = JSON.parseObject(str1);
                JSONObject bankJson = jsonObject.getJSONObject(str);
//                System.out.println(bankJson);
                BankAudit bank = JSON.parseObject(String.valueOf(bankJson), BankAudit.class);
//                System.out.println(bank);
                if (bank.getIsFrom() == 0) {
                    cHSum = cHSum.multiply(bank.getCom()).mod(arr[0]);
                } else {
                    BigInteger newx = bank.getCom().modPow(BigInteger.valueOf(-1), arr[0]);
                    cHSum = cHSum.multiply(newx).mod(arr[0]);
                }
            }
            br.close();
            file1.close();
        } catch (IOException e) {
            System.out.println("读取失败！");
        }
        return cHSum;
    }


    //计算比特承诺的和
    static BigInteger bitComHomomorphismSum(String str, BigInteger[] arr) throws FileNotFoundException {
        BigInteger bCHSum = new BigInteger("1");

        /**
         * 先从交易文件中读取每一行交易，然后在计算
         */
        FileReader file1 = new FileReader("allTransactions.txt");
        BufferedReader br = new BufferedReader(file1);
        String tempInput = null;
        //循环读取每一行
        try {
            while ((tempInput = br.readLine()) != null) {
                //读进来的字符串转换去掉前面的时间字段
                String str1 = tempInput.replaceFirst("\\{\"time\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\",", "{");
                JSONObject bankJson = JSON.parseObject(str1).getJSONObject(str);
                BankAudit bank = JSON.parseObject(String.valueOf(bankJson), BankAudit.class);
                bCHSum = bCHSum.multiply(bank.getBitCom()).mod(arr[0]);
            }
            br.close();
            file1.close();
        } catch (IOException e) {
            System.out.println("读取失败！");
        }
        return bCHSum;
    }


    //计算隐藏值二次方的和
    static BigInteger doubleComHomomorphismSum(String str, BigInteger[] arr) throws FileNotFoundException {
        BigInteger dCHSum = new BigInteger("1");

        /**
         * 先从交易文件中读取每一行交易，然后在计算
         */
        FileReader file1 = new FileReader("allTransactions.txt");
        BufferedReader br = new BufferedReader(file1);
        String tempInput = null;
        //循环读取每一行
        try {
            while ((tempInput = br.readLine()) != null) {
                //读进来的字符串转换去掉前面的时间字段
                String str1 = tempInput.replaceFirst("\\{\"time\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\",", "{");
                JSONObject bankJson = JSON.parseObject(str1).getJSONObject(str);
                BankAudit bank = JSON.parseObject(String.valueOf(bankJson), BankAudit.class);
                dCHSum = dCHSum.multiply(bank.getDoubleCom()).mod(arr[0]);
            }
            br.close();
            file1.close();
        } catch (IOException e) {
            System.out.println("读取失败！");
        }
        return dCHSum;
    }


    //计算隐藏值三次方的和
    static BigInteger tripleComHomomorphismSum(String str, BigInteger[] arr) throws FileNotFoundException {
        BigInteger tCHSum = new BigInteger("1");

        /**
         * 先从交易文件中读取每一行交易，然后在计算
         */
        FileReader file1 = new FileReader("allTransactions.txt");
        BufferedReader br = new BufferedReader(file1);
        String tempInput = null;
        //循环读取每一行
        try {
            while ((tempInput = br.readLine()) != null) {
                //读进来的字符串转换去掉前面的时间字段
                String str1 = tempInput.replaceFirst("\\{\"time\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\",", "{");
                JSONObject bankJson = JSON.parseObject(str1).getJSONObject(str);
                BankAudit bank = JSON.parseObject(String.valueOf(bankJson), BankAudit.class);
                tCHSum = tCHSum.multiply(bank.getTripleCom()).mod(arr[0]);
            }
            br.close();
            file1.close();
        } catch (IOException e) {
            System.out.println("读取失败！");
        }
        return tCHSum;
    }


    //计算隐藏值的四次方的和
    static BigInteger quadraComHomomorphismSum(String str, BigInteger[] arr) throws FileNotFoundException {
        BigInteger qCHSum = new BigInteger("1");

        /**
         * 先从交易文件中读取每一行交易，然后在计算
         */
        FileReader file1 = new FileReader("allTransactions.txt");
        BufferedReader br = new BufferedReader(file1);
        String tempInput = null;
        //循环读取每一行
        try {
            while ((tempInput = br.readLine()) != null) {
                //读进来的字符串转换去掉前面的时间字段
                String str1 = tempInput.replaceFirst("\\{\"time\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\",", "{");
                JSONObject bankJson = JSON.parseObject(str1).getJSONObject(str);
                BankAudit bank = JSON.parseObject(String.valueOf(bankJson), BankAudit.class);
                qCHSum = qCHSum.multiply(bank.getQuadraCom()).mod(arr[0]);
            }
            br.close();
            file1.close();
        } catch (IOException e) {
            System.out.println("读取失败！");
        }
        return qCHSum;
    }

}
