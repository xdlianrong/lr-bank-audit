package com.hyperledger.audit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.hyperledger.audit.BigIntegerToBigDecimal.transferBigIntegertoBigDecimal;
import static com.hyperledger.audit.CommitHomomorphism.tripleComHomomorphismSum;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2020/12/17
 * \* Time: 16:26
 * \* To change this template use File | Settings | File Templates.
 * \* Description:  银行审计求偏度
 * \
 */
public interface Skewness {
    /**
     * 求银行交易的偏度
     * 参数列表：银行，银行每笔交易三次资产和、三次承诺随机数的和、账本
     */
    static BigDecimal calaulateSkewness(String str, BigInteger bankTripleAssetsSum, BigInteger bankTripleComRandomSum, BigInteger[] arr) throws IOException {
        BigDecimal skewness = null;

        /**
         * 从账本获取三次资产的和
         */
        BigInteger tCHSum = tripleComHomomorphismSum(str, arr);
        if (tCHSum.equals((arr[2].modPow(bankTripleAssetsSum, arr[0])).multiply(arr[3].modPow(bankTripleComRandomSum, arr[0])).mod(arr[0]))){
            /**
             * 三次资产的值写进文件
             */
            FileWriter file = new FileWriter(str + "TripleSum.txt");
            file.write(bankTripleAssetsSum + "\n");
            file.close();
        } else {
            System.out.println("三次承诺验证不通过！");
            return null;
        }


        /**
         * 下面开始计算该银行的偏度，其中用到的数据有二次资产和、交易平均值、交易次数和方差
         * 读取该银行的二次资产和
         */
        FileReader file1 = new FileReader(str+"DoubleSum.txt");
        BufferedReader br1 = new BufferedReader(file1);
        String mInput1 = br1.readLine();
        BigDecimal bankDoubleAssetsSum = null;
        try{
            bankDoubleAssetsSum = new BigDecimal(mInput1);
        } catch (NullPointerException e) {
            System.out.println("读取该银行二次资产和错误！");
        }
        br1.close();
        file1.close();


        /**
         * 读取该银行的平均值
         */
        FileReader file2 = new FileReader(str+"TransactionAverageValue.txt");
        BufferedReader br2 = new BufferedReader(file2);
        String mInput2 = br2.readLine();
        BigDecimal bTAV = null;
        try{
            bTAV = new BigDecimal(mInput2);
        } catch (NullPointerException e) {
            System.out.println("读取该银行平均值错误！");
        }
        br2.close();
        file2.close();


        /**
         * 读取参与交易的次数
         */
        FileReader file3 = new FileReader(str+"TransactionTime.txt");
        BufferedReader br3 = new BufferedReader(file3);
        String mInput3 = br3.readLine();
        BigDecimal bTT = null;
        try{
            bTT = new BigDecimal(mInput3);
        } catch (NullPointerException e) {
            System.out.println("读取该银行参与交易次数错误！");
        }
        br3.close();
        file3.close();


        /**
         * 读取该银行的方差
         */
        FileReader file4 = new FileReader(str+"Variance.txt");
        BufferedReader br4 = new BufferedReader(file4);
        String mInput4 = br4.readLine();
        BigDecimal bVariance = null;
        try{
            bVariance = new BigDecimal(mInput4);
        } catch (NullPointerException e) {
            System.out.println("读取该银行的方差错误！");
        }
        br4.close();
        file4.close();


        /**
         * 最重要的一步，验证通过之后，需要将三次资产和转换成BigDecimal类型
         */
        BigDecimal bankTripleAssetsSum1 = transferBigIntegertoBigDecimal(bankTripleAssetsSum);

        java.text.NumberFormat nf = java.text.NumberFormat.getInstance();
        nf.setGroupingUsed(false);
        nf.setMaximumFractionDigits(10);

        /**
         * 计算偏度
         */
        //分子
        BigDecimal molecular = ((bankTripleAssetsSum1.divide(bTT, 8, BigDecimal.ROUND_HALF_UP)).add(bTAV.pow(3).multiply(BigDecimal.valueOf(2)))
                .subtract((bankDoubleAssetsSum.multiply(bTAV).multiply(BigDecimal.valueOf(3))).divide(bTT, 8, BigDecimal.ROUND_HALF_UP))).
                setScale(8, BigDecimal.ROUND_HALF_UP);
        /**
        BigDecimal a = bankTripleAssetsSum1.divide(bTT);
        System.out.println(nf.format(a));
        BigDecimal b = bankDoubleAssetsSum.multiply(bTAV).multiply(BigDecimal.valueOf(3)).divide(bTT);
        System.out.println(nf.format(b));
        BigDecimal c = bTAV.pow(3).multiply(BigDecimal.valueOf(2));
        System.out.println(nf.format(c));
         */
        //分母 方差的二分之三次方
        /**
         * 这个地方不好计算，因为API给的方法只是pow里边的参数是int，而不是double。
         */
//        BigDecimal denominator = bVariance.pow((1.5));
        /**
         * 退而求其次，我们银行的转账一般不会超过2的64次方，所以我们先把方差转换为double类型，然后利用Math的pow方法计算3/2次方。
         */
//
//
//        System.out.println(nf.format(molecular));       //输出的结果 0
//        System.out.println(molecular);                  //输出的结果 0E-24
        double bVariance1 = bVariance.doubleValue();
//        System.out.println(bVariance1);
//        System.out.println("----------------");
        double denominator = Math.pow(bVariance1, 1.5);
        System.out.println(denominator);


        /**
         * 后面假如我们一直使用double的话，分子除以分母的话，是直接取整，没有余数，这样的话，精确度太差，所以我们再次把double类型
         * 的分母转换成BigDecimal类型.
         */
        BigDecimal denominator1 = BigDecimal.valueOf(denominator);  //这样，分母就是BigDecimal
//        System.out.println(denominator1);
//
//
//        /**
//         * 现在分子分母都是BigDecimal类型，所以直接进行除法运算
//         */
        skewness = molecular.divide(denominator1, 8, BigDecimal.ROUND_HALF_UP);
//        System.out.println(skewness);
        String str1 = nf.format(skewness);

        /**
         * 把偏度写入文件
         */
        FileWriter file5 = new FileWriter(str+"Skewness.txt");  //不加true就是覆盖之前写的数据

        file5.write(str1 + "\n");
        file5.close();
        return skewness;
    }
}
