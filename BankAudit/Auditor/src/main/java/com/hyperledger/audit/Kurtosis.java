package com.hyperledger.audit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.hyperledger.audit.BigIntegerToBigDecimal.transferBigIntegertoBigDecimal;
import static com.hyperledger.audit.CommitHomomorphism.quadraComHomomorphismSum;


public interface Kurtosis {

    /**
     * 此方法用来求银行交易的峰度
     *  参数列表：银行、银行的四次资产和、交易四次承诺随机数和、账本
     */
    static BigDecimal calculateKurtosis(String str, BigInteger bankQuadraAssetsSum, BigInteger bankQuadraComRandomSum, BigInteger[] arr) throws IOException {

        BigDecimal kurtosis = null;

        /**
         * 从账本获取四次资产的和
         */
        BigInteger qCHSum = quadraComHomomorphismSum(str, arr);
        if (qCHSum.equals((arr[2].modPow(bankQuadraAssetsSum, arr[0])).multiply(arr[3].modPow(bankQuadraComRandomSum, arr[0])).mod(arr[0]))){
            /**
             * 四次资产和的值写进文件
             */
            FileWriter file = new FileWriter(str + "QuadraSum.txt");
            file.write(bankQuadraAssetsSum + "\n");
            file.close();
        } else {
            System.out.println("四次承诺验证不通过！");
            return null;
        }

        /**
         * 下面开始计算银行交易的峰度，其中用到的数据 银行的三次资产和、银行的二次资产和、交易平均值、交易次数、方差
         * 下面先读取银行的三次资产和
         */
        FileReader file1 = new FileReader(str+"TripleSum.txt");
        BufferedReader br1 = new BufferedReader(file1);
        String mInput1 = br1.readLine();
        BigDecimal bankTripleAssetsSum = null;
        try {
            bankTripleAssetsSum = new BigDecimal(mInput1);
        } catch (NullPointerException e){
            System.out.println("读取该银行的三次资产和错误！");
        }
        br1.close();
        file1.close();


        /**
         * 读取银行的二次资产和
         */
        FileReader file2 = new FileReader(str + "DoubleSum.txt");
        BufferedReader br2 = new BufferedReader(file2);
        String mInput2 = br2.readLine();
        BigDecimal bankDoubleAssetsSum = null;
        try {
            bankDoubleAssetsSum = new BigDecimal(mInput2);
        } catch (NullPointerException e){
            System.out.println("读取该银行的二次资产和错误！");
        }
        br2.close();
        file2.close();


        /**
         * 读取银行的交易平均值
         */
        FileReader file3 = new FileReader(str + "TransactionAverageValue.txt");
        BufferedReader br3 = new BufferedReader(file3);
        String mInput3 = br3.readLine();
        BigDecimal bTAV = null;
        try {
            bTAV = new BigDecimal(mInput3);
        } catch (NullPointerException e){
            System.out.println("读取该银行的交易平均值错误！");
        }
        br3.close();
        file3.close();


        /**
         * 读取银行的交易次数
         */
        FileReader file4 = new FileReader(str + "TransactionTime.txt");
        BufferedReader br4 = new BufferedReader(file4);
        String mInput4 = br4.readLine();
        BigDecimal bTT = null;
        try {
            bTT = new BigDecimal(mInput4);
        } catch (NullPointerException e) {
            System.out.println("读取银该行的交易次数错误！");
        }
        br4.close();
        file4.close();


        /**
         * 读取银行的方差
         */
        FileReader file5 = new FileReader(str + "Variance.txt");
        BufferedReader br5 = new BufferedReader(file5);
        String mInput5 = br5.readLine();
        BigDecimal bVariance = null;
        try {
            bVariance = new BigDecimal(mInput5);
        } catch (NullPointerException e) {
            System.out.println("读取该银行的交易值方差错误！");
        }
        br5.close();
        file5.close();


        /**
         * 最重要的一步，验证通过之后，需要将三次资产和转换成BigDecimal类型
         */
        BigDecimal bankQuadraAssetsSum1 = transferBigIntegertoBigDecimal(bankQuadraAssetsSum);

        /**
         * 下面考试计算峰度
         */
        //分子
        BigDecimal molecular = bankQuadraAssetsSum1.divide(bTT, 8, BigDecimal.ROUND_HALF_UP).subtract((bankTripleAssetsSum.multiply(bTAV).
                multiply(BigDecimal.valueOf(4))).divide(bTT, 8, BigDecimal.ROUND_HALF_UP)).add((bankDoubleAssetsSum.multiply((bTAV.pow(2).
                multiply(BigDecimal.valueOf(6))))).divide(bTT, 8, BigDecimal.ROUND_HALF_UP)).subtract(bTAV.pow(4).multiply(BigDecimal.valueOf(3)));
        //分母
        BigDecimal denominator = bVariance.pow(2);

        /**
         * zhixing
         */
        kurtosis = molecular.divide(denominator, 8, BigDecimal.ROUND_HALF_UP).subtract(BigDecimal.valueOf(3));


        /**
         * 把偏度写入文件吧
         */
        FileWriter file6 = new FileWriter(str + "Kurtosis.txt");
        file6.write(kurtosis + "\n");
        file6.close();

        return kurtosis;
    }
}
