package com.hyperledger.audit;


import com.hyperledger.connection.ReadBase;
import com.hyperledger.connection.ReadInitializeRandom;
import com.hyperledger.socket.ReadBigInteger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.hyperledger.audit.BigIntegerToBigDecimal.transferBigIntegertoBigDecimal;
import static com.hyperledger.audit.CommitHomomorphism.bitComHomomorphismSum;
import static com.hyperledger.audit.CommitHomomorphism.comHomomorphismSum;


public interface Sum {

    //审计特指银行的资产和是否正确
    //函数的返回值为boolean, 参数是银行给的资产和、随机数和以及从区块链上获取到的账本
    static String  bankAssetsSum(String str, BigInteger  bankAssetsSum, BigInteger bankRandomSum, BigInteger[] arr) throws IOException {
        BigInteger cHSum = comHomomorphismSum(str, arr);
        System.out.println(cHSum);
        /**
         * 这个地方就要把银行初始化的过程中的r计算进去，加到bankRandom中
         */
        int bankStr = Integer.valueOf(str.substring(4, str.length()));
        BigInteger[] random= ReadInitializeRandom.readBase();
        if (bankStr == 1){
            bankRandomSum = (bankRandomSum.add(random[0])).mod(arr[1]);
        }else if (bankStr == 2){
            bankRandomSum = (bankRandomSum.add(random[1])).mod(arr[1]);
        }else if (bankStr == 3){
            bankRandomSum = (bankRandomSum.add(random[2])).mod(arr[1]);
        }else {
            bankRandomSum = (bankRandomSum.add(random[3])).mod(arr[1]);
        }

        BigInteger newCHSum = (arr[2].modPow(bankAssetsSum, arr[0])).multiply((arr[3].modPow(bankRandomSum, arr[0]))).mod(arr[0]);
        System.out.println(newCHSum);
        if (cHSum.equals(newCHSum)){
            /**
             * 此处如果审计资产和通过，我们 就把该银行的资产和写进文件
             */
            FileWriter file = new FileWriter(str+"Sum.txt");  //不加true就是覆盖之前写的的系统和
            file.write(bankAssetsSum + "\n");
            file.close();
            System.out.println(str+ "资产和结算正确，通过！");
            return "资产和结算正确，通过！";
        } else {
            System.out.println("资产和审计失败");
            return "资产和审计失败";
        }
    }


    //审计特指银行交易的平均值
    //这里的平均值意味着是求特指的银行参与交易的平均值，则是账本上的和除以次数
    //1、求比特承诺的和,这里的我们需要知道参与的次数，因为我们不能从Pedersen承诺当中求出比特承诺的具体的次数，所以我们需要银行给我们提供参与交易的次数，然后
    //   我们去账本验证。
    static BigDecimal bankTransactionAverageValue(String str, BigInteger bankBitComSum,
                                                    BigInteger bankBitComRandomSum, BigInteger[] arr) throws IOException {
        /**
         * 再求交易的平均值之前，我们需要首先进行资产和的验证，然后把正确的资产和写进文件，现在需要求平均，我们只需去从文件读取资产和即可，不需用行函数的参数传进来。
         */
        //读取银行的资产和
        FileReader file = new FileReader(str+"Sum.txt");
        BufferedReader br = new BufferedReader(file);
        String mInput = br.readLine();
        /**
         * 文件中保存的是String类型，因为后续计算时，我们需要的是BigDecimal类型（存在除法，有可能存在小数），所以我们直接转换为BigDeciaml,而不是BigInteger
         */
        BigDecimal strBankSum = null;
        try {
            strBankSum = new BigDecimal(mInput);
        } catch (NumberFormatException e){
            System.out.println("读取该银行资产和错误！");
        }
        file.close();
        //求平均值的时候，分母只算转账的金额，要计算现在的资产和和初始值之间的差值
        int bankStr = Integer.valueOf(str.substring(4, str.length()));
        BigInteger lastAssets = BigInteger.valueOf(0);
        if (bankStr == 1){
            lastAssets = ReadBigInteger.readBigInteger("bank1InitializeAssets.txt");
        }else if (bankStr == 2){
            lastAssets = ReadBigInteger.readBigInteger("bank2InitializeAssets.txt");
        }else if (bankStr == 3){
            lastAssets = ReadBigInteger.readBigInteger("bank3InitializeAssets.txt");
        }else {
            lastAssets = ReadBigInteger.readBigInteger("bank4InitializeAssets.txt");
        }

        /**
         * 计算参与交易的金额
         */
//        System.out.println(lastAssets);
        BigDecimal newLastAssets = BigIntegerToBigDecimal.transferBigIntegertoBigDecimal(lastAssets);
        strBankSum = (newLastAssets.subtract(strBankSum)).abs();
        System.out.println(strBankSum);
        /**
         * 求比特承诺的次数
         */
        //求比特承诺和
        BigInteger bCHSum = bitComHomomorphismSum(str, arr);

        /**
         * 首先，平均值是BigDecimal类型，保留8位小数
         */
        BigDecimal averageValue = null;
        /**
         * 由银行给的和账本一起验证比特承诺
         */
        if (bCHSum.equals((arr[2].modPow(bankBitComSum, arr[0])).multiply((arr[3].modPow(bankBitComRandomSum, arr[0]))).mod(arr[0]))) {
            /**
             * 将BigInteger转换成BigDecimal类型，把交易次数转换成BigDecimal类型
             */
            BigDecimal bTT = transferBigIntegertoBigDecimal(bankBitComSum);
            averageValue = strBankSum.divide(bTT, 8, BigDecimal.ROUND_HALF_UP);  //保留八位小数
            /**
             * 因为后面求方差、偏度和峰度的时候，也要用到平均值，所以我们把平均值保存，放到文件中去。
             */
            FileWriter file1 = new FileWriter(str+"TransactionAverageValue.txt");  //不加true就是覆盖之前写的的文件
            FileWriter file2 = new FileWriter(str+"TransactionTime.txt");
            file1.write(averageValue + "\n");
            file2.write(bankBitComSum + "\n");
            file1.close();
            file2.close();
        } else {
            System.out.println("输入的交易次数有误！");
        }
        return averageValue;
    }
}
