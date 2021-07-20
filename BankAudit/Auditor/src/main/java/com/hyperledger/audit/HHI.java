package com.hyperledger.audit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

public interface HHI {
    /**
     * HHI又称赫芬达尔指数，是衡量企业规模与与行业之间关系的指标，HHI = 市场份额百分比的平方和
     * 此处我们可以看到，需要用到市场占有率，所以需要调用市场占有率的函数，但是我们之前求市场占有率的时候
     * 已经掉用过，现在继续调用的话，需要更多的参数、更多的时间，我们就把需要用到的市场占有率在审计员审计
     * 市场占有率的时候写进文件，以后方便其他地方使用。
     */
    /**
     *
     * @param  ，
     * @return ， 返回HHI,double
     */
    static BigDecimal HHIredix() throws IOException {
        //1、首先我们需要读取每一个银行的市场占有率
        BigDecimal hhi = null;
        BigDecimal rate1 = null;
        BigDecimal rate2 = null;
        BigDecimal rate3 = null;
        BigDecimal rate4 = null;
        FileReader file1 = new FileReader("bank1MarketRate.txt");
        FileReader file2 = new FileReader("bank2MarketRate.txt");
        FileReader file3 = new FileReader("bank3MarketRate.txt");
        FileReader file4 = new FileReader("bank4MarketRate.txt");
        BufferedReader br1 = new BufferedReader(file1);
        BufferedReader br2 = new BufferedReader(file2);
        BufferedReader br3 = new BufferedReader(file3);
        BufferedReader br4 = new BufferedReader(file4);
        String mInput1 = br1.readLine();
        String mInput2 = br2.readLine();
        String mInput3 = br3.readLine();
        String mInput4 = br4.readLine();

        /**
         * 将读取到的string数字转换成double类型
         */
        try {
            rate1 = new BigDecimal(mInput1);
            rate2 = new BigDecimal(mInput2);
            rate3 = new BigDecimal(mInput3);
            rate4 = new BigDecimal(mInput4);
            br1.close();
            br2.close();
            br3.close();
            br4.close();
            file1.close();
            file2.close();
            file3.close();
            file4.close();
            } catch (NumberFormatException e) {
            System.out.println("读取银行市场占有率错误！");
        }

        /**
         * 计算hhi
         */
        hhi = rate1.pow(2).add(rate2.pow(2)).add(rate3.pow(3)).add(rate4.pow(2));

        /**
         * 把HHI写进文件吧
         */
        FileWriter file5 = new FileWriter("HHI.txt");
        file5.write(hhi + "\n");
        file5.close();
        return hhi;
    }
}
