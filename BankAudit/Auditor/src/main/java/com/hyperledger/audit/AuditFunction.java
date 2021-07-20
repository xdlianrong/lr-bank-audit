package com.hyperledger.audit;

import com.hyperledger.socket.Client;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

import static com.hyperledger.connection.ReadBase.readBase;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2020/12/29
 * \* Time: 11:46
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */

/**
 * 这个函数主要进行审计者对账本资产进行结算审计
 */
public class AuditFunction {
    public static void auditLedger() throws IOException {
        Scanner scanner = new Scanner(System.in);
        /**
         * 审计员和银行进行交互，得到相应的承诺的致盲因子的和
         */
        String[] bankNum = {"bank1", "bank2", "bank3", "bank4"};
        Integer[] portNum = {8881, 8882, 8883, 8884};
        for (int i = 0; i < portNum.length; i++) {
            Client.client("2", bankNum[i], "127.0.0.1", portNum[i]);
        }
        System.out.println("资产审计员对银行资产进行结算审计：");
        while (true){
            System.out.println("============================");
            System.out.println("请输入审计模式：");
            System.out.println("1. 银行指标");
            System.out.println("2. 系统指标");
            System.out.println("3. 退出审计");
            String str = scanner.nextLine();

            if(str.equals("1")) {
                System.out.println("请选择资产结算审计银行：");
                System.out.println("1. bank1");
                System.out.println("2. bank2");
                System.out.println("3. bank3");
                System.out.println("4. bank4");
                System.out.println("5. exit");
                String str1 = scanner.nextLine();
                if(str1.equals("1")){
                    while(true){
                        System.out.println("请输入资产结算审计指标：");
                        System.out.println("1. AssetsSum");
                        System.out.println("2. TransactionAverageValue");
                        System.out.println("3. TransactionVariance");
                        System.out.println("4. TransactionSkewness");
                        System.out.println("5. TransactionKurtosis");
                        System.out.println("6. exit");
                        String str2 = scanner.nextLine();
                        if (str2.equals("1")){
                            System.out.println("输入银行1的资产和和交易金额的承诺的致盲因子和：");
                            BigInteger bankAssetsSum = scanner.nextBigInteger();
                            BigInteger bankRandomSum = scanner.nextBigInteger();
                            Sum.bankAssetsSum("bank1",bankAssetsSum, bankRandomSum, readBase());
                        } else if (str2.equals("2")) {
                            System.out.println("输入银行1的交易次数和和比特承诺的致盲因子和：");
                            BigInteger bankBitComSum = scanner.nextBigInteger();
                            BigInteger bankBitComRandomSum = scanner.nextBigInteger();
                            BigDecimal bTAV = Sum.bankTransactionAverageValue("bank1", bankBitComSum, bankBitComRandomSum, readBase());
                            System.out.println("bank1交易平均值= " + bTAV);
                        } else if (str2.equals("3")) {
                            System.out.println("输入银行1的交易金额平方和和交易金额平方承诺的致盲因子和：");
                            BigInteger bankDoubleAssetsSum = scanner.nextBigInteger();
                            BigInteger bankDoubleComRandomSum = scanner.nextBigInteger();
                            BigDecimal variance = Variance.calculateVariance("bank1", bankDoubleAssetsSum, bankDoubleComRandomSum, readBase());
                            System.out.println("bank1交易方差= " + variance);
                        } else if (str2.equals("4")) {
                            System.out.println("输入银行1的交易金额立方和和交易金额立方承诺的致盲因子和：");
                            BigInteger bankTripleAssetsSum = scanner.nextBigInteger();
                            BigInteger bankTripleComRandomSum = scanner.nextBigInteger();
                            BigDecimal skewness = Skewness.calaulateSkewness("bank1", bankTripleAssetsSum, bankTripleComRandomSum, readBase());
                            System.out.println("bank1交易偏度= " + skewness);
                        } else if (str2.equals("5")) {
                            System.out.println("输入银行1的交易金额四次方和和交易金额四次方承诺的致盲因子和：");
                            BigInteger bankQuadraAssetsSum = scanner.nextBigInteger();
                            BigInteger bankQuadraComRandomSum = scanner.nextBigInteger();
                            BigDecimal kurtosis = Kurtosis.calculateKurtosis("bank1", bankQuadraAssetsSum, bankQuadraComRandomSum, readBase());
                            System.out.println("bank1交易峰度= " + kurtosis);
                        } else {
                            break;
                        }
                    }
                }else if (str1.equals("2")) {
                    while(true){
                        System.out.println("请输入资产结算审计指标：");
                        System.out.println("1. AssetsSum");
                        System.out.println("2. TransactionAverageValue");
                        System.out.println("3. TransactionVariance");
                        System.out.println("4. TransactionSkewness");
                        System.out.println("5. TransactionKurtosis");
                        System.out.println("6. exit");
                        String str2 = scanner.nextLine();
                        if (str2.equals("1")){
                            System.out.println("输入银行2的资产和和交易金额的承诺的致盲因子和：");
                            BigInteger bankAssetsSum = scanner.nextBigInteger();
                            BigInteger bankRandomSum = scanner.nextBigInteger();
                            Sum.bankAssetsSum("bank2",bankAssetsSum, bankRandomSum, readBase());
                        } else if (str2.equals("2")) {
                            System.out.println("输入银行2的交易次数和和比特承诺的致盲因子和：");
                            BigInteger bankBitComSum = scanner.nextBigInteger();
                            BigInteger bankBitComRandomSum = scanner.nextBigInteger();
                            BigDecimal bTAV = Sum.bankTransactionAverageValue("bank2", bankBitComSum, bankBitComRandomSum, readBase());
                            System.out.println("bank2交易平均值= " + bTAV);
                        } else if (str2.equals("3")) {
                            System.out.println("输入银行2的交易金额平方和和交易金额平方承诺的致盲因子和：");
                            BigInteger bankDoubleAssetsSum = scanner.nextBigInteger();
                            BigInteger bankDoubleComRandomSum = scanner.nextBigInteger();
                            BigDecimal variance = Variance.calculateVariance("bank2", bankDoubleAssetsSum, bankDoubleComRandomSum, readBase());
                            System.out.println("bank2交易方差= " + variance);
                        } else if (str2.equals("4")) {
                            System.out.println("输入银行2的交易金额立方和和交易金额立方承诺的致盲因子和：");
                            BigInteger bankTripleAssetsSum = scanner.nextBigInteger();
                            BigInteger bankTripleComRandomSum = scanner.nextBigInteger();
                            BigDecimal skewness = Skewness.calaulateSkewness("bank2", bankTripleAssetsSum, bankTripleComRandomSum, readBase());
                            System.out.println("bank2交易偏度= " + skewness);
                        } else if (str2.equals("5")) {
                            System.out.println("输入银行2的交易金额四次方和和交易金额四次方承诺的致盲因子和：");
                            BigInteger bankQuadraAssetsSum = scanner.nextBigInteger();
                            BigInteger bankQuadraComRandomSum = scanner.nextBigInteger();
                            BigDecimal kurtosis = Kurtosis.calculateKurtosis("bank2", bankQuadraAssetsSum, bankQuadraComRandomSum, readBase());
                            System.out.println("bank2交易峰度= " + kurtosis);
                        } else {
                            break;
                        }
                    }
                } else if (str1.equals("3")) {
                    while(true){
                        System.out.println("请输入资产结算审计指标：");
                        System.out.println("1. AssetsSum");
                        System.out.println("2. TransactionAverageValue");
                        System.out.println("3. TransactionVariance");
                        System.out.println("4. TransactionSkewness");
                        System.out.println("5. TransactionKurtosis");
                        System.out.println("6. exit");
                        String str2 = scanner.nextLine();
                        if (str2.equals("1")){
                            System.out.println("输入银行3的资产和和交易金额的承诺的致盲因子和：");
                            BigInteger bankAssetsSum = scanner.nextBigInteger();
                            BigInteger bankRandomSum = scanner.nextBigInteger();
                            Sum.bankAssetsSum("bank3",bankAssetsSum, bankRandomSum, readBase());
                        } else if (str2.equals("2")) {
                            System.out.println("输入银行3的交易次数和和比特承诺的致盲因子和：");
                            BigInteger bankBitComSum = scanner.nextBigInteger();
                            BigInteger bankBitComRandomSum = scanner.nextBigInteger();
                            BigDecimal bTAV = Sum.bankTransactionAverageValue("bank3", bankBitComSum, bankBitComRandomSum, readBase());
                            System.out.println("bank3交易平均值= " + bTAV);
                        } else if (str2.equals("3")) {
                            System.out.println("输入银行3的交易金额平方和和交易金额平方承诺的致盲因子和：");
                            BigInteger bankDoubleAssetsSum = scanner.nextBigInteger();
                            BigInteger bankDoubleComRandomSum = scanner.nextBigInteger();
                            BigDecimal variance = Variance.calculateVariance("bank3", bankDoubleAssetsSum, bankDoubleComRandomSum, readBase());
                            System.out.println("bank3交易方差= " + variance);
                        } else if (str2.equals("4")) {
                            System.out.println("输入银行3的交易金额立方和和交易金额立方承诺的致盲因子和：");
                            BigInteger bankTripleAssetsSum = scanner.nextBigInteger();
                            BigInteger bankTripleComRandomSum = scanner.nextBigInteger();
                            BigDecimal skewness = Skewness.calaulateSkewness("bank3", bankTripleAssetsSum, bankTripleComRandomSum, readBase());
                            System.out.println("bank3交易偏度= " + skewness);
                        } else if (str2.equals("5")) {
                            System.out.println("输入银行3的交易金额四次方和和交易金额四次方承诺的致盲因子和：");
                            BigInteger bankQuadraAssetsSum = scanner.nextBigInteger();
                            BigInteger bankQuadraComRandomSum = scanner.nextBigInteger();
                            BigDecimal kurtosis = Kurtosis.calculateKurtosis("bank3", bankQuadraAssetsSum, bankQuadraComRandomSum, readBase());
                            System.out.println("bank3交易峰度= " + kurtosis);
                        } else {
                            break;
                        }
                    }
                } else if (str1.equals("4")) {
                    while(true){
                        System.out.println("请输入资产结算审计指标：");
                        System.out.println("1. AssetsSum");
                        System.out.println("2. TransactionAverageValue");
                        System.out.println("3. TransactionVariance");
                        System.out.println("4. TransactionSkewness");
                        System.out.println("5. TransactionKurtosis");
                        System.out.println("6. exit");
                        String str2 = scanner.nextLine();
                        if (str2.equals("1")){
                            System.out.println("输入银行4的资产和和交易金额的承诺的致盲因子和：");
                            BigInteger bankAssetsSum = scanner.nextBigInteger();
                            BigInteger bankRandomSum = scanner.nextBigInteger();
                            Sum.bankAssetsSum("bank4",bankAssetsSum, bankRandomSum, readBase());
                        } else if (str2.equals("2")) {
                            System.out.println("输入银行4的交易次数和和比特承诺的致盲因子和：");
                            BigInteger bankBitComSum = scanner.nextBigInteger();
                            BigInteger bankBitComRandomSum = scanner.nextBigInteger();
                            BigDecimal bTAV = Sum.bankTransactionAverageValue("bank4", bankBitComSum, bankBitComRandomSum, readBase());
                            System.out.println("bank4交易平均值= " + bTAV);
                        } else if (str2.equals("3")) {
                            System.out.println("输入银行4的交易金额平方和和交易金额平方承诺的致盲因子和：");
                            BigInteger bankDoubleAssetsSum = scanner.nextBigInteger();
                            BigInteger bankDoubleComRandomSum = scanner.nextBigInteger();
                            BigDecimal variance = Variance.calculateVariance("bank4", bankDoubleAssetsSum, bankDoubleComRandomSum, readBase());
                            System.out.println("bank4交易方差= " + variance);
                        } else if (str2.equals("4")) {
                            System.out.println("输入银行4的交易金额立方和和交易金额立方承诺的致盲因子和：");
                            BigInteger bankTripleAssetsSum = scanner.nextBigInteger();
                            BigInteger bankTripleComRandomSum = scanner.nextBigInteger();
                            BigDecimal skewness = Skewness.calaulateSkewness("bank4", bankTripleAssetsSum, bankTripleComRandomSum, readBase());
                            System.out.println("bank4交易偏度= " + skewness);
                        } else if (str2.equals("5")) {
                            System.out.println("输入银行4的交易金额四次方和和交易金额四次方承诺的致盲因子和：");
                            BigInteger bankQuadraAssetsSum = scanner.nextBigInteger();
                            BigInteger bankQuadraComRandomSum = scanner.nextBigInteger();
                            BigDecimal kurtosis = Kurtosis.calculateKurtosis("bank4", bankQuadraAssetsSum, bankQuadraComRandomSum, readBase());
                            System.out.println("bank4交易峰度= " + kurtosis);
                        } else {
                            break;
                        }
                    }
                }
            } else if (str.equals("2")){
                while (true){
                    System.out.println("结算审计系统资产情况：");
                    System.out.println("1. SystemAssetsSum");
                    System.out.println("2. MarketRate");
                    System.out.println("3. RealTimeTransactionAverageValue");
                    System.out.println("4. HHIIndex");
                    String str3 = scanner.nextLine();
                    if (str3.equals("1")) {
                        BigInteger systemSum = new BigInteger("0");
                        try {
                            systemSum = MarketRate.systemAssetsSum("bank1", "bank2", "bank3", "bank4");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            System.out.println("整个系统的资产和= " + systemSum);
                        }
                    } else if (str3.equals("2")){
                        BigDecimal bank1MarketRate = new BigDecimal("0");
                        BigDecimal bank2MarketRate = new BigDecimal("0");
                        BigDecimal bank3MarketRate = new BigDecimal("0");
                        BigDecimal bank4MarketRate = new BigDecimal("0");
                        try {
                            bank1MarketRate = MarketRate.marketRate("bank1");
                            bank2MarketRate = MarketRate.marketRate("bank2");
                            bank3MarketRate = MarketRate.marketRate("bank3");
                            bank4MarketRate = MarketRate.marketRate("bank4");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            System.out.println("bank1的市场占有率：" + bank1MarketRate);
                            System.out.println("bank2的市场占有率：" + bank2MarketRate);
                            System.out.println("bank3的市场占有率：" + bank3MarketRate);
                            System.out.println("bank4的市场占有率：" + bank4MarketRate);
                        }
                    } else if (str3.equals("3")) {
                        BigDecimal rTSAV = new BigDecimal("0");
                        try {
                            rTSAV = RealTimeAveragePrice.realTimeAveragePrice("bank1", "bank2", "bank3", "bank4");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            System.out.println("系统的实时平均价格：" + rTSAV);
                        }
                    } else if (str3.equals("4")) {
                        BigDecimal hhi = new BigDecimal("0");
                        try {
                            hhi = HHI.HHIredix();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            System.out.println("系统的HHI指数：" + hhi);
                        }
                    } else {
                        break;
                    }
                }
            }else if (str.equals("3")){
                break;
            } else {
                System.out.println("输入正确的指标参数！");
            }
        }
    }
}
