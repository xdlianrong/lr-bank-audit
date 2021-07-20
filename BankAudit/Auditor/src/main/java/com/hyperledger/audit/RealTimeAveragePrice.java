package com.hyperledger.audit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

public interface RealTimeAveragePrice {
    //实时平均价格
    /*
    *   说的就是在一段时间之内，整个系统中金额的流通额除以这段时间之内发生的交易数
    * */
    /**
     * 要求实时的平均价格，按照需求是审计员输入起始时间，但是那个过程的话，对于智能合约来说操作起来有点麻烦，
     * 所以我们直接把在审计时获取到的系统和，作为这一段时间的交易总资产。也就是直接从文件中读取，作为分子。
     * 分母就是在这段时间发生的交易数，但是我们不可能去从账本上获取交易的次数。交易的次数我们不是有每一个银行的交易次数吗？
     * 我们直接把这些银行交易的次数加起来，然后除以2就是从上一次资产结算审计之后系统发生的交易次数。
     */
    static BigDecimal realTimeAveragePrice(String str1,
                                                  String str2,
                                                  String str3,
                                                  String str4) throws IOException {
        /**
         * 1、首先读取系统的资产和
         */
        FileReader file1 = new FileReader("systemSum.txt");
        BufferedReader br1 = new BufferedReader(file1);
        String mInput1 = br1.readLine();
        BigDecimal rSystemSum = null;
        try {
            rSystemSum = new BigDecimal(mInput1);
            br1.close();
            file1.close();
        } catch (NumberFormatException e){
            System.out.println("再求实时平均价格时，读取系统和错误！");;
        }

        /**
         * 读取银行1这段时间的交易次数
         */
        FileReader file2 = new FileReader(str1+"TransactionTime.txt");
        BufferedReader br2 = new BufferedReader(file2);
        String mInput2 = br2.readLine();
        BigDecimal bank1BTT = null;
        try {
            bank1BTT = new BigDecimal(mInput2);
            br2.close();
            file2.close();
        } catch (NumberFormatException e){
            System.out.println("在求实时平均价格时，读取银行" + str1 + "错误！");
        }

        /**
         * 读取银行2这段时间的交易次数
         */
        FileReader file3 = new FileReader(str2+"TransactionTime.txt");
        BufferedReader br3 = new BufferedReader(file3);
        String mInput3 = br3.readLine();
        BigDecimal bank2BTT = null;
        try {
            bank2BTT = new BigDecimal(mInput3);
            br3.close();
            file3.close();
        } catch (NumberFormatException e){
            System.out.println("在求实时平均价格时，读取银行" + str2 + "错误！");
        }

        /**
         * 读取银行3这段时间的交易次数
         */
        FileReader file4 = new FileReader(str3+"TransactionTime.txt");
        BufferedReader br4 = new BufferedReader(file4);
        String mInput4 = br4.readLine();
        BigDecimal bank3BTT = null;
        try {
            bank3BTT = new BigDecimal(mInput4);
            br4.close();
            file4.close();
        } catch (NumberFormatException e) {
            System.out.println("在求实时平均价格时，读取银行" + str3 + "错误！");
        }

        /**
         * 读取银行4这段时间的交易次数
         */
        FileReader file5 = new FileReader(str4+"TransactionTime.txt");
        BufferedReader br5 = new BufferedReader(file5);
        String mInput5 = br5.readLine();
        BigDecimal bank4BTT = null;
        try {
            bank4BTT = new BigDecimal(mInput5);
            br5.close();
            file5.close();
        } catch (NumberFormatException e){
            System.out.println("在求实时平均价格时，读取银行" + str4 + "错误");
        }

        /**
         * 求交易次数
         */
        BigDecimal systemTT = (bank1BTT.add(bank2BTT).add(bank3BTT).add(bank4BTT)).divide(BigDecimal.valueOf(2));

        /**
         * 系统实时交易平均值
         */
        BigDecimal rTSAV = rSystemSum.divide(systemTT, 8, BigDecimal.ROUND_HALF_UP);
        FileWriter file6 = new FileWriter("systemRealTransactionAverageValue.txt");
        //把平均值写进文件
        file6.write(rTSAV + "\n");
        file6.close();
        return rTSAV;
    }
}
