package com.hyperledger.audit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public interface MarketRate {
    //市场占有率
    /**
     * 市场占有率：就是在审计此刻之前，被审计银行拥有的资产数目比整个系统之中的资产总数目。
     *
     *  从下面的函数看，虽然我们一次性把一个功能都放在了一个函数当中，但是这样极度不友好，因为后面我们计算HHI的时候也要计算系统资产和
     *  这样就又要用到系统和的函数，但是我们没有这个函数的返回值，所以我们现在把下面的三个函数分成三份，这样有利于后续的计算。
     */

    //1、求整个系统的资产和,参数中的是银行、银行给的资产和、随机数和、账本。
    static BigInteger systemAssetsSum(String str1,
                                             String str2,
                                             String str3,
                                             String str4
                                             ) throws IOException {
        //审计每一个银行的资金，从账本和银行返回的值验证
        //首先调用银行资产和的审计函数，去检验银行给的和是不是正确，之后进行求平均值运算
        /**
         * 这样的结果时自此调用验证资产和的函数，不友好，因为之前审计和的时候已经调用过函数了，并已经把正确的资产和写入了文件，现在只需
         * 去文件读取即可。
         */
        /**
         * 银行1
         */
        FileReader file1 = new FileReader(str1+"Sum.txt");
        BufferedReader br1 = new BufferedReader(file1);
        String mInput1 = br1.readLine();
        BigInteger bank1AssetsSum = null;
        try{
            bank1AssetsSum = new BigInteger(mInput1);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        br1.close();
        file1.close();

        /**
         * 银行2
         */
        FileReader file2 = new FileReader(str2+"Sum.txt");
        BufferedReader br2 = new BufferedReader(file2);
        String mInput2 = br2.readLine();
        BigInteger bank2AssetsSum = null;
        try{
            bank2AssetsSum = new BigInteger(mInput2);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        br2.close();
        file2.close();

        /**
         * 银行3
         */
        FileReader file3 = new FileReader(str3+"Sum.txt");
        BufferedReader br3 = new BufferedReader(file3);
        String mInput3 = br3.readLine();
        BigInteger bank3AssetsSum = null;
        try{
            bank3AssetsSum = new BigInteger(mInput3);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        br3.close();
        file3.close();

        /**
         * 银行4
         */
        FileReader file4 = new FileReader(str4+"Sum.txt");
        BufferedReader br4 = new BufferedReader(file4);
        String mInput4 = br4.readLine();
        BigInteger bank4AssetsSum = null;
        try{
            bank4AssetsSum = new BigInteger(mInput4);
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        br4.close();
        file4.close();


        //总金额
        BigInteger systemSum = null;
        systemSum = bank1AssetsSum.add(bank2AssetsSum).add(bank3AssetsSum).add(bank4AssetsSum);


        /**
         * 此处我们把系统的资产和写进一个文件吧
         */
        FileWriter file = new FileWriter("systemSum.txt");  //不加true就是覆盖之前写的的系统和
        file.write(systemSum + "\n");
        file.close();
        return systemSum;

    }

    //2、求每个银行对应的市场占有率
    static BigDecimal marketRate(String str) throws IOException {
        /**
         * 1、首先读取系统的资产总和
         */
        FileReader file = new FileReader("systemSum.txt");
        BufferedReader br = new BufferedReader(file);
        String mInput = br.readLine();
        /**
         * 此时从文件读取的数字是整个系统的资产和，要进行市场占有率的计算，有小数点，所以应该是BigDecical类型
         */
        BigDecimal systemSum = null;
        try {
            systemSum = new BigDecimal(mInput);
        } catch (NumberFormatException e){
            System.out.println("读取系统资产和读取失败！");
        }
        br.close();
        file.close();

        /**
         * 银行的资产也不用需要审计员输入，而是直接从结算资产和之后的文件读取
         */
        FileReader file1 = new FileReader(str+"Sum.txt");
        BufferedReader br1 = new BufferedReader(file1);
        String mInput1 = br1.readLine();
        BigDecimal bankAssetsSum = null;
        try{
            bankAssetsSum = new BigDecimal(mInput1);
        } catch(NumberFormatException e){
            System.out.println("读取该银行资产和错误！");
        }
        br1.close();
        file1.close();


        //求市场占有率
        BigDecimal bankrate = bankAssetsSum.divide(systemSum, 8, BigDecimal.ROUND_HALF_UP);

        /**
         * 此时计算出来的市场占有率在后面的HHI计算时要用到，所以我们直接把结果写进文件，方便后续使用。
         */

        FileWriter file2 = new FileWriter(str+"MarketRate.txt");
        file2.write(bankrate + "\n");
        file2.close();
        return bankrate;
    }
}
