package com.hyperledger.socket;

import com.alibaba.fastjson.JSON;
import com.hyperledger.bank.Bank;
import com.hyperledger.bank.BankAudit;
import com.hyperledger.bank.BankBalanceAndBitZKP;
import com.hyperledger.zkp.HashFunction;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

public class Commit {
    //读取椭圆曲线的生成元
    public static BigInteger[] readBase() throws IOException {
        FileReader file = new FileReader("ecc.txt");
        BufferedReader br = new BufferedReader(file);

        String pInput = br.readLine();
        String qInput = br.readLine();
        String gInput = br.readLine();
        String hInput = br.readLine();

        BigInteger p = null;
        BigInteger q = null;
        BigInteger g = null;
        BigInteger h = null;

        try{
            p = new BigInteger(pInput);
            q = new BigInteger(qInput);
            g = new BigInteger(gInput);
            h = new BigInteger(hInput);
            br.close();
            file.close();
        } catch(NumberFormatException | NullPointerException e){
            System.out.println("读取失败！");
//            break;
//            e.printStackTrace();
        }

        BigInteger[] biginteger = {p, q, g, h};
        return biginteger;
    }

    //新建承诺方法
    public static BigInteger[] commitSelfBank(BigInteger m, BigInteger[] arr) throws IOException {
        //Pedersen承诺的形式c = g^m * h^r (m是要隐藏的金额，r是致盲因子)
        //产生random c，在2到q - 2之间
        BigInteger r1 = GetRandom.getRandom(arr);
        //计算承诺
        BigInteger[] comarr = new BigInteger[6];
        //交易金额的承诺
        comarr[0] = (arr[2].modPow(m, arr[0])).multiply((arr[3]).modPow(r1, arr[0])).mod(arr[0]);
        comarr[1] = BigInteger.valueOf(1);
        //是否为交易的发起者
        comarr[2] = BigInteger.valueOf(0);
        //隐藏值的二次方的承诺
        comarr[3] = BigInteger.valueOf(1);
        //隐藏值的三次方的承诺
        comarr[4] = BigInteger.valueOf(1);
        //隐藏值的四次方的承诺
        comarr[5] = BigInteger.valueOf(1);

        //保存r1
        FileWriter file = new FileWriter("initializeRandom.txt", true);
        file.write(r1+"\n");
        file.close();
        return comarr;

    }



    //四个银行资产初始化生成
    public static void commitAll(BigInteger[] arr) throws IOException {
        //读取每一个银行的资金
        BigInteger bank1Assets = ReadBigInteger.readBigInteger("bank1InitializeAssets.txt");
        BigInteger bank2Assets = ReadBigInteger.readBigInteger("bank2InitializeAssets.txt");
        BigInteger bank3Assets = ReadBigInteger.readBigInteger("bank3InitializeAssets.txt");
        BigInteger bank4Assets = ReadBigInteger.readBigInteger("bank4InitializeAssets.txt");

        BankAudit bank1 = new BankAudit();
        BigInteger[] bank1arr = commitSelfBank(bank1Assets, arr);
        bank1.setCom(bank1arr[0]);
        bank1.setBitCom(bank1arr[1]);
        bank1.setIsFrom((bank1arr[2].intValue()));
        bank1.setDoubleCom(bank1arr[3]);
        bank1.setTripleCom(bank1arr[4]);
        bank1.setQuadraCom(bank1arr[5]);

        BankAudit bank2 = new BankAudit();
        BigInteger[] bank2arr = commitSelfBank(bank2Assets, arr);
        bank2.setCom(bank2arr[0]);
        bank2.setBitCom(bank2arr[1]);
        bank2.setIsFrom((bank2arr[2].intValue()));
        bank2.setDoubleCom(bank2arr[3]);
        bank2.setTripleCom(bank2arr[4]);
        bank2.setQuadraCom(bank2arr[5]);

        BankAudit bank3 = new BankAudit();
        BigInteger[] bank3arr = commitSelfBank(bank3Assets, arr);
        bank3.setCom(bank3arr[0]);
        bank3.setBitCom(bank3arr[1]);
        bank3.setIsFrom((bank3arr[2].intValue()));
        bank3.setDoubleCom(bank3arr[3]);
        bank3.setTripleCom(bank3arr[4]);
        bank3.setQuadraCom(bank3arr[5]);

        BankAudit bank4 = new BankAudit();
        BigInteger[] bank4arr = commitSelfBank(bank4Assets, arr);
        bank4.setCom(bank4arr[0]);
        bank4.setBitCom(bank4arr[1]);
        bank4.setIsFrom((bank4arr[2].intValue()));
        bank4.setDoubleCom(bank4arr[3]);
        bank4.setTripleCom(bank4arr[4]);
        bank4.setQuadraCom(bank4arr[5]);

        mappingGen(bank1, bank2, bank3, bank4);
    }

    /**
     *
     * @param bank1
     * @param bank2
     * @param bank3
     * @param bank4
     * @throws IOException
     */

    public static void mappingGen(BankAudit bank1, BankAudit bank2, BankAudit bank3, BankAudit bank4) throws IOException {
        //银行与银行里边的每个字段对应是key-value,其中key是字符串"bank1", value是bank对象
        //新建TreeMap,按键值进行排序
        TreeMap<String, BankAudit> treemap = new TreeMap<>();
        treemap.put("bank1", bank1);
        treemap.put("bank2", bank2);
        treemap.put("bank3", bank3);
        treemap.put("bank4", bank4);

        String str = JSON.toJSONString(treemap);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = "{" + "\"time\":" + "\"" + df.format(new Date()) + "\""+",\"bank1\"";
        str = str.replace("{\"bank1\"",time);
//        System.out.println(str);

        FileWriter file = new FileWriter("initTransaction.txt",true);
        file.write(str+"\n");
        file.close();
    }


}
