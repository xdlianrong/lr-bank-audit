package com.hyperledger.zkp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyperledger.bank.Bank;
import com.hyperledger.bank.BankBalanceAndBitZKP;
import com.hyperledger.commit.ReadVectorFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2021/3/3
 * \* Time: 15:01
 * \* To change this template use File | Settings | File Templates.
 * \* Description:   该类主要是实现零知识证明的验证
 * \
 */
public class ZKPProof implements generateComIndexProof {
    //零知识证明的主函数，并从链上拉取的json中获取每一个字段
    public static void ReadFieldAndZKP(BigInteger[] arr) throws IOException {
//        boolean result = false;
        /**
         * 首先，审计员从链上拉取下来一条交易记录之后，此交易记录保存在oneTransaction.txt文件中，然后审计员对这一条交易进行正确性检验，首先就是平衡证明，
         * 即从oneTransaction.txt文件中读取第一行，然后进行验证。验证通过之后，把这条交易记录保存到allTransaction.txt文件中，以便后续的审计。
         */
        //首先从oneTransaction.txt读取该笔交易
        FileReader file1 = new FileReader("oneTransaction.txt");
        BufferedReader br = new BufferedReader(file1);
        String tempInput = br.readLine();
        String str1 = tempInput.replaceFirst("\\{\"time\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\",", "{");
        //读取json字符串的时候不能出现\,否则报错
//        System.out.println(str1);
        JSONObject bankJson1 = JSON.parseObject(str1).getJSONObject("bank1");
        JSONObject bankJson2 = JSON.parseObject(str1).getJSONObject("bank2");
        JSONObject bankJson3 = JSON.parseObject(str1).getJSONObject("bank3");
        JSONObject bankJson4 = JSON.parseObject(str1).getJSONObject("bank4");
        Bank bank1 = JSON.parseObject(String.valueOf(bankJson1), Bank.class);
        Bank bank2 = JSON.parseObject(String.valueOf(bankJson2), Bank.class);
        Bank bank3 = JSON.parseObject(String.valueOf(bankJson3), Bank.class);
        Bank bank4 = JSON.parseObject(String.valueOf(bankJson4), Bank.class);
        JSONObject zkpFieldjson = JSON.parseObject(str1).getJSONObject("balProofParam");
        BankBalanceAndBitZKP zkpField = JSON.parseObject(String.valueOf(zkpFieldjson), BankBalanceAndBitZKP.class);


        /**
         * 首先判断是那个银行是支出行，就是依靠isFrom==1
         */
        Bank bankFrom = new Bank();
        Bank bankTo1 = new Bank();
        Bank bankTo2 = new Bank();
        Bank bankTo3 = new Bank();

        if (bank1.getisFrom() == 1){
            bankFrom = bank1;
            bankTo1 = bank2;
            bankTo2 = bank3;
            bankTo3 = bank4;
        }
        if (bank2.getisFrom() == 1){
            bankFrom = bank2;
            bankTo1 = bank1;
            bankTo2 = bank3;
            bankTo3 = bank4;
        }
        if (bank3.getisFrom() == 1){
            bankFrom = bank3;
            bankTo1 = bank1;
            bankTo2 = bank2;
            bankTo3 = bank4;
        }
        if (bank4.getisFrom() == 1){
            bankFrom = bank4;
            bankTo1 = bank1;
            bankTo2 = bank2;
            bankTo3 = bank3;
        }
        /**
         * 资产平衡证明，证明转出的资金等于转入的资金
         */
        BigInteger P = bankFrom.getCom();
        BigInteger Q = (bankTo1.getCom().multiply(bankTo2.getCom().multiply(bankTo3.getCom()))).mod(arr[0]);
        BigInteger c1 = zkpField.getC1();
        BigInteger d = zkpField.getD();
        BigInteger d1 = zkpField.getD1();
        BigInteger d2 = zkpField.getD2();
//        System.out.println(c1);

        boolean result1 = BalanceProof.EqualityOfMessageInPedersenCoomiment(P, Q, c1, d, d1, d2, arr);
//        System.out.println(result1);

        /**
         * 证明比特承诺的隐藏值前后相等
         */
        BigInteger E = bankFrom.getBitCom();
        BigInteger F = (bankTo1.getBitCom().multiply(bankTo2.getBitCom().multiply(bankTo3.getBitCom()))).mod(arr[0]);
        BigInteger c2 = zkpField.getC2();
        BigInteger m = zkpField.getM();
        BigInteger m1 = zkpField.getM1();
        BigInteger m2 = zkpField.getM2();

        boolean result2 = BalanceProof.EqualityOfMessageInPedersenCoomiment(E, F, c2, m, m1, m2, arr);
//        System.out.println(result2);

        /**
         * bank1-bitCom隐藏值非0即1证明
         */
        BigInteger bank1C = bank1.getBitCom();
        BigInteger bank1C1 = bank1.getBitComTemp1();
        BigInteger bank1C2 = bank1.getBitComTemp2();
        BigInteger bank1x = bank1.getChallenge();
        BigInteger bank1hash_x = bank1.getHash_challenge();
        BigInteger bank1f = bank1.getBitComResponse1();
        BigInteger bank1Z1 = bank1.getBitComResponse2();
        BigInteger bank1Z2 = bank1.getBitComResponse3();

        boolean bank1BitReslut = If0Else1Proof.bit01Proof(bank1C, bank1C1, bank1C2, bank1x, bank1hash_x, bank1f, bank1Z1, bank1Z2, arr);
//        System.out.println(bank1BitReslut);

        /**
         * bank2-bitCom隐藏值非0即1证明
         */
        BigInteger bank2C = bank2.getBitCom();
        BigInteger bank2C1 = bank2.getBitComTemp1();
        BigInteger bank2C2 = bank2.getBitComTemp2();
        BigInteger bank2x = bank2.getChallenge();
        BigInteger bank2hash_x = bank2.getHash_challenge();
        BigInteger bank2f = bank2.getBitComResponse1();
        BigInteger bank2Z1 = bank2.getBitComResponse2();
        BigInteger bank2Z2 = bank2.getBitComResponse3();

        boolean bank2BitReslut = If0Else1Proof.bit01Proof(bank2C, bank2C1, bank2C2, bank2x, bank2hash_x, bank2f, bank2Z1, bank2Z2, arr);
//        System.out.println(bank2BitReslut);

        /**
         * bank3-bitCom隐藏值非0即1证明
         */
        BigInteger bank3C = bank3.getBitCom();
        BigInteger bank3C1 = bank3.getBitComTemp1();
        BigInteger bank3C2 = bank3.getBitComTemp2();
        BigInteger bank3x = bank3.getChallenge();
        BigInteger bank3hash_x = bank3.getHash_challenge();
        BigInteger bank3f = bank3.getBitComResponse1();
        BigInteger bank3Z1 = bank3.getBitComResponse2();
        BigInteger bank3Z2 = bank3.getBitComResponse3();

        boolean bank3BitReslut = If0Else1Proof.bit01Proof(bank3C, bank3C1, bank3C2, bank3x, bank3hash_x, bank3f, bank3Z1, bank3Z2, arr);
//        System.out.println(bank3BitReslut);

        /**
         * bank4-bitCom隐藏值非0即1证明
         */
        BigInteger bank4C = bank4.getBitCom();
        BigInteger bank4C1 = bank4.getBitComTemp1();
        BigInteger bank4C2 = bank4.getBitComTemp2();
        BigInteger bank4x = bank4.getChallenge();
        BigInteger bank4hash_x = bank4.getHash_challenge();
        BigInteger bank4f = bank4.getBitComResponse1();
        BigInteger bank4Z1 = bank4.getBitComResponse2();
        BigInteger bank4Z2 = bank4.getBitComResponse3();

        boolean bank4BitReslut = If0Else1Proof.bit01Proof(bank4C, bank4C1, bank4C2, bank4x, bank4hash_x, bank4f, bank4Z1, bank4Z2, arr);
//        System.out.println(bank4BitReslut);

        /**
         * 证明所有bitCom乘积的隐藏值为2
         */
        BigInteger bankNum = zkpField.getTwoObject();
        BigInteger r2Sum = zkpField.getAllR2_Sum();
        boolean twoResult = twoObject.twoObjectCompare(bank2C, bank1C, bank3C, bank4C, bankNum, r2Sum, arr);
//        System.out.println(twoResult);

        /**
         * 证明g的s次方
         */
        BigInteger bank1temp1 = bank1.getTemp1();
        BigInteger bank1tempC = bank1.getTemp1C();
        BigInteger bank1tempS = bank1.getTemp1S();
        boolean bank1_g_s_result = BalanceProof.SigmaProtocols(bank1temp1, bank1tempC, bank1tempS, arr);
//        System.out.println(bank1_g_s_result);
        BigInteger bank2temp1 = bank2.getTemp1();
        BigInteger bank2tempC = bank2.getTemp1C();
        BigInteger bank2tempS = bank2.getTemp1S();
        boolean bank2_g_s_result = BalanceProof.SigmaProtocols(bank2temp1, bank2tempC, bank2tempS, arr);
//        System.out.println(bank2_g_s_result);
        BigInteger bank3temp1 = bank3.getTemp1();
        BigInteger bank3tempC = bank3.getTemp1C();
        BigInteger bank3tempS = bank3.getTemp1S();
        boolean bank3_g_s_result = BalanceProof.SigmaProtocols(bank3temp1, bank3tempC, bank3tempS, arr);
        BigInteger bank4temp1 = bank4.getTemp1();
        BigInteger bank4tempC = bank4.getTemp1C();
        BigInteger bank4tempS = bank4.getTemp1S();
        boolean bank4_g_s_result = BalanceProof.SigmaProtocols(bank4temp1, bank4tempC, bank4tempS, arr);

        /**
         * 验证的是comarr[17]是comarr[0]的1/0+s次方
         */
        BigInteger bank1ComTemp1 = bank1.getComTemp1();
        BigInteger bank1ComTemp1C = bank1.getComTemp1C();
        BigInteger bank1Comtemp1S = bank1.getComTemp1S();
        boolean bank1_com_1or0_s_result = BalanceProof.com1Or0AddsProtocols(bank1.getCom(), bank1ComTemp1, bank1ComTemp1C, bank1Comtemp1S, arr);
//        System.out.println(bank1_com_1or0_s_result);
        BigInteger bank2ComTemp1 = bank2.getComTemp1();
        BigInteger bank2ComTemp1C = bank2.getComTemp1C();
        BigInteger bank2Comtemp1S = bank2.getComTemp1S();
        boolean bank2_com_1or0_s_result = BalanceProof.com1Or0AddsProtocols(bank2.getCom(), bank2ComTemp1, bank2ComTemp1C, bank2Comtemp1S, arr);
//        System.out.println(bank2_com_1or0_s_result);
        BigInteger bank3ComTemp1 = bank3.getComTemp1();
        BigInteger bank3ComTemp1C = bank3.getComTemp1C();
        BigInteger bank3Comtemp1S = bank3.getComTemp1S();
        boolean bank3_com_1or0_s_result = BalanceProof.com1Or0AddsProtocols(bank3.getCom(), bank3ComTemp1, bank3ComTemp1C, bank3Comtemp1S, arr);
//        System.out.println(bank3_com_1or0_s_result);
        BigInteger bank4ComTemp1 = bank4.getComTemp1();
        BigInteger bank4ComTemp1C = bank4.getComTemp1C();
        BigInteger bank4Comtemp1S = bank4.getComTemp1S();
        boolean bank4_com_1or0_s_result = BalanceProof.com1Or0AddsProtocols(bank4.getCom(), bank4ComTemp1, bank4ComTemp1C, bank4Comtemp1S, arr);

        /**
         * 证明指数相等
         */
        BigInteger bank1com2 = bank1.getBitComMulTemp1();
        BigInteger bank1com3 = bank1.getComTemp1();
        //t是三个随机数的承诺
        BigInteger bank1t = bank1.getIndexEqualTemp1();
        //下面三个是响应
        BigInteger bank1s1 = bank1.getIndexEqualTemp2();
        BigInteger bank1s2 = bank1.getIndexEqualTemp3();
        BigInteger bank1s3 = bank1.getIndexEqualTemp4();
//        System.out.println(s1);
//        System.out.println(s2);
//        System.out.println(s3);
        boolean bank1Indexresult = IndexEqualsProof.indexEqualsProof(bank1.getCom(), bank1com2, bank1com3, bank1t, bank1s1, bank1s2, bank1s3, arr);
//        System.out.println(bank1Indexresult);

        BigInteger bank2com2 = bank2.getBitComMulTemp1();
        BigInteger bank2com3 = bank2.getComTemp1();
        BigInteger bank2t = bank2.getIndexEqualTemp1();
        BigInteger bank2s1 = bank2.getIndexEqualTemp2();
        BigInteger bank2s2 = bank2.getIndexEqualTemp3();
        BigInteger bank2s3 = bank2.getIndexEqualTemp4();
        boolean bank2Indexresult = IndexEqualsProof.indexEqualsProof(bank2.getCom(), bank2com2, bank2com3, bank2t, bank2s1, bank2s2, bank2s3, arr);
//        System.out.println(bank2Indexresult);

        BigInteger bank3com2 = bank3.getBitComMulTemp1();
        BigInteger bank3com3 = bank3.getComTemp1();
        BigInteger bank3t = bank3.getIndexEqualTemp1();
        BigInteger bank3s1 = bank3.getIndexEqualTemp2();
        BigInteger bank3s2 = bank3.getIndexEqualTemp3();
        BigInteger bank3s3 = bank3.getIndexEqualTemp4();
        boolean bank3Indexresult = IndexEqualsProof.indexEqualsProof(bank3.getCom(), bank3com2, bank3com3, bank3t, bank3s1, bank3s2, bank3s3, arr);
//        System.out.println(bank3Indexresult);

        BigInteger bank4com2 = bank4.getBitComMulTemp1();
        BigInteger bank4com3 = bank4.getComTemp1();
        BigInteger bank4t = bank4.getIndexEqualTemp1();
        BigInteger bank4s1 = bank4.getIndexEqualTemp2();
        BigInteger bank4s2 = bank4.getIndexEqualTemp3();
        BigInteger bank4s3 = bank4.getIndexEqualTemp4();
        boolean bank4Indexresult = IndexEqualsProof.indexEqualsProof(bank4.getCom(), bank4com2, bank4com3, bank4t, bank4s1, bank4s2, bank4s3, arr);
//        System.out.println(bank4Indexresult);

        /**
         * 证明资产只转给一个人，即bitCom乘g的s次方，并把com相应的1+s或者0+s次方，证明支出的承诺隐藏值和其他银行承诺乘积的隐藏值相等
         */
        BigInteger bankComTemp = bankFrom.getComTemp1();
        BigInteger otherBankComTemp = bankTo1.getComTemp1().multiply(bankTo2.getComTemp1()).multiply(bankTo3.getComTemp1()).mod(arr[0]);
        BigInteger h2 = zkpField.getH2();
        BigInteger n = zkpField.getN();
        BigInteger n1 = zkpField.getN1();
        BigInteger n2 = zkpField.getN2();
        boolean assetsToOneBank = BalanceProof.EqualityOfMessageInPedersenCoomiment(bankComTemp, otherBankComTemp, h2, n, n1, n2, arr);
//        System.out.println(assetsToOneBank);

        /**
         * 下面的代码证明doubleCom的隐藏值是com隐藏值的平方
         */
        BigInteger bank1DoubleCom = bank1.getDoubleCom();
        BigInteger bank1ComSAddVIdex = bank1.getComSAddVIndex();
        BigInteger bank1ComSIndex = bank1.getComSIndex();
        BigInteger bank1HBaseIndex = bank1.gethBaseIndex();
        BigInteger bank1Hash_h = bank1.getHash_h();
        BigInteger bank1H_Response = bank1.getH_Response();
        boolean bank1DoubleIndexProofResult = generateComIndexProof.generateComIndexProof(bank1DoubleCom, bank1ComSAddVIdex, bank1ComSIndex, bank1HBaseIndex,
                bank1Hash_h, bank1H_Response, arr);
        BigInteger bank2DoubleCom = bank2.getDoubleCom();
        BigInteger bank2ComSAddVIdex = bank2.getComSAddVIndex();
        BigInteger bank2ComSIndex = bank2.getComSIndex();
        BigInteger bank2HBaseIndex = bank2.gethBaseIndex();
        BigInteger bank2Hash_h = bank2.getHash_h();
        BigInteger bank2H_Response = bank2.getH_Response();
        boolean bank2DoubleIndexProofResult = generateComIndexProof.generateComIndexProof(bank2DoubleCom, bank2ComSAddVIdex, bank2ComSIndex, bank2HBaseIndex,
                bank2Hash_h, bank2H_Response, arr);
        BigInteger bank3DoubleCom = bank3.getDoubleCom();
        BigInteger bank3ComSAddVIdex = bank3.getComSAddVIndex();
        BigInteger bank3ComSIndex = bank3.getComSIndex();
        BigInteger bank3HBaseIndex = bank3.gethBaseIndex();
        BigInteger bank3Hash_h = bank3.getHash_h();
        BigInteger bank3H_Response = bank3.getH_Response();
        boolean bank3DoubleIndexProofResult = generateComIndexProof.generateComIndexProof(bank3DoubleCom, bank3ComSAddVIdex, bank3ComSIndex, bank3HBaseIndex,
                bank3Hash_h, bank3H_Response, arr);
        BigInteger bank4DoubleCom = bank4.getDoubleCom();
        BigInteger bank4ComSAddVIdex = bank4.getComSAddVIndex();
        BigInteger bank4ComSIndex = bank4.getComSIndex();
        BigInteger bank4HBaseIndex = bank4.gethBaseIndex();
        BigInteger bank4Hash_h = bank4.getHash_h();
        BigInteger bank4H_Response = bank4.getH_Response();
        boolean bank4DoubleIndexProofResult = generateComIndexProof.generateComIndexProof(bank4DoubleCom, bank4ComSAddVIdex, bank4ComSIndex, bank4HBaseIndex,
                bank4Hash_h, bank4H_Response, arr);

        /**
         * 下面的代码证明tripleCom的隐藏值是com隐藏值的立方
         */
        BigInteger bank1TripleCom = bank1.getTripleCom();
        BigInteger bank1ComSAddVSquareIndex = bank1.getComSAddVSquareIndex();
        BigInteger bank1HBaseIndex1 = bank1.gethBaseIndex1();
        BigInteger bank1Hash_h1 = bank1.getHash_h1();
        BigInteger bank1H_Response1 = bank1.getH_Response1();
        boolean bank1TripleIndexProofResult = generateComIndexProof.generateComIndexProof(bank1TripleCom, bank1ComSAddVSquareIndex, bank1ComSIndex, bank1HBaseIndex1,
                bank1Hash_h1, bank1H_Response1, arr);
        BigInteger bank2TripleCom = bank2.getTripleCom();
        BigInteger bank2ComSAddVSquareIndex = bank2.getComSAddVSquareIndex();
        BigInteger bank2HBaseIndex1 = bank2.gethBaseIndex1();
        BigInteger bank2Hash_h1 = bank2.getHash_h1();
        BigInteger bank2H_Response1 = bank2.getH_Response1();
        boolean bank2TripleIndexProofResult = generateComIndexProof.generateComIndexProof(bank2TripleCom, bank2ComSAddVSquareIndex, bank2ComSIndex, bank2HBaseIndex1,
                bank2Hash_h1, bank2H_Response1, arr);
        BigInteger bank3TripleCom = bank3.getTripleCom();
        BigInteger bank3ComSAddVSquareIndex = bank3.getComSAddVSquareIndex();
        BigInteger bank3HBaseIndex1 = bank3.gethBaseIndex1();
        BigInteger bank3Hash_h1 = bank3.getHash_h1();
        BigInteger bank3H_Response1 = bank3.getH_Response1();
        boolean bank3TripleIndexProofResult = generateComIndexProof.generateComIndexProof(bank3TripleCom, bank3ComSAddVSquareIndex, bank3ComSIndex, bank3HBaseIndex1,
                bank3Hash_h1, bank3H_Response1, arr);
        BigInteger bank4TripleCom = bank4.getTripleCom();
        BigInteger bank4ComSAddVSquareIndex = bank4.getComSAddVSquareIndex();
        BigInteger bank4HBaseIndex1 = bank4.gethBaseIndex1();
        BigInteger bank4Hash_h1 = bank4.getHash_h1();
        BigInteger bank4H_Response1 = bank4.getH_Response1();
        boolean bank4TripleIndexProofResult = generateComIndexProof.generateComIndexProof(bank4TripleCom, bank4ComSAddVSquareIndex, bank4ComSIndex, bank4HBaseIndex1,
                bank4Hash_h1, bank4H_Response1, arr);

        /**
         * 下面的代码证明quadraCom的隐藏值是com隐藏值的四次方
         */
        BigInteger bank1QuadraCom = bank1.getQuadraCom();
        BigInteger bank1ComSAddVTripleIndex = bank1.getComSAddVTripleIndex();
        BigInteger bank1HBaseIndex2 = bank1.gethBaseIndex2();
        BigInteger bank1Hash_h2 = bank1.getHash_h2();
        BigInteger bank1H_Response2 = bank1.getH_Response2();
        boolean bank1QuadraIndexProofResult = generateComIndexProof.generateComIndexProof(bank1QuadraCom, bank1ComSAddVTripleIndex, bank1ComSIndex, bank1HBaseIndex2,
                bank1Hash_h2, bank1H_Response2, arr);
        BigInteger bank2QuadraCom = bank2.getQuadraCom();
        BigInteger bank2ComSAddVTripleIndex = bank2.getComSAddVTripleIndex();
        BigInteger bank2HBaseIndex2 = bank2.gethBaseIndex2();
        BigInteger bank2Hash_h2 = bank2.getHash_h2();
        BigInteger bank2H_Response2 = bank2.getH_Response2();
        boolean bank2QuadraIndexProofResult = generateComIndexProof.generateComIndexProof(bank2QuadraCom, bank2ComSAddVTripleIndex, bank2ComSIndex, bank2HBaseIndex2,
                bank2Hash_h2, bank2H_Response2, arr);
        BigInteger bank3QuadraCom = bank3.getQuadraCom();
        BigInteger bank3ComSAddVTripleIndex = bank3.getComSAddVTripleIndex();
        BigInteger bank3HBaseIndex2 = bank3.gethBaseIndex2();
        BigInteger bank3Hash_h2 = bank3.getHash_h2();
        BigInteger bank3H_Response2 = bank3.getH_Response2();
        boolean bank3QuadraIndexProofResult = generateComIndexProof.generateComIndexProof(bank3QuadraCom, bank3ComSAddVTripleIndex, bank3ComSIndex, bank3HBaseIndex2,
                bank3Hash_h2, bank3H_Response2, arr);
        BigInteger bank4QuadraCom = bank4.getQuadraCom();
        BigInteger bank4ComSAddVTripleIndex = bank4.getComSAddVTripleIndex();
        BigInteger bank4HBaseIndex2 = bank4.gethBaseIndex2();
        BigInteger bank4Hash_h2 = bank4.getHash_h2();
        BigInteger bank4H_Response2 = bank4.getH_Response2();
        boolean bank4QuadraIndexProofResult = generateComIndexProof.generateComIndexProof(bank4QuadraCom, bank4ComSAddVTripleIndex, bank4ComSIndex, bank4HBaseIndex2,
                bank4Hash_h2, bank4H_Response2, arr);


        /**
         * 下面开始v的四次方的范围证明
         */
        /**
         * bank1的范围证明
         */
        //首先验证t的成立
        BigInteger bank1A = bank1.getA();
        BigInteger bank1S = bank1.getS();
        BigInteger bank1yHash = bank1.getyHash();
        BigInteger bank1zHash = bank1.getzHash();
        BigInteger bank1xHash = bank1.getxHash();
        BigInteger bank1T1 = bank1.getT1();
        BigInteger bank1T2 = bank1.getT2();
        String bank1strLx = bank1.getLx().substring(0, bank1.getLx().length()-1);
        String[] newBank1StrLx = bank1strLx.split(",");
        BigInteger[] Bank1Lx = new BigInteger[newBank1StrLx.length];
        for (int i = 0;i<newBank1StrLx.length;i++){
            Bank1Lx[i] = new BigInteger(newBank1StrLx[i]);
        }
        String bank1strRx = bank1.getRx().substring(0, bank1.getRx().length()-1);
        String[] newBank1StrRx = bank1strRx.split(",");
        BigInteger[] Bank1Rx = new BigInteger[newBank1StrRx.length];
        for (int i = 0;i<newBank1StrRx.length;i++){
            Bank1Rx[i] = new BigInteger(newBank1StrRx[i]);
        }
        BigInteger bank1T = bank1.getT();
        BigInteger bank1taoX = bank1.getTaox();
        BigInteger bank1miu = bank1.getMiu();
        BigInteger bank1deerTa = GetDeerTa.getDeerTa(bank1yHash, bank1zHash, Bank1Lx, arr);
        BigInteger[] G = ReadVectorFile.readFile("gVector.txt");
        BigInteger[] H = ReadVectorFile.readFile("hVector.txt");
        BigInteger[] newBank1H = HVectorConvert.hVectorConvert(H, bank1yHash, arr);
        boolean bank1TResult = RangeProof.isTresult(bank1zHash, bank1xHash, bank1T, bank1QuadraCom, bank1taoX, bank1T1, bank1T2, bank1deerTa, arr);
//        System.out.println("BANK1="+bank1TResult);
        boolean bank1PResult = RangeProof.verifierP(bank1A, bank1S, G, newBank1H, Bank1Lx, Bank1Rx, bank1yHash, bank1zHash, bank1xHash, bank1miu, arr);
//        System.out.println("BANK1="+bank1PResult);
        boolean bank1InnerProductResult = RangeProof.verLx_Rx_T(Bank1Lx, Bank1Rx, bank1T, arr);
//        System.out.println("BANK1="+bank1InnerProductResult);

        /**
         * bank2的范围证明
         */
        //首先验证t的成立
        BigInteger bank2A = bank2.getA();
        BigInteger bank2S = bank2.getS();
        BigInteger bank2yHash = bank2.getyHash();
        BigInteger bank2zHash = bank2.getzHash();
        BigInteger bank2xHash = bank2.getxHash();
        BigInteger bank2T1 = bank2.getT1();
        BigInteger bank2T2 = bank2.getT2();
        String bank2strLx = bank2.getLx().substring(0, bank2.getLx().length()-1);
        String[] newBank2StrLx = bank2strLx.split(",");
        BigInteger[] Bank2Lx = new BigInteger[newBank2StrLx.length];
        for (int i = 0;i<newBank2StrLx.length;i++){
            Bank2Lx[i] = new BigInteger(newBank2StrLx[i]);
        }
        String bank2strRx = bank2.getRx().substring(0, bank2.getRx().length()-1);
        String[] newBank2StrRx = bank2strRx.split(",");
        BigInteger[] Bank2Rx = new BigInteger[newBank2StrRx.length];
        for (int i = 0;i<newBank2StrRx.length;i++){
            Bank2Rx[i] = new BigInteger(newBank2StrRx[i]);
        }
        BigInteger bank2T = bank2.getT();
        BigInteger bank2taoX = bank2.getTaox();
        BigInteger bank2miu = bank2.getMiu();
        BigInteger bank2deerTa = GetDeerTa.getDeerTa(bank2yHash, bank2zHash, Bank2Lx, arr);
        BigInteger[] newBank2H = HVectorConvert.hVectorConvert(H, bank2yHash, arr);
        boolean bank2TResult = RangeProof.isTresult(bank2zHash, bank2xHash, bank2T, bank2QuadraCom, bank2taoX, bank2T1, bank2T2, bank2deerTa, arr);
//        System.out.println("BANK2="+bank2TResult);
        boolean bank2PResult = RangeProof.verifierP(bank2A, bank2S, G, newBank2H, Bank2Lx, Bank2Rx, bank2yHash, bank2zHash, bank2xHash, bank2miu, arr);
//        System.out.println("BANK2="+bank2PResult);
        boolean bank2InnerProductResult = RangeProof.verLx_Rx_T(Bank2Lx, Bank2Rx, bank2T, arr);
//        System.out.println("BANK2="+bank2InnerProductResult);

        /**
         * bank3的范围证明
         */
        //首先验证t的成立
        BigInteger bank3A = bank3.getA();
        BigInteger bank3S = bank3.getS();
        BigInteger bank3yHash = bank3.getyHash();
        BigInteger bank3zHash = bank3.getzHash();
        BigInteger bank3xHash = bank3.getxHash();
        BigInteger bank3T1 = bank3.getT1();
        BigInteger bank3T2 = bank3.getT2();
        String bank3strLx = bank3.getLx().substring(0, bank3.getLx().length()-1);
        String[] newBank3StrLx = bank3strLx.split(",");
        BigInteger[] Bank3Lx = new BigInteger[newBank3StrLx.length];
        for (int i = 0;i<newBank3StrLx.length;i++){
            Bank3Lx[i] = new BigInteger(newBank3StrLx[i]);
        }
        String bank3strRx = bank3.getRx().substring(0, bank3.getRx().length()-1);
        String[] newBank3StrRx = bank3strRx.split(",");
        BigInteger[] Bank3Rx = new BigInteger[newBank3StrRx.length];
        for (int i = 0;i<newBank3StrRx.length;i++){
            Bank3Rx[i] = new BigInteger(newBank3StrRx[i]);
        }
        BigInteger bank3T = bank3.getT();
        BigInteger bank3taoX = bank3.getTaox();
        BigInteger bank3miu = bank3.getMiu();
        BigInteger bank3deerTa = GetDeerTa.getDeerTa(bank3yHash, bank3zHash, Bank3Lx, arr);
        BigInteger[] newBank3H = HVectorConvert.hVectorConvert(H, bank3yHash, arr);
        boolean bank3TResult = RangeProof.isTresult(bank3zHash, bank3xHash, bank3T, bank3QuadraCom, bank3taoX, bank3T1, bank3T2, bank3deerTa, arr);
//        System.out.println("BANK3="+bank3TResult);
        boolean bank3PResult = RangeProof.verifierP(bank3A, bank3S, G, newBank3H, Bank3Lx, Bank3Rx, bank3yHash, bank3zHash, bank3xHash, bank3miu, arr);
//        System.out.println("BANK3="+bank3PResult);
        boolean bank3InnerProductResult = RangeProof.verLx_Rx_T(Bank3Lx, Bank3Rx, bank3T, arr);
//        System.out.println("BANK3="+bank3InnerProductResult);

        /**
         * bank4的范围证明
         */
        //首先验证t的成立
        BigInteger bank4A = bank4.getA();
        BigInteger bank4S = bank4.getS();
        BigInteger bank4yHash = bank4.getyHash();
        BigInteger bank4zHash = bank4.getzHash();
        BigInteger bank4xHash = bank4.getxHash();
        BigInteger bank4T1 = bank4.getT1();
        BigInteger bank4T2 = bank4.getT2();
        String bank4strLx = bank4.getLx().substring(0, bank4.getLx().length()-1);
        String[] newBank4StrLx = bank4strLx.split(",");
        BigInteger[] Bank4Lx = new BigInteger[newBank4StrLx.length];
        for (int i = 0;i<newBank4StrLx.length;i++){
            Bank4Lx[i] = new BigInteger(newBank4StrLx[i]);
        }
        String bank4strRx = bank4.getRx().substring(0, bank4.getRx().length()-1);
        String[] newBank4StrRx = bank4strRx.split(",");
        BigInteger[] Bank4Rx = new BigInteger[newBank4StrRx.length];
        for (int i = 0;i<newBank4StrRx.length;i++){
            Bank4Rx[i] = new BigInteger(newBank4StrRx[i]);
        }
        BigInteger bank4T = bank4.getT();
        BigInteger bank4taoX = bank4.getTaox();
        BigInteger bank4miu = bank4.getMiu();
        BigInteger bank4deerTa = GetDeerTa.getDeerTa(bank4yHash, bank4zHash, Bank4Lx, arr);
        BigInteger[] newBank4H = HVectorConvert.hVectorConvert(H, bank4yHash, arr);
        boolean bank4TResult = RangeProof.isTresult(bank4zHash, bank4xHash, bank4T, bank4QuadraCom, bank4taoX, bank4T1, bank4T2, bank4deerTa, arr);
//        System.out.println("BANK4="+bank4TResult);
        boolean bank4PResult = RangeProof.verifierP(bank4A, bank4S, G, newBank4H, Bank4Lx, Bank4Rx, bank4yHash, bank4zHash, bank4xHash, bank4miu, arr);
//        System.out.println("BANK4="+bank4PResult);
        boolean bank4InnerProductResult = RangeProof.verLx_Rx_T(Bank4Lx, Bank4Rx, bank4T, arr);
//        System.out.println("BANK4="+bank4InnerProductResult);


        /**
         * 下面把验证通过之后的交易信息写进allTransaction.txt文件中，只需要写进需要审计的字段，也就是bank类中的前六个字段。
         */
        String strTemp = tempInput.substring(0, 30);
        String bank1Str = "\"bank1\""+":{\"bitCom\":"+bank1.getBitCom()+",\"com\":"+bank1.getCom()+",\"doubleCom\":"+bank1.getDoubleCom()+",\"isFrom\":"+bank1.getisFrom()
                +",\"quadraCom\":"+bank1.getQuadraCom()+",\"tripleCom\":"+bank1.getTripleCom()+"},";
        String bank2Str = "\"bank2\""+":{\"bitCom\":"+bank2.getBitCom()+",\"com\":"+bank2.getCom()+",\"doubleCom\":"+bank2.getDoubleCom()+",\"isFrom\":"+bank2.getisFrom()
                +",\"quadraCom\":"+bank2.getQuadraCom()+",\"tripleCom\":"+bank2.getTripleCom()+"},";
        String bank3Str = "\"bank3\""+":{\"bitCom\":"+bank3.getBitCom()+",\"com\":"+bank3.getCom()+",\"doubleCom\":"+bank3.getDoubleCom()+",\"isFrom\":"+bank3.getisFrom()
                +",\"quadraCom\":"+bank3.getQuadraCom()+",\"tripleCom\":"+bank3.getTripleCom()+"},";
        String bank4Str = "\"bank4\""+":{\"bitCom\":"+bank4.getBitCom()+",\"com\":"+bank4.getCom()+",\"doubleCom\":"+bank4.getDoubleCom()+",\"isFrom\":"+bank4.getisFrom()
                +",\"quadraCom\":"+bank4.getQuadraCom()+",\"tripleCom\":"+bank4.getTripleCom()+"}}";
//        System.out.println(strTemp);
//        System.out.println(bank1Str);
//        System.out.println(bank2Str);
//        System.out.println(bank3Str);
//        System.out.println(bank4Str);
        String transStr = strTemp.concat(bank1Str).concat(bank2Str).concat(bank3Str).concat(bank4Str);
//        System.out.println(transStr);
        if(result1 && result2 && bank1BitReslut && bank2BitReslut && bank3BitReslut && bank4BitReslut && twoResult && bank1_g_s_result && bank2_g_s_result
                && bank3_g_s_result && bank4_g_s_result && bank1_com_1or0_s_result && bank2_com_1or0_s_result && bank3_com_1or0_s_result && bank4_com_1or0_s_result
                && bank1Indexresult && bank2Indexresult && bank3Indexresult && bank4Indexresult && assetsToOneBank && bank1DoubleIndexProofResult && bank2DoubleIndexProofResult
                && bank3DoubleIndexProofResult && bank4DoubleIndexProofResult && bank1TripleIndexProofResult && bank2TripleIndexProofResult && bank3TripleIndexProofResult
                && bank4TripleIndexProofResult && bank1QuadraIndexProofResult && bank2QuadraIndexProofResult && bank3QuadraIndexProofResult && bank4QuadraIndexProofResult
                && bank1TResult && bank1PResult && bank1InnerProductResult && bank2TResult && bank2PResult && bank2InnerProductResult && bank3TResult && bank3PResult
                && bank3InnerProductResult && bank4TResult && bank4PResult && bank4InnerProductResult){
            FileWriter file = new FileWriter("allTransactions.txt", true);
            file.write(transStr+"\n");
            file.close();
            System.out.println("零知识证明通过！");
        }else {
            System.out.println("零知识证明失败！");
        }
    }

}
