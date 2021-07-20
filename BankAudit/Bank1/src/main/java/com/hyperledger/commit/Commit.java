package com.hyperledger.commit;

import com.alibaba.fastjson.JSON;
import com.hyperledger.bank.Bank;
import com.hyperledger.bank.BankBalanceAndBitZKP;
import com.hyperledger.connection.Connection;
import com.hyperledger.connection.Invoke;
import com.hyperledger.socket.SendMain;
import com.hyperledger.socket.SendMessage;
import com.hyperledger.zkp.HashFunction;

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
        SecureRandom random = new SecureRandom();
        BigInteger r1 = new BigInteger(40,random);
        BigInteger r2 = new BigInteger(40,random);
        BigInteger x = new BigInteger(40,random);
        BigInteger y = new BigInteger(40,random);
        BigInteger z = new BigInteger(40,random);

        r1 = GetRandom.getRandom(arr);
        r2 = GetRandom.getRandom(arr);
        x = GetRandom.getRandom(arr);
        y = GetRandom.getRandom(arr);
        z = GetRandom.getRandom(arr);

        //下面的字段用于bitComZKP（非0即1）的随机数
        BigInteger a = new BigInteger(40,random);
        BigInteger s = new BigInteger(40,random);
        BigInteger t = new BigInteger(40,random);
        BigInteger challenge = new BigInteger(40,random);
        a = GetRandom.getRandom(arr);
        s = GetRandom.getRandom(arr);
        t = GetRandom.getRandom(arr);
        challenge = GetRandom.getRandom(arr);

        /**
         * 下面的字段用于证明资产只转给一个人的中间变量,其中的s选取一个定值，就以之前出现的s = 898363816870
         */
        //证明g的s次方的随机数
        BigInteger rToOne = new BigInteger(40,random);
        BigInteger sTemp = new BigInteger("898363816870");  //s
        BigInteger sAndBinTemp = new BigInteger(40,random);     //证明com的1/0+s次方的随机数
        rToOne = GetRandom.getRandom(arr);
        sAndBinTemp = GetRandom.getRandom(arr);
        //计算承诺
        BigInteger[] comarr = new BigInteger[47];

        /**
         * 读取银行1的资产
         */
        BigInteger assets = ReadBigInteger.readBigInteger("bank1AssetsRecord.txt");
        if (assets.compareTo(m) != 1){
            System.out.println("余额不足，请重新选择 ！");
//            Thread.currentThread().interrupt();
//            System.exit(0);
            CommitGenerateAll.transcation();
        }else {
            //交易金额的承诺
            comarr[0] = (arr[2].modPow(m, arr[0])).multiply((arr[3]).modPow(r1, arr[0])).mod(arr[0]);

            //比特承诺，是否参与了交易，1为参与交易，0为没有参与交易
    //        comarr[1] = (arr[2].modPow(BigInteger.valueOf(0), arr[0])).multiply((arr[3]).modPow(r2, arr[0])).mod(arr[0]);
            comarr[1] = (arr[2].modPow(BigInteger.valueOf(1), arr[0])).multiply((arr[3]).modPow(r2, arr[0])).mod(arr[0]);

            //是否为交易的发起者
            comarr[2] = BigInteger.valueOf(1);
            /**
             * 银行作为交易的发起者，参与交易的次数加1
             */
            BigInteger transactionTime = ReadBigInteger.readBigInteger("bank1TransactionTime.txt");
            FileWriter fileTransactionTime = new FileWriter("bank1TransactionTime.txt");
            fileTransactionTime.write(transactionTime.add(BigInteger.valueOf(1))+"\n");
            fileTransactionTime.close();

            //隐藏值的二次方的承诺
            comarr[3] = (arr[2].modPow(m.pow(2), arr[0])).multiply((arr[3]).modPow(x, arr[0])).mod(arr[0]);
            /**
             * 交易额的平方写进文件
             */
            BigInteger doubleComSum = ReadBigInteger.readBigInteger("bank1DoubleComSum.txt");
            FileWriter fileDoubleComSum = new FileWriter("bank1DoubleComSum.txt");
            fileDoubleComSum.write(doubleComSum.add(m.pow(2))+"\n");
            fileDoubleComSum.close();

            //隐藏值的三次方的承诺
            comarr[4] = (arr[2].modPow(m.pow(3), arr[0])).multiply((arr[3]).modPow(y, arr[0])).mod(arr[0]);
            /**
             * 交易金额的立方写进文件
             */
            BigInteger tripleComSum = ReadBigInteger.readBigInteger("bank1TripleComSum.txt");
            FileWriter fileTripleComSum = new FileWriter("bank1TripleComSum.txt");
            fileTripleComSum.write(tripleComSum.add(m.pow(3))+"\n");
            fileTripleComSum.close();

            //隐藏值的四次方的承诺
            comarr[5] = (arr[2].modPow(m.pow(4), arr[0])).multiply((arr[3]).modPow(z, arr[0])).mod(arr[0]);
            /**
             * 交易金额的四次方写进文件
             */
            BigInteger quadraComSum = ReadBigInteger.readBigInteger("bank1QuadraComSum.txt");
            FileWriter fileQuadraComSum = new FileWriter("bank1QuadraComSum.txt");
            fileQuadraComSum.write(quadraComSum.add(m.pow(4))+"\n");
            fileQuadraComSum.close();

            //下面的字段是用于证明比特承诺非0即1的字段
            //中间承诺变量1
            comarr[6] = (arr[2].modPow(a, arr[0]).multiply(arr[3].modPow(s, arr[0]))).mod(arr[0]);
            //中间承诺变量2
            comarr[7] = (arr[2].modPow(a.multiply(BigInteger.valueOf(1)), arr[0]).multiply(arr[3].modPow(t, arr[0]))).mod(arr[0]);
            //挑战
            comarr[8] = challenge;
            //挑战的哈希
            comarr[9] = new BigInteger(HashFunction.getSHA256StrJava(challenge.toString())).mod(arr[1]);
            //响应1
            comarr[10] = (challenge.multiply(BigInteger.valueOf(1)).add(a)).mod(arr[1]);
            //响应2
            comarr[11] = (r2.multiply(challenge).add(s)).mod(arr[1]);
            //响应3
            comarr[12] = (r2.multiply(challenge.subtract(comarr[10])).add(t)).mod(arr[1]);

            //下面的字段用于证明g的s次方中间变量
            comarr[13] = arr[2].modPow(sTemp, arr[0]);  //g的s次方
            BigInteger tTemp = arr[2].modPow(rToOne, arr[0]); //g的随机数次方
            String str = arr[2].toString() + comarr[13].toString() +tTemp.toString();
            comarr[14] = new BigInteger(HashFunction.getSHA256StrJava(str)).mod(arr[1]);
            comarr[15] = (sTemp.multiply(comarr[14]).add(rToOne)).mod(arr[1]);

            //bitCom和g的s次方相乘
            comarr[16] = comarr[1].multiply(comarr[13]).mod(arr[0]);
            //com的1/0+s次方
            comarr[17] = comarr[0].modPow((sTemp.add(BigInteger.valueOf(1))), arr[0]).mod(arr[0]);
            //com的随机数次方
            BigInteger tAndBinTempCom = comarr[0].modPow(sAndBinTemp, arr[0]).mod(arr[0]);
            String str1 = comarr[0].toString() + comarr[17].toString() + tAndBinTempCom.toString();
            comarr[18] = new BigInteger(HashFunction.getSHA256StrJava(str1)).mod(arr[1]);
            comarr[19] = ((sTemp.add(BigInteger.valueOf(1))).multiply(comarr[18])).add(sAndBinTemp).mod(arr[1]);

            //下面的字段证明comarr[16]g的指数和comarr[17]中gvhr的指数的相等，三个随机数
            BigInteger v1 = new BigInteger(40,random);
            BigInteger v2 = new BigInteger(40,random);
            v1 = GetRandom.getRandom(arr);
            v2 = GetRandom.getRandom(arr);
            BigInteger v3 = v2;

            comarr[20] = arr[3].modPow(v1, arr[0]).multiply(arr[2].modPow(v2,arr[0])).multiply(comarr[0].modPow(v3, arr[0])).mod(arr[0]);
            String str2 = arr[3].toString() + arr[2].toString() + comarr[0].toString() + comarr[20].toString();
            BigInteger hashc = new BigInteger(HashFunction.getSHA256StrJava(str2)).mod(arr[1]);
    //        System.out.println("comarr[20]:"+comarr[20]);
    //        System.out.println("hashc:" + hashc);
            comarr[21] = (v1.subtract(hashc.multiply(r2))).mod(arr[1]);
            comarr[22] = (v2.subtract(hashc.multiply(sTemp.add(BigInteger.valueOf(1))))).mod(arr[1]);
            comarr[23] = (v3.subtract(hashc.multiply(sTemp.add(BigInteger.valueOf(1))))).mod(arr[1]);

            /**
             * 下面的是证明doubleCom的隐藏值是com隐藏值的平方
             */
            comarr[24] = comarr[0].modPow(sTemp.add(m), arr[0]).mod(arr[0]);    //com的s+v次方
            comarr[25] = comarr[0].modPow(sTemp, arr[0]).mod(arr[0]);   //com的s次方
            //证明h的表达
            BigInteger index = (r1.multiply(m).subtract(x)).mod(arr[1]);
            comarr[26] = arr[3].modPow(index,arr[0]).mod(arr[0]);
            BigInteger hComRandom = new BigInteger(40,random);    //h承诺的随机数
            hComRandom = GetRandom.getRandom(arr);
            BigInteger hCom = arr[3].modPow(hComRandom, arr[0]).mod(arr[0]);
            comarr[27] = new BigInteger(HashFunction.getSHA256StrJava(arr[3].toString() + comarr[26].toString() + hCom.toString())).mod(arr[1]);
            comarr[28] = (comarr[27].multiply(index).add(hComRandom)).mod(arr[1]);

            /**
             * 下面的是证明tripleCom的隐藏值是com隐藏值的立方
             */
            comarr[29] = comarr[0].modPow(sTemp.add(m.pow(2)), arr[0]).mod(arr[0]); //com的s+v^2次方
            //证明h的表达
            BigInteger index1 = (r1.multiply(m.pow(2)).subtract(y)).mod(arr[1]);
            comarr[30] = arr[3].modPow(index1,arr[0]).mod(arr[0]);
            BigInteger hComRandom1 = new BigInteger(40,random);    //h承诺的随机数
            hComRandom1 = GetRandom.getRandom(arr);
            BigInteger hCom1 = arr[3].modPow(hComRandom1, arr[0]).mod(arr[0]);
            comarr[31] = new BigInteger(HashFunction.getSHA256StrJava(arr[3].toString() + comarr[30].toString() + hCom1.toString())).mod(arr[1]);
            comarr[32] = (comarr[31].multiply(index1).add(hComRandom1)).mod(arr[1]);

            /**
             * 下面的是证明quadraCom的隐藏值是com隐藏值的四次方
             */
            comarr[33] = comarr[0].modPow(sTemp.add(m.pow(3)), arr[0]).mod(arr[0]); //com的s+v^2次方
            //证明h的表达
            BigInteger index2 = (r1.multiply(m.pow(3)).subtract(z)).mod(arr[1]);
            comarr[34] = arr[3].modPow(index2,arr[0]).mod(arr[0]);
            BigInteger hComRandom2 = new BigInteger(40,random);    //h承诺的随机数
            hComRandom2 = GetRandom.getRandom(arr);
            BigInteger hCom2 = arr[3].modPow(hComRandom2, arr[0]).mod(arr[0]);
            comarr[35] = new BigInteger(HashFunction.getSHA256StrJava(arr[3].toString() + comarr[34].toString() + hCom2.toString())).mod(arr[1]);
            comarr[36] = (comarr[35].multiply(index2).add(hComRandom2)).mod(arr[1]);


            /**
             * 现在进行范围证明的字段生成，我们只验证v的四次方，不然全验证字段太多。v的最大值1千万，n需要94位。
             */
            BigInteger[] gVector = ReadVectorFile.readFile("gVector.txt");
            BigInteger[] hVector = ReadVectorFile.readFile("hVector.txt");
            BigInteger Alpha = GetRandom.getRandom(arr);
            BigInteger[] al = GetAl.getAl(m.pow(4));
            BigInteger[] ar = GetAr.getAr(al,arr);
            comarr[37] = GetA.getA(gVector,hVector, al, ar, Alpha, arr);
            BigInteger Rho = GetRandom.getRandom(arr);
            BigInteger[] Sl = Get_SlAnd_SrAnd_S.getSl(al, arr);
            BigInteger[] Sr = Get_SlAnd_SrAnd_S.getSr(al, arr);
            comarr[38] = Get_SlAnd_SrAnd_S.getS(gVector, hVector, Sl, Sr, Rho, arr);
            //y
            comarr[39] = new BigInteger(HashFunction.getSHA256StrJava(comarr[37].toString()+comarr[38].toString())).mod(arr[1]);
            //z
            comarr[40] = new BigInteger(HashFunction.getSHA256StrJava(comarr[37].toString()+comarr[38].toString()+comarr[39].toString())).mod(arr[1]);
            //x
            comarr[41] = new BigInteger(HashFunction.getSHA256StrJava(comarr[37].toString()+comarr[38].toString()+comarr[39].toString()+comarr[40].toString())).mod(arr[1]);
            BigInteger tao1 = GetRandom.getRandom(arr);
            comarr[42] = GetT1and_T2.getT1(al, ar, Sl, Sr, comarr[39], comarr[40], tao1, arr);
            BigInteger tao2 = GetRandom.getRandom(arr);
            comarr[43] = GetT1and_T2.getT2(Sl, Sr, comarr[39], tao2, arr);
            //lx
            BigInteger[] lx = GetLxAnd_Rx_T.getLx(al, Sl, comarr[41], comarr[40], arr);
            String lxStr = "";
            for (int i=0;i<lx.length;i++){
                lxStr = lxStr + lx[i] + ",";
            }
    //        comarr[44] = new BigInteger(lxStr);
            //lx向量不好存储，先放进文件，在读取
            FileWriter fileLx = new FileWriter("bank1FileLx.txt");
            fileLx.write(lxStr+"\n");
            fileLx.close();
            //rx
            BigInteger[] rx = GetLxAnd_Rx_T.getRx(ar, Sr, comarr[41], comarr[39], comarr[40], arr);
            String rxStr = "";
            for (int i=0;i<lx.length;i++){
                rxStr = rxStr + rx[i] + ",";
            }
    //        comarr[45] = new BigInteger(rxStr);
            //t
            //rx向量不好存储，先放进文件，在读取
            FileWriter fileRx = new FileWriter("bank1FileRx.txt");
            fileRx.write(rxStr+"\n");
            fileRx.close();
            comarr[44] = GetLxAnd_Rx_T.getT(lx, rx, arr);
            comarr[45] = GetTaox.getTaox(comarr[41], tao1, tao2, comarr[40], z, arr);
            comarr[46] = GetMiu.getMiu(Alpha, Rho, comarr[41], arr);


            //写入文件
            //把银行1本次转账的金额和余额也保存进文件中
            FileWriter file3 = new FileWriter("Transactionamount.txt");
            FileWriter file2 = new FileWriter("bank1AssetsRecord.txt");
            file2.write(assets.subtract(m)+ "\n");
            file3.write(m+"\n");
            file2.close();
            file3.close();


    //        FileWriter file = new FileWriter("bank1.txt",true);
            FileWriter file1 = new FileWriter("bank1random.txt");
            FileWriter fileRandom = new FileWriter("bankAllRandom.txt", true);
    //        System.out.println("======================bank的承诺=========================");
    //        file.write(comarr[0] +"\t"+ comarr[1] + "\t" + comarr[2] +"\t" + comarr[3] +"\t" +comarr[4] + "\t" +comarr[5] + "\n");
    //        file.write("{"+"\"com\":"+comarr[0] +",\"bitCom\":"+ comarr[1] + ",\"isFrom\":" + comarr[2] +",\"doubleCom\":" + comarr[3] +",\"tripleCom\":"
    //                +comarr[4] + ",\"quadraCom\":" +comarr[5] + ",\"bitComTemp1\":" +comarr[6] + ",\"bitComTemp2\":" +comarr[7] + ",\"challenge\":" +comarr[8] +
    //                ",\"hash_challenge\":" +comarr[9] + ",\"bitComResponse1\":" +comarr[10] + ",\"bitComResponse2\":" +comarr[11] + ",\"bitComResponse3\":" +comarr[12] + "}"+"\n");
    //        System.out.println("======================bank的致盲因子======================");
    //        file1.write(r1 + "\t" + r2 + "\t" + x + "\t" + y + "\t" + z +"\n");
            file1.write("{"+"\"r1\":"+r1 + ",\"r2\":" + r2 + ",\"x\":" + x + ",\"y\":" + y + ",\"z\":" + z +"}"+"\n");
            fileRandom.write("{"+"\"r1\":"+BigInteger.valueOf(0).subtract(r1).mod(arr[1]) + ",\"r2\":" + r2 + ",\"x\":" + x + ",\"y\":" + y + ",\"z\":" + z +"}"+"\n");
    //        file.close();
            file1.close();
            fileRandom.close();
        }
        //写进文件的承诺和致盲因子的类型是String

//        System.out.println("成功写入文件！");
        return comarr;

    }


    //创建其他银行承诺方法
    public static BigInteger[] commitOtherBank(String str, BigInteger m, BigInteger[] arr) throws IOException {
        //Pedersen承诺的形式c = g^m * h^r (m是要隐藏的金额，r是致盲因子)
        //产生random c，在2到q - 2之间
        SecureRandom random = new SecureRandom();
        BigInteger r1 = new BigInteger(40,random);
        BigInteger r2 = new BigInteger(40,random);
        BigInteger x = new BigInteger(40,random);
        BigInteger y = new BigInteger(40,random);
        BigInteger z = new BigInteger(40,random);

//        while(r1.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || r1.compareTo(BigInteger.valueOf(2)) == -1){
//            r1 = new BigInteger(40, random);
//        }
        r1 = GetRandom.getRandom(arr);
//        while(r2.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || r2.compareTo(BigInteger.valueOf(2)) == -1){
//            r2 = new BigInteger(40, random);
//        }
        r2 = GetRandom.getRandom(arr);
//        while(x.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || x.compareTo(BigInteger.valueOf(2)) == -1){
//            x = new BigInteger(40, random);
//        }
        x = GetRandom.getRandom(arr);
//        while(y.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || y.compareTo(BigInteger.valueOf(2)) == -1){
//            y = new BigInteger(40, random);
//        }
        y = GetRandom.getRandom(arr);
//        while(z.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || z.compareTo(BigInteger.valueOf(2)) == -1){
//            z = new BigInteger(40, random);
//        }
        z = GetRandom.getRandom(arr);

        //下面的字段用于bitComZKP的随机数
        BigInteger a = new BigInteger(40,random);
        BigInteger s = new BigInteger(40,random);
        BigInteger t = new BigInteger(40,random);
        BigInteger challenge = new BigInteger(40,random);
//        while(a.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || a.compareTo(BigInteger.valueOf(2)) == -1){
//            a = new BigInteger(40, random);
//        }
        a = GetRandom.getRandom(arr);
//        while(s.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || s.compareTo(BigInteger.valueOf(2)) == -1){
//            s = new BigInteger(40, random);
//        }
        s = GetRandom.getRandom(arr);
//        while(t.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || t.compareTo(BigInteger.valueOf(2)) == -1){
//            t = new BigInteger(40, random);
//        }
        t = GetRandom.getRandom(arr);
//        while(challenge.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || challenge.compareTo(BigInteger.valueOf(2)) == -1){
//            challenge = new BigInteger(40, random);
//        }
        challenge = GetRandom.getRandom(arr);

        /**
         * 下面的字段用于证明资产只转给一个人的中间变量,其中的s选取一个定值，就以之前出现的s = 898363816870
         */
        //证明g的s次方的随机数
        BigInteger rToOne = new BigInteger(40,random);
        BigInteger sTemp = new BigInteger("898363816870");  //s
        BigInteger sAndBinTemp = new BigInteger(40,random);     //证明com的1/0+s次方的随机数
//        while(rToOne.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || rToOne.compareTo(BigInteger.valueOf(2)) == -1){
//            rToOne = new BigInteger(40, random);
//        }
        rToOne = GetRandom.getRandom(arr);
//        while(sAndBinTemp.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || sAndBinTemp.compareTo(BigInteger.valueOf(2)) == -1){
//            sAndBinTemp = new BigInteger(40, random);
//        }
        sAndBinTemp = GetRandom.getRandom(arr);

        //计算承诺
        BigInteger[] comarr = new BigInteger[47];

        //交易金额的承诺
        comarr[0] = (arr[2].modPow(m, arr[0])).multiply((arr[3]).modPow(r1, arr[0])).mod(arr[0]);
        /**
         * 给其他银行发送金额，网络编程过去
         */
        //其他三个银行的port
        int bankStr = Integer.valueOf(str.substring(4,5));
        if (bankStr == 2){
            SendMessage.sendMesssage("4bank1", m.toString(),"127.0.0.1", 8882);
        }else if (bankStr == 3){
            SendMessage.sendMesssage("4bank1", m.toString(),"127.0.0.1", 8883);
        }else if (bankStr == 4){
            SendMessage.sendMesssage("4bank1", m.toString(),"127.0.0.1", 8884);
        }

        //是否为交易的发起者
        comarr[2] = BigInteger.valueOf(0);

        //隐藏值的二次方的承诺
        comarr[3] = (arr[2].modPow(m.pow(2), arr[0])).multiply((arr[3]).modPow(x, arr[0])).mod(arr[0]);

        //隐藏值的三次方的承诺
        comarr[4] = (arr[2].modPow(m.pow(3), arr[0])).multiply((arr[3]).modPow(y, arr[0])).mod(arr[0]);

        //隐藏值的四次方的承诺
        comarr[5] = (arr[2].modPow(m.pow(4), arr[0])).multiply((arr[3]).modPow(z, arr[0])).mod(arr[0]);

        //下面的字段是用于证明比特承诺非0即1的字段
        //中间承诺变量1
        comarr[6] = (arr[2].modPow(a, arr[0]).multiply(arr[3].modPow(s, arr[0]))).mod(arr[0]);
//        //中间承诺变量2
//        comarr[7] = (arr[2].modPow(a.multiply(m), arr[0]).multiply(arr[3].modPow(t, arr[0]))).mod(arr[0]);
        //挑战
        comarr[8] = challenge;
        //挑战的哈希
        comarr[9] = new BigInteger(HashFunction.getSHA256StrJava(challenge.toString())).mod(arr[1]);
        //响应1
//        comarr[10] = (challenge.multiply(BigInteger.valueOf(1)).add(a)).mod(arr[1]);
        //响应2
        comarr[11] = (r2.multiply(challenge).add(s)).mod(arr[1]);
        //响应3
//        comarr[12] = (r2.multiply(challenge.subtract(comarr[10])).add(t)).mod(arr[1]);

        //下面的字段用于证明g的s次方中间变量
        comarr[13] = arr[2].modPow(sTemp, arr[0]);  //g的s次方
        BigInteger tTemp = arr[2].modPow(rToOne, arr[0]); //
        String strtemp = arr[2].toString() + comarr[13].toString() +tTemp.toString();
        comarr[14] = new BigInteger(HashFunction.getSHA256StrJava(strtemp)).mod(arr[1]).mod(arr[1]);
        comarr[15] = (sTemp.multiply(comarr[14]).add(rToOne)).mod(arr[1]);

        //bitCom和g的s次方相乘
//        comarr[16] = comarr[1].multiply(comarr[13]).mod(arr[0]);

        //下面的字段证明comarr[16]g的指数和comarr[17]中gvhr的指数的相等，三个随机数
        BigInteger v1 = new BigInteger(40,random);
        BigInteger v2 = new BigInteger(40,random);
//        while(v1.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || v1.compareTo(BigInteger.valueOf(2)) == -1){
//            v1 = new BigInteger(40, random);
//        }
        v1 = GetRandom.getRandom(arr);
//        while(v2.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || v2.compareTo(BigInteger.valueOf(2)) == -1){
//            v2 = new BigInteger(40, random);
//        }
        v2 = GetRandom.getRandom(arr);
        BigInteger v3 = v2;

        //比特承诺，是否参与了交易，1为参与交易，0为没有参与交易
        if (m.compareTo(BigInteger.valueOf(0)) == 0) {
//            System.out.println("没有参与了交易");
            comarr[1] = (arr[2].modPow(BigInteger.valueOf(0), arr[0])).multiply((arr[3]).modPow(r2, arr[0])).mod(arr[0]);
            //中间承诺变量2
            comarr[7] = (arr[2].modPow(a.multiply(BigInteger.valueOf(0)), arr[0]).multiply(arr[3].modPow(t, arr[0]))).mod(arr[0]);
            //响应1
            comarr[10] = challenge.multiply(BigInteger.valueOf(0)).add(a).mod(arr[1]);
            //bitCom和g的s次方相乘
            comarr[12] = (r2.multiply(challenge.subtract(comarr[10])).add(t)).mod(arr[1]);
            comarr[16] = comarr[1].multiply(comarr[13]).mod(arr[0]);
            //com的1/0+s次方
            comarr[17] = comarr[0].modPow((sTemp.add(BigInteger.valueOf(0))), arr[0]).mod(arr[0]);
            //com的随机数次方
            BigInteger tAndBinTempCom = comarr[0].modPow(sAndBinTemp, arr[0]).mod(arr[0]);
            String str1 = comarr[0].toString() + comarr[17].toString() + tAndBinTempCom.toString();
            comarr[18] = new BigInteger(HashFunction.getSHA256StrJava(str1)).mod(arr[1]);
            comarr[19] = ((sTemp.add(BigInteger.valueOf(0))).multiply(comarr[18])).add(sAndBinTemp).mod(arr[1]);
            comarr[20] = arr[3].modPow(v1, arr[0]).multiply(arr[2].modPow(v2,arr[0])).multiply(comarr[0].modPow(v3, arr[0])).mod(arr[0]);
            String str2 = arr[3].toString() + arr[2].toString() + comarr[0].toString()+ comarr[20].toString();
            BigInteger hashc = new BigInteger(HashFunction.getSHA256StrJava(str2)).mod(arr[1]);
            comarr[21] = (v1.subtract(hashc.multiply(r2))).mod(arr[1]);
            comarr[22] = (v2.subtract(hashc.multiply(sTemp.add(BigInteger.valueOf(0))))).mod(arr[1]);
            comarr[23] = (v3.subtract(hashc.multiply(sTemp.add(BigInteger.valueOf(0))))).mod(arr[1]);

        }else {
//            System.out.println("参与了交易");
            comarr[1] = (arr[2].modPow(BigInteger.valueOf(1), arr[0])).multiply((arr[3]).modPow(r2, arr[0])).mod(arr[0]);
            //中间承诺变量2
            comarr[7] = (arr[2].modPow(a.multiply(BigInteger.valueOf(1)), arr[0]).multiply(arr[3].modPow(t, arr[0]))).mod(arr[0]);
            //响应1
            comarr[10] = challenge.multiply(BigInteger.valueOf(1)).add(a).mod(arr[1]);
            comarr[12] = (r2.multiply(challenge.subtract(comarr[10])).add(t)).mod(arr[1]);
            //bitCom和g的s次方相乘
            comarr[16] = comarr[1].multiply(comarr[13]).mod(arr[0]);
            //com的1/0+s次方
            comarr[17] = comarr[0].modPow(sTemp.add(BigInteger.valueOf(1)), arr[0]).mod(arr[0]);
            //com的随机数次方
            BigInteger tAndBinTempCom = comarr[0].modPow(sAndBinTemp, arr[0]).mod(arr[0]);
            String str1 = comarr[0].toString() + comarr[17].toString() + tAndBinTempCom.toString();
            comarr[18] = new BigInteger(HashFunction.getSHA256StrJava(str1)).mod(arr[1]);
            comarr[19] = ((sTemp.add(BigInteger.valueOf(1))).multiply(comarr[18])).add(sAndBinTemp).mod(arr[1]);
            comarr[20] = arr[3].modPow(v1, arr[0]).multiply(arr[2].modPow(v2,arr[0])).multiply(comarr[0].modPow(v3, arr[0])).mod(arr[0]);
//            System.out.println("comarr[20]:" + comarr[20]);
            String str2 = arr[3].toString() + arr[2].toString() + comarr[0].toString()+ comarr[20].toString();
            BigInteger hashc = new BigInteger(HashFunction.getSHA256StrJava(str2)).mod(arr[1]);
//            System.out.println("hachc:" + hashc);
            comarr[21] = (v1.subtract(hashc.multiply(r2))).mod(arr[1]);
            comarr[22] = (v2.subtract(hashc.multiply(sTemp.add(BigInteger.valueOf(1))))).mod(arr[1]);
            comarr[23] = (v3.subtract(hashc.multiply(sTemp.add(BigInteger.valueOf(1))))).mod(arr[1]);
        }

        /**
         * 下面的是证明doubleCom的隐藏值是com隐藏值的平方
         */
        comarr[24] = comarr[0].modPow(sTemp.add(m), arr[0]);    //com的s+v次方
        comarr[25] = comarr[0].modPow(sTemp, arr[0]);   //com的s次方
        //证明h的表达
        BigInteger index = r1.multiply(m).subtract(x);
        comarr[26] = arr[3].modPow(index,arr[0]).mod(arr[0]);
        BigInteger hComRandom = new BigInteger(40,random);    //h承诺的随机数
//        while(hComRandom.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || hComRandom.compareTo(BigInteger.valueOf(2)) == -1){
//            hComRandom = new BigInteger(40, random);
//        }
        hComRandom = GetRandom.getRandom(arr);
        BigInteger hCom = arr[3].modPow(hComRandom, arr[0]).mod(arr[0]);
        comarr[27] = new BigInteger(HashFunction.getSHA256StrJava(arr[3].toString() + comarr[26].toString() + hCom.toString())).mod(arr[1]);
        comarr[28] = comarr[27].multiply(index).add(hComRandom).mod(arr[1]);

        /**
         * 下面的是证明tripleCom的隐藏值是com隐藏值的立方
         */
        comarr[29] = comarr[0].modPow(sTemp.add(m.pow(2)), arr[0]).mod(arr[0]); //com的s+v^2次方
        //证明h的表达
        BigInteger index1 = (r1.multiply(m.pow(2)).subtract(y)).mod(arr[1]);
        comarr[30] = arr[3].modPow(index1,arr[0]).mod(arr[0]);
        BigInteger hComRandom1 = new BigInteger(40,random);    //h承诺的随机数
//        while(hComRandom1.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || hComRandom1.compareTo(BigInteger.valueOf(2)) == -1){
//            hComRandom1 = new BigInteger(40, random);
//        }
        hComRandom1 = GetRandom.getRandom(arr);
        BigInteger hCom1 = arr[3].modPow(hComRandom1, arr[0]).mod(arr[0]);
        comarr[31] = new BigInteger(HashFunction.getSHA256StrJava(arr[3].toString() + comarr[30].toString() + hCom1.toString())).mod(arr[1]);
        comarr[32] = (comarr[31].multiply(index1).add(hComRandom1)).mod(arr[1]);

        /**
         * 下面的是证明quadraCom的隐藏值是com隐藏值的四次方
         */
        comarr[33] = comarr[0].modPow(sTemp.add(m.pow(3)), arr[0]).mod(arr[0]); //com的s+v^2次方
        //证明h的表达
        BigInteger index2 = (r1.multiply(m.pow(3)).subtract(z)).mod(arr[1]);
        comarr[34] = arr[3].modPow(index2,arr[0]).mod(arr[0]);
        BigInteger hComRandom2 = new BigInteger(40,random);    //h承诺的随机数
//        while(hComRandom2.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || hComRandom2.compareTo(BigInteger.valueOf(2)) == -1){
//            hComRandom2 = new BigInteger(40, random);
//        }
        hComRandom2 = GetRandom.getRandom(arr);
        BigInteger hCom2 = arr[3].modPow(hComRandom2, arr[0]).mod(arr[0]);
        comarr[35] = new BigInteger(HashFunction.getSHA256StrJava(arr[3].toString() + comarr[34].toString() + hCom2.toString())).mod(arr[1]);
        comarr[36] = (comarr[35].multiply(index2).add(hComRandom2)).mod(arr[1]);

        /**
         * 现在进行范围证明的字段生成，我们只验证v的四次方，不然全验证字段太多。v的最大值1亿，n需要107位。
         */
        BigInteger[] gVector = ReadVectorFile.readFile("gVector.txt");
        BigInteger[] hVector = ReadVectorFile.readFile("hVector.txt");
        BigInteger Alpha = GetRandom.getRandom(arr);
        BigInteger[] al = GetAl.getAl(m.pow(4));
        BigInteger[] ar = GetAr.getAr(al,arr);
        comarr[37] = GetA.getA(gVector,hVector, al, ar, Alpha, arr);
        BigInteger Rho = GetRandom.getRandom(arr);
        BigInteger[] Sl = Get_SlAnd_SrAnd_S.getSl(al, arr);
        BigInteger[] Sr = Get_SlAnd_SrAnd_S.getSr(al, arr);
        comarr[38] = Get_SlAnd_SrAnd_S.getS(gVector, hVector, Sl, Sr, Rho, arr);
        //y
        comarr[39] = new BigInteger(HashFunction.getSHA256StrJava(comarr[37].toString()+comarr[38].toString())).mod(arr[1]);
        //z
        comarr[40] = new BigInteger(HashFunction.getSHA256StrJava(comarr[37].toString()+comarr[38].toString()+comarr[39].toString())).mod(arr[1]);
        //x
        comarr[41] = new BigInteger(HashFunction.getSHA256StrJava(comarr[37].toString()+comarr[38].toString()+comarr[39].toString()+comarr[40].toString())).mod(arr[1]);
        BigInteger tao1 = GetRandom.getRandom(arr);
        comarr[42] = GetT1and_T2.getT1(al, ar, Sl, Sr, comarr[39], comarr[40], tao1, arr);
        BigInteger tao2 = GetRandom.getRandom(arr);
        comarr[43] = GetT1and_T2.getT2(Sl, Sr, comarr[39], tao2, arr);
        //lx
        BigInteger[] lx = GetLxAnd_Rx_T.getLx(al, Sl, comarr[41], comarr[40], arr);
        String lxStr = "";
        for (int i=0;i<lx.length;i++){
            lxStr = lxStr + lx[i] + ",";
        }
//        comarr[44] = new BigInteger(lxStr);
        //lx向量不好存储，先放进文件，在读取
        FileWriter fileLx = new FileWriter(str+"FileLx.txt");
        fileLx.write(lxStr+"\n");
        fileLx.close();
        //rx
        BigInteger[] rx = GetLxAnd_Rx_T.getRx(ar, Sr, comarr[41], comarr[39], comarr[40], arr);
        String rxStr = "";
        for (int i=0;i<lx.length;i++){
            rxStr = rxStr + rx[i] + ",";
        }
//        comarr[45] = new BigInteger(rxStr);
        //t
        //rx向量不好存储，先放进文件，在读取
        FileWriter fileRx = new FileWriter(str+"FileRx.txt");
        fileRx.write(rxStr+"\n");
        fileRx.close();
        comarr[44] = GetLxAnd_Rx_T.getT(lx, rx, arr);
        comarr[45] = GetTaox.getTaox(comarr[41], tao1, tao2, comarr[40], z, arr);
        comarr[46] = GetMiu.getMiu(Alpha, Rho, comarr[41], arr);

        /**
         * 下面的字段用于证明银行有足够的钱去转账
         */



        //写入文件
        if(str.equals("bank2")){
//            FileWriter file1 = new FileWriter("bank2.txt",true);
            FileWriter file4 = new FileWriter("bank2random.txt");
//            FileWriter fileRandom = new FileWriter("bank2random1.txt",true);
//            file1.write(comarr[0] +"\t"+ comarr[1] + "\t" + comarr[2] +"\t" + comarr[3] +"\t" +comarr[4] + "\t" +comarr[5] + "\n");
//            file1.write("{"+"\"com\":"+comarr[0] +",\"bitCom\":"+ comarr[1] + ",\"isFrom\":" + comarr[2] +",\"doubleCom\":" + comarr[3] + ",\"tripleCom\":"
//                    + comarr[4] + ",\"quadraCom\":" +comarr[5] + ",\"bitComTemp1\":" +comarr[6] + ",\"bitComTemp2\":" +comarr[7] + ",\"challenge\":" +comarr[8] +
//                    ",\"hash_challenge\":" +comarr[9] + ",\"bitComResponse1\":" +comarr[10] + ",\"bitComResponse2\":" +comarr[11] + ",\"bitComResponse3\":" +comarr[12] + "}"+"\n");
//        System.out.println("======================bank的致盲因子======================");
//            file4.write(r1 + "\t" + r2 + "\t" + x + "\t" + y + "\t" + z +"\n");
            file4.write("{"+"\"r1\":"+r1 + ",\"r2\":" + r2 + ",\"x\":" + x + ",\"y\":" + y + ",\"z\":" + z +"}"+"\n");
//            fileRandom.write("{"+"\"r1\":"+r1 + ",\"r2\":" + r2 + ",\"x\":" + x + ",\"y\":" + y + ",\"z\":" + z +"}"+"\n");
//            file1.close();
            file4.close();
//            fileRandom.close();


        } else if (str.equals("bank3")){
//            FileWriter file2 = new FileWriter("bank3.txt",true);
            FileWriter file5 = new FileWriter("bank3random.txt");
//            FileWriter fileRandom = new FileWriter("bank3random1.txt", true);
//            file2.write(comarr[0] +"\t"+ comarr[1] + "\t" + comarr[2] +"\t" + comarr[3] +"\t" +comarr[4] + "\t" +comarr[5] + "\n");
//            file2.write("{"+"\"com\":"+comarr[0] +",\"bitCom\":"+ comarr[1] + ",\"isFrom\":" + comarr[2] +",\"doubleCom\":" + comarr[3] +",\"tripleCom\":"
//                    +comarr[4] + ",\"quadraCom\":" +comarr[5] + ",\"bitComTemp1\":" +comarr[6] + ",\"bitComTemp2\":" +comarr[7] + ",\"challenge\":" +comarr[8] +
//                    ",\"hash_challenge\":" +comarr[9] + ",\"bitComResponse1\":" +comarr[10] + ",\"bitComResponse2\":" +comarr[11] + ",\"bitComResponse3\":" +comarr[12] + "}"+"\n");
//        System.out.println("======================bank的致盲因子======================");
//            file5.write(r1 + "\t" + r2 + "\t" + x + "\t" + y + "\t" + z +"\n");
            file5.write("{"+"\"r1\":"+r1 + ",\"r2\":" + r2 + ",\"x\":" + x + ",\"y\":" + y + ",\"z\":" + z +"}"+"\n");
//            fileRandom.write("{"+"\"r1\":"+r1 + ",\"r2\":" + r2 + ",\"x\":" + x + ",\"y\":" + y + ",\"z\":" + z +"}"+"\n");
//            fileRandom.close();
//            file2.close();
            file5.close();


        } else {
//            FileWriter file3 = new FileWriter("bank4.txt",true);
            FileWriter file6 = new FileWriter("bank4random.txt");
//            FileWriter fileRandom = new FileWriter("bank4random1.txt",true);
//            file3.write(comarr[0] +"\t"+ comarr[1] + "\t" + comarr[2] +"\t" + comarr[3] +"\t" +comarr[4] + "\t" +comarr[5] + "\n");
//            file3.write("{"+"\"com\":"+comarr[0] +",\"bitCom\":"+ comarr[1] + ",\"isFrom\":" + comarr[2] +",\"doubleCom\":" + comarr[3] +",\"tripleCom\":"
//                    +comarr[4] + ",\"quadraCom\":" +comarr[5] + ",\"bitComTemp1\":" +comarr[6] + ",\"bitComTemp2\":" +comarr[7] + ",\"challenge\":" +comarr[8] +
//                    ",\"hash_challenge\":" +comarr[9] + ",\"bitComResponse1\":" +comarr[10] + ",\"bitComResponse2\":" +comarr[11] + ",\"bitComResponse3\":" +comarr[12] + "}"+"\n");
//        System.out.println("======================bank的致盲因子======================");
//            file6.write(r1 + "\t" + r2 + "\t" + x + "\t" + y + "\t" + z +"\n");
            file6.write("{"+"\"r1\":"+r1 + ",\"r2\":" + r2 + ",\"x\":" + x + ",\"y\":" + y + ",\"z\":" + z +"}"+"\n");
//            fileRandom.write("{"+"\"r1\":"+r1 + ",\"r2\":" + r2 + ",\"x\":" + x + ",\"y\":" + y + ",\"z\":" + z +"}"+"\n");
//            file3.close();
            file6.close();
//            fileRandom.close();

        }

//        System.out.println("成功写入文件！");
        return comarr;
    }



    //四个银行的承诺生成
    public static void commitAll(String str, BigInteger value, BigInteger[] arr) throws IOException {
        //首先判断要给那个银行转账，本程序的以bank1连接的节点，所以是以bank1作为支出银行的。
        Bank bank1 = new Bank();
        BigInteger[] bank1arr = commitSelfBank(value, arr);
        bank1.setCom(bank1arr[0]);
        bank1.setBitCom(bank1arr[1]);
        bank1.setisFrom((bank1arr[2].intValue()));
        bank1.setDoubleCom(bank1arr[3]);
        bank1.setTripleCom(bank1arr[4]);
        bank1.setQuadraCom(bank1arr[5]);
        bank1.setBitComTemp1(bank1arr[6]);
        bank1.setBitComTemp2(bank1arr[7]);
        bank1.setChallenge(bank1arr[8]);
        bank1.setHash_challenge(bank1arr[9]);
        bank1.setBitComResponse1(bank1arr[10]);
        bank1.setBitComResponse2(bank1arr[11]);
        bank1.setBitComResponse3(bank1arr[12]);
        bank1.setTemp1(bank1arr[13]);
        bank1.setTemp1C(bank1arr[14]);
        bank1.setTemp1S(bank1arr[15]);
        bank1.setBitComMulTemp1(bank1arr[16]);
        bank1.setComTemp1(bank1arr[17]);
        bank1.setComTemp1C(bank1arr[18]);
        bank1.setComTemp1S(bank1arr[19]);
        bank1.setIndexEqualTemp1(bank1arr[20]);
        bank1.setIndexEqualTemp2(bank1arr[21]);
        bank1.setIndexEqualTemp3(bank1arr[22]);
        bank1.setIndexEqualTemp4(bank1arr[23]);
        bank1.setComSAddVIndex(bank1arr[24]);
        bank1.setComSIndex(bank1arr[25]);
        bank1.sethBaseIndex(bank1arr[26]);
        bank1.setHash_h(bank1arr[27]);
        bank1.setH_Response(bank1arr[28]);
        bank1.setComSAddVSquareIndex(bank1arr[29]);
        bank1.sethBaseIndex1(bank1arr[30]);
        bank1.setHash_h1(bank1arr[31]);
        bank1.setH_Response1(bank1arr[32]);
        bank1.setComSAddVTripleIndex(bank1arr[33]);
        bank1.sethBaseIndex2(bank1arr[34]);
        bank1.setHash_h2(bank1arr[35]);
        bank1.setH_Response2(bank1arr[36]);
        bank1.setA(bank1arr[37]);
        bank1.setS(bank1arr[38]);
        bank1.setyHash(bank1arr[39]);
        bank1.setzHash(bank1arr[40]);
        bank1.setxHash(bank1arr[41]);
        bank1.setT1(bank1arr[42]);
        bank1.setT2(bank1arr[43]);
        bank1.setLx(ReadFileLxAndRx.readFile("bank1FileLx.txt"));
//        System.out.println(bank1.getLx());
        bank1.setRx(ReadFileLxAndRx.readFile("bank1FileRx.txt"));
        bank1.setT(bank1arr[44]);
        bank1.setTaox(bank1arr[45]);
        bank1.setMiu(bank1arr[46]);
        if(str.equals("bank2")){
            //银行2的交易信息
            Bank bank2 = new Bank();
            BigInteger[] bank2arr = commitOtherBank("bank2", value, arr);
//            BigInteger[] bank2arr = commitOtherBank("bank2", BigInteger.valueOf(0), arr);
            bank2.setCom(bank2arr[0]);
            bank2.setBitCom(bank2arr[1]);
            bank2.setisFrom(bank2arr[2].intValue());
            bank2.setDoubleCom(bank2arr[3]);
            bank2.setTripleCom(bank2arr[4]);
            bank2.setQuadraCom(bank2arr[5]);
            bank2.setBitComTemp1(bank2arr[6]);
            bank2.setBitComTemp2(bank2arr[7]);
            bank2.setChallenge(bank2arr[8]);
            bank2.setHash_challenge(bank2arr[9]);
            bank2.setBitComResponse1(bank2arr[10]);
            bank2.setBitComResponse2(bank2arr[11]);
            bank2.setBitComResponse3(bank2arr[12]);
            bank2.setTemp1(bank2arr[13]);
            bank2.setTemp1C(bank2arr[14]);
            bank2.setTemp1S(bank2arr[15]);
            bank2.setBitComMulTemp1(bank2arr[16]);
            bank2.setComTemp1(bank2arr[17]);
            bank2.setComTemp1C(bank2arr[18]);
            bank2.setComTemp1S(bank2arr[19]);
            bank2.setIndexEqualTemp1(bank2arr[20]);
            bank2.setIndexEqualTemp2(bank2arr[21]);
            bank2.setIndexEqualTemp3(bank2arr[22]);
            bank2.setIndexEqualTemp4(bank2arr[23]);
            bank2.setComSAddVIndex(bank2arr[24]);
            bank2.setComSIndex(bank2arr[25]);
            bank2.sethBaseIndex(bank2arr[26]);
            bank2.setHash_h(bank2arr[27]);
            bank2.setH_Response(bank2arr[28]);
            bank2.setComSAddVSquareIndex(bank2arr[29]);
            bank2.sethBaseIndex1(bank2arr[30]);
            bank2.setHash_h1(bank2arr[31]);
            bank2.setH_Response1(bank2arr[32]);
            bank2.setComSAddVTripleIndex(bank2arr[33]);
            bank2.sethBaseIndex2(bank2arr[34]);
            bank2.setHash_h2(bank2arr[35]);
            bank2.setH_Response2(bank2arr[36]);
            bank2.setA(bank2arr[37]);
            bank2.setS(bank2arr[38]);
            bank2.setyHash(bank2arr[39]);
            bank2.setzHash(bank2arr[40]);
            bank2.setxHash(bank2arr[41]);
            bank2.setT1(bank2arr[42]);
            bank2.setT2(bank2arr[43]);
            bank2.setLx(ReadFileLxAndRx.readFile("bank2FileLx.txt"));
//            System.out.println(bank2.getLx());
            bank2.setRx(ReadFileLxAndRx.readFile("bank2FileRx.txt"));
            bank2.setT(bank2arr[44]);
            bank2.setTaox(bank2arr[45]);
            bank2.setMiu(bank2arr[46]);

            //银行3的交易信息
            Bank bank3 = new Bank();
            BigInteger[] bank3arr = commitOtherBank("bank3", BigInteger.valueOf(0), arr);
            bank3.setCom(bank3arr[0]);
            bank3.setBitCom(bank3arr[1]);
            bank3.setisFrom(bank3arr[2].intValue());
            bank3.setDoubleCom(bank3arr[3]);
            bank3.setTripleCom(bank3arr[4]);
            bank3.setQuadraCom(bank3arr[5]);
            bank3.setBitComTemp1(bank3arr[6]);
            bank3.setBitComTemp2(bank3arr[7]);
            bank3.setChallenge(bank3arr[8]);
            bank3.setHash_challenge(bank3arr[9]);
            bank3.setBitComResponse1(bank3arr[10]);
            bank3.setBitComResponse2(bank3arr[11]);
            bank3.setBitComResponse3(bank3arr[12]);
            bank3.setTemp1(bank3arr[13]);
            bank3.setTemp1C(bank3arr[14]);
            bank3.setTemp1S(bank3arr[15]);
            bank3.setBitComMulTemp1(bank3arr[16]);
            bank3.setComTemp1(bank3arr[17]);
            bank3.setComTemp1C(bank3arr[18]);
            bank3.setComTemp1S(bank3arr[19]);
            bank3.setIndexEqualTemp1(bank3arr[20]);
            bank3.setIndexEqualTemp2(bank3arr[21]);
            bank3.setIndexEqualTemp3(bank3arr[22]);
            bank3.setIndexEqualTemp4(bank3arr[23]);
            bank3.setComSAddVIndex(bank3arr[24]);
            bank3.setComSIndex(bank3arr[25]);
            bank3.sethBaseIndex(bank3arr[26]);
            bank3.setHash_h(bank3arr[27]);
            bank3.setH_Response(bank3arr[28]);
            bank3.setComSAddVSquareIndex(bank3arr[29]);
            bank3.sethBaseIndex1(bank3arr[30]);
            bank3.setHash_h1(bank3arr[31]);
            bank3.setH_Response1(bank3arr[32]);
            bank3.setComSAddVTripleIndex(bank3arr[33]);
            bank3.sethBaseIndex2(bank3arr[34]);
            bank3.setHash_h2(bank3arr[35]);
            bank3.setH_Response2(bank3arr[36]);
            bank3.setA(bank3arr[37]);
            bank3.setS(bank3arr[38]);
            bank3.setyHash(bank3arr[39]);
            bank3.setzHash(bank3arr[40]);
            bank3.setxHash(bank3arr[41]);
            bank3.setT1(bank3arr[42]);
            bank3.setT2(bank3arr[43]);
            bank3.setLx(ReadFileLxAndRx.readFile("bank3FileLx.txt"));
//            System.out.println(bank3.getLx());
            bank3.setRx(ReadFileLxAndRx.readFile("bank3FileRx.txt"));
            bank3.setT(bank3arr[44]);
            bank3.setTaox(bank3arr[45]);
            bank3.setMiu(bank3arr[46]);

            //银行4的交易信息
            Bank bank4 = new Bank();
            BigInteger[] bank4arr = commitOtherBank("bank4",BigInteger.valueOf(0), arr);
            bank4.setCom(bank4arr[0]);
            bank4.setBitCom(bank4arr[1]);
            bank4.setisFrom(bank4arr[2].intValue());
            bank4.setDoubleCom(bank4arr[3]);
            bank4.setTripleCom(bank4arr[4]);
            bank4.setQuadraCom(bank4arr[5]);
            bank4.setBitComTemp1(bank4arr[6]);
            bank4.setBitComTemp2(bank4arr[7]);
            bank4.setChallenge(bank4arr[8]);
            bank4.setHash_challenge(bank4arr[9]);
            bank4.setBitComResponse1(bank4arr[10]);
            bank4.setBitComResponse2(bank4arr[11]);
            bank4.setBitComResponse3(bank4arr[12]);
            bank4.setTemp1(bank4arr[13]);
            bank4.setTemp1C(bank4arr[14]);
            bank4.setTemp1S(bank4arr[15]);
            bank4.setBitComMulTemp1(bank4arr[16]);
            bank4.setComTemp1(bank4arr[17]);
            bank4.setComTemp1C(bank4arr[18]);
            bank4.setComTemp1S(bank4arr[19]);
            bank4.setIndexEqualTemp1(bank4arr[20]);
            bank4.setIndexEqualTemp2(bank4arr[21]);
            bank4.setIndexEqualTemp3(bank4arr[22]);
            bank4.setIndexEqualTemp4(bank4arr[23]);
            bank4.setComSAddVIndex(bank4arr[24]);
            bank4.setComSIndex(bank4arr[25]);
            bank4.sethBaseIndex(bank4arr[26]);
            bank4.setHash_h(bank4arr[27]);
            bank4.setH_Response(bank4arr[28]);
            bank4.setComSAddVSquareIndex(bank4arr[29]);
            bank4.sethBaseIndex1(bank4arr[30]);
            bank4.setHash_h1(bank4arr[31]);
            bank4.setH_Response1(bank4arr[32]);
            bank4.setComSAddVTripleIndex(bank4arr[33]);
            bank4.sethBaseIndex2(bank4arr[34]);
            bank4.setHash_h2(bank4arr[35]);
            bank4.setH_Response2(bank4arr[36]);
            bank4.setA(bank4arr[37]);
            bank4.setS(bank4arr[38]);
            bank4.setyHash(bank4arr[39]);
            bank4.setzHash(bank4arr[40]);
            bank4.setxHash(bank4arr[41]);
            bank4.setT1(bank4arr[42]);
            bank4.setT2(bank4arr[43]);
            bank4.setLx(ReadFileLxAndRx.readFile("bank4FileLx.txt"));
//            System.out.println(bank4.getLx());
            bank4.setRx(ReadFileLxAndRx.readFile("bank4FileRx.txt"));
            bank4.setT(bank4arr[44]);
            bank4.setTaox(bank4arr[45]);
            bank4.setMiu(bank4arr[46]);

            /**
             * 该文件记录银行1的资产转给了哪个银行
             */
            FileWriter file = new FileWriter("bank1AssetsRecord1.txt",true);
            file.write("bank1 -> "+"bank2:" + value + "\n");
            file.close();

            mappingGen(bank1, bank2, bank3, bank4, str);

        } else if (str.equals("bank3")) {
            //银行2的交易信息
            Bank bank2 = new Bank();
            BigInteger[] bank2arr = commitOtherBank("bank2",BigInteger.valueOf(0), arr);
            bank2.setCom(bank2arr[0]);
            bank2.setBitCom(bank2arr[1]);
            bank2.setisFrom(bank2arr[2].intValue());
            bank2.setDoubleCom(bank2arr[3]);
            bank2.setTripleCom(bank2arr[4]);
            bank2.setQuadraCom(bank2arr[5]);
            bank2.setBitComTemp1(bank2arr[6]);
            bank2.setBitComTemp2(bank2arr[7]);
            bank2.setChallenge(bank2arr[8]);
            bank2.setHash_challenge(bank2arr[9]);
            bank2.setBitComResponse1(bank2arr[10]);
            bank2.setBitComResponse2(bank2arr[11]);
            bank2.setBitComResponse3(bank2arr[12]);
            bank2.setTemp1(bank2arr[13]);
            bank2.setTemp1C(bank2arr[14]);
            bank2.setTemp1S(bank2arr[15]);
            bank2.setBitComMulTemp1(bank2arr[16]);
            bank2.setComTemp1(bank2arr[17]);
            bank2.setComTemp1C(bank2arr[18]);
            bank2.setComTemp1S(bank2arr[19]);
            bank2.setIndexEqualTemp1(bank2arr[20]);
            bank2.setIndexEqualTemp2(bank2arr[21]);
            bank2.setIndexEqualTemp3(bank2arr[22]);
            bank2.setIndexEqualTemp4(bank2arr[23]);
            bank2.setComSAddVIndex(bank2arr[24]);
            bank2.setComSIndex(bank2arr[25]);
            bank2.sethBaseIndex(bank2arr[26]);
            bank2.setHash_h(bank2arr[27]);
            bank2.setH_Response(bank2arr[28]);
            bank2.setComSAddVSquareIndex(bank2arr[29]);
            bank2.sethBaseIndex1(bank2arr[30]);
            bank2.setHash_h1(bank2arr[31]);
            bank2.setH_Response1(bank2arr[32]);
            bank2.setComSAddVTripleIndex(bank2arr[33]);
            bank2.sethBaseIndex2(bank2arr[34]);
            bank2.setHash_h2(bank2arr[35]);
            bank2.setH_Response2(bank2arr[36]);
            bank2.setA(bank2arr[37]);
            bank2.setS(bank2arr[38]);
            bank2.setyHash(bank2arr[39]);
            bank2.setzHash(bank2arr[40]);
            bank2.setxHash(bank2arr[41]);
            bank2.setT1(bank2arr[42]);
            bank2.setT2(bank2arr[43]);
            bank2.setLx(ReadFileLxAndRx.readFile("bank2FileLx.txt"));
//            System.out.println(bank2.getLx());
            bank2.setRx(ReadFileLxAndRx.readFile("bank2FileRx.txt"));
            bank2.setT(bank2arr[44]);
            bank2.setTaox(bank2arr[45]);
            bank2.setMiu(bank2arr[46]);

            //银行3的交易信息
            Bank bank3 = new Bank();
            BigInteger[] bank3arr = commitOtherBank("bank3", value, arr);
            bank3.setCom(bank3arr[0]);
            bank3.setBitCom(bank3arr[1]);
            bank3.setisFrom(bank3arr[2].intValue());
            bank3.setDoubleCom(bank3arr[3]);
            bank3.setTripleCom(bank3arr[4]);
            bank3.setQuadraCom(bank3arr[5]);
            bank3.setBitComTemp1(bank3arr[6]);
            bank3.setBitComTemp2(bank3arr[7]);
            bank3.setChallenge(bank3arr[8]);
            bank3.setHash_challenge(bank3arr[9]);
            bank3.setBitComResponse1(bank3arr[10]);
            bank3.setBitComResponse2(bank3arr[11]);
            bank3.setBitComResponse3(bank3arr[12]);
            bank3.setTemp1(bank3arr[13]);
            bank3.setTemp1C(bank3arr[14]);
            bank3.setTemp1S(bank3arr[15]);
            bank3.setBitComMulTemp1(bank3arr[16]);
            bank3.setComTemp1(bank3arr[17]);
            bank3.setComTemp1C(bank3arr[18]);
            bank3.setComTemp1S(bank3arr[19]);
            bank3.setIndexEqualTemp1(bank3arr[20]);
            bank3.setIndexEqualTemp2(bank3arr[21]);
            bank3.setIndexEqualTemp3(bank3arr[22]);
            bank3.setIndexEqualTemp4(bank3arr[23]);
            bank3.setComSAddVIndex(bank3arr[24]);
            bank3.setComSIndex(bank3arr[25]);
            bank3.sethBaseIndex(bank3arr[26]);
            bank3.setHash_h(bank3arr[27]);
            bank3.setH_Response(bank3arr[28]);
            bank3.setComSAddVSquareIndex(bank3arr[29]);
            bank3.sethBaseIndex1(bank3arr[30]);
            bank3.setHash_h1(bank3arr[31]);
            bank3.setH_Response1(bank3arr[32]);
            bank3.setComSAddVTripleIndex(bank3arr[33]);
            bank3.sethBaseIndex2(bank3arr[34]);
            bank3.setHash_h2(bank3arr[35]);
            bank3.setH_Response2(bank3arr[36]);
            bank3.setA(bank3arr[37]);
            bank3.setS(bank3arr[38]);
            bank3.setyHash(bank3arr[39]);
            bank3.setzHash(bank3arr[40]);
            bank3.setxHash(bank3arr[41]);
            bank3.setT1(bank3arr[42]);
            bank3.setT2(bank3arr[43]);
            bank3.setLx(ReadFileLxAndRx.readFile("bank3FileLx.txt"));
//            System.out.println(bank3.getLx());
            bank3.setRx(ReadFileLxAndRx.readFile("bank3FileRx.txt"));
            bank3.setT(bank3arr[44]);
            bank3.setTaox(bank3arr[45]);
            bank3.setMiu(bank3arr[46]);

            //银行4的交易信息
            Bank bank4 = new Bank();
            BigInteger[] bank4arr = commitOtherBank("bank4",BigInteger.valueOf(0), arr);
            bank4.setCom(bank4arr[0]);
            bank4.setBitCom(bank4arr[1]);
            bank4.setisFrom(bank4arr[2].intValue());
            bank4.setDoubleCom(bank4arr[3]);
            bank4.setTripleCom(bank4arr[4]);
            bank4.setQuadraCom(bank4arr[5]);
            bank4.setBitComTemp1(bank4arr[6]);
            bank4.setBitComTemp2(bank4arr[7]);
            bank4.setChallenge(bank4arr[8]);
            bank4.setHash_challenge(bank4arr[9]);
            bank4.setBitComResponse1(bank4arr[10]);
            bank4.setBitComResponse2(bank4arr[11]);
            bank4.setBitComResponse3(bank4arr[12]);
            bank4.setTemp1(bank4arr[13]);
            bank4.setTemp1C(bank4arr[14]);
            bank4.setTemp1S(bank4arr[15]);
            bank4.setBitComMulTemp1(bank4arr[16]);
            bank4.setComTemp1(bank4arr[17]);
            bank4.setComTemp1C(bank4arr[18]);
            bank4.setComTemp1S(bank4arr[19]);
            bank4.setIndexEqualTemp1(bank4arr[20]);
            bank4.setIndexEqualTemp2(bank4arr[21]);
            bank4.setIndexEqualTemp3(bank4arr[22]);
            bank4.setIndexEqualTemp4(bank4arr[23]);
            bank4.setComSAddVIndex(bank4arr[24]);
            bank4.setComSIndex(bank4arr[25]);
            bank4.sethBaseIndex(bank4arr[26]);
            bank4.setHash_h(bank4arr[27]);
            bank4.setH_Response(bank4arr[28]);
            bank4.setComSAddVSquareIndex(bank4arr[29]);
            bank4.sethBaseIndex1(bank4arr[30]);
            bank4.setHash_h1(bank4arr[31]);
            bank4.setH_Response1(bank4arr[32]);
            bank4.setComSAddVTripleIndex(bank4arr[33]);
            bank4.sethBaseIndex2(bank4arr[34]);
            bank4.setHash_h2(bank4arr[35]);
            bank4.setH_Response2(bank4arr[36]);
            bank4.setA(bank4arr[37]);
            bank4.setS(bank4arr[38]);
            bank4.setyHash(bank4arr[39]);
            bank4.setzHash(bank4arr[40]);
            bank4.setxHash(bank4arr[41]);
            bank4.setT1(bank4arr[42]);
            bank4.setT2(bank4arr[43]);
            bank4.setLx(ReadFileLxAndRx.readFile("bank4FileLx.txt"));
//            System.out.println(bank4.getLx());
            bank4.setRx(ReadFileLxAndRx.readFile("bank4FileRx.txt"));
            bank4.setT(bank4arr[44]);
            bank4.setTaox(bank4arr[45]);
            bank4.setMiu(bank4arr[46]);

            /**
             * 该文件记录银行1的资产转给了哪个银行
             */
            FileWriter file = new FileWriter("bank1AssetsRecord1.txt",true);
            file.write("bank1 -> "+"bank3:" + value + "\n");
            file.close();

            mappingGen(bank1, bank2, bank3, bank4, str);

        } else if (str.equals("bank4")) {
            //银行2的交易信息
            Bank bank2 = new Bank();
            BigInteger[] bank2arr = commitOtherBank("bank2",BigInteger.valueOf(0), arr);
            bank2.setCom(bank2arr[0]);
            bank2.setBitCom(bank2arr[1]);
            bank2.setisFrom(bank2arr[2].intValue());
            bank2.setDoubleCom(bank2arr[3]);
            bank2.setTripleCom(bank2arr[4]);
            bank2.setQuadraCom(bank2arr[5]);
            bank2.setBitComTemp1(bank2arr[6]);
            bank2.setBitComTemp2(bank2arr[7]);
            bank2.setChallenge(bank2arr[8]);
            bank2.setHash_challenge(bank2arr[9]);
            bank2.setBitComResponse1(bank2arr[10]);
            bank2.setBitComResponse2(bank2arr[11]);
            bank2.setBitComResponse3(bank2arr[12]);
            bank2.setTemp1(bank2arr[13]);
            bank2.setTemp1C(bank2arr[14]);
            bank2.setTemp1S(bank2arr[15]);
            bank2.setBitComMulTemp1(bank2arr[16]);
            bank2.setComTemp1(bank2arr[17]);
            bank2.setComTemp1C(bank2arr[18]);
            bank2.setComTemp1S(bank2arr[19]);
            bank2.setIndexEqualTemp1(bank2arr[20]);
            bank2.setIndexEqualTemp2(bank2arr[21]);
            bank2.setIndexEqualTemp3(bank2arr[22]);
            bank2.setIndexEqualTemp4(bank2arr[23]);
            bank2.setComSAddVIndex(bank2arr[24]);
            bank2.setComSIndex(bank2arr[25]);
            bank2.sethBaseIndex(bank2arr[26]);
            bank2.setHash_h(bank2arr[27]);
            bank2.setH_Response(bank2arr[28]);
            bank2.setComSAddVSquareIndex(bank2arr[29]);
            bank2.sethBaseIndex1(bank2arr[30]);
            bank2.setHash_h1(bank2arr[31]);
            bank2.setH_Response1(bank2arr[32]);
            bank2.setComSAddVTripleIndex(bank2arr[33]);
            bank2.sethBaseIndex2(bank2arr[34]);
            bank2.setHash_h2(bank2arr[35]);
            bank2.setH_Response2(bank2arr[36]);
            bank2.setA(bank2arr[37]);
            bank2.setS(bank2arr[38]);
            bank2.setyHash(bank2arr[39]);
            bank2.setzHash(bank2arr[40]);
            bank2.setxHash(bank2arr[41]);
            bank2.setT1(bank2arr[42]);
            bank2.setT2(bank2arr[43]);
            bank2.setLx(ReadFileLxAndRx.readFile("bank2FileLx.txt"));
//            System.out.println(bank2.getLx());
            bank2.setRx(ReadFileLxAndRx.readFile("bank2FileRx.txt"));
            bank2.setT(bank2arr[44]);
            bank2.setTaox(bank2arr[45]);
            bank2.setMiu(bank2arr[46]);

            //银行3的交易信息
            Bank bank3 = new Bank();
            BigInteger[] bank3arr = commitOtherBank("bank3",BigInteger.valueOf(0), arr);
            bank3.setCom(bank3arr[0]);
            bank3.setBitCom(bank3arr[1]);
            bank3.setisFrom(bank3arr[2].intValue());
            bank3.setDoubleCom(bank3arr[3]);
            bank3.setTripleCom(bank3arr[4]);
            bank3.setQuadraCom(bank3arr[5]);
            bank3.setBitComTemp1(bank3arr[6]);
            bank3.setBitComTemp2(bank3arr[7]);
            bank3.setChallenge(bank3arr[8]);
            bank3.setHash_challenge(bank3arr[9]);
            bank3.setBitComResponse1(bank3arr[10]);
            bank3.setBitComResponse2(bank3arr[11]);
            bank3.setBitComResponse3(bank3arr[12]);
            bank3.setTemp1(bank3arr[13]);
            bank3.setTemp1C(bank3arr[14]);
            bank3.setTemp1S(bank3arr[15]);
            bank3.setBitComMulTemp1(bank3arr[16]);
            bank3.setComTemp1(bank3arr[17]);
            bank3.setComTemp1C(bank3arr[18]);
            bank3.setComTemp1S(bank3arr[19]);
            bank3.setIndexEqualTemp1(bank3arr[20]);
            bank3.setIndexEqualTemp2(bank3arr[21]);
            bank3.setIndexEqualTemp3(bank3arr[22]);
            bank3.setIndexEqualTemp4(bank3arr[23]);
            bank3.setComSAddVIndex(bank3arr[24]);
            bank3.setComSIndex(bank3arr[25]);
            bank3.sethBaseIndex(bank3arr[26]);
            bank3.setHash_h(bank3arr[27]);
            bank3.setH_Response(bank3arr[28]);
            bank3.setComSAddVSquareIndex(bank3arr[29]);
            bank3.sethBaseIndex1(bank3arr[30]);
            bank3.setHash_h1(bank3arr[31]);
            bank3.setH_Response1(bank3arr[32]);
            bank3.setComSAddVTripleIndex(bank3arr[33]);
            bank3.sethBaseIndex2(bank3arr[34]);
            bank3.setHash_h2(bank3arr[35]);
            bank3.setH_Response2(bank3arr[36]);
            bank3.setA(bank3arr[37]);
            bank3.setS(bank3arr[38]);
            bank3.setyHash(bank3arr[39]);
            bank3.setzHash(bank3arr[40]);
            bank3.setxHash(bank3arr[41]);
            bank3.setT1(bank3arr[42]);
            bank3.setT2(bank3arr[43]);
            bank3.setLx(ReadFileLxAndRx.readFile("bank3FileLx.txt"));
//            System.out.println(bank3.getLx());
            bank3.setRx(ReadFileLxAndRx.readFile("bank3FileRx.txt"));
            bank3.setT(bank3arr[44]);
            bank3.setTaox(bank3arr[45]);
            bank3.setMiu(bank3arr[46]);

            //银行4的交易信息
            Bank bank4 = new Bank();
            BigInteger[] bank4arr = commitOtherBank("bank4",value, arr);
            bank4.setCom(bank4arr[0]);
            bank4.setBitCom(bank4arr[1]);
            bank4.setisFrom(bank4arr[2].intValue());
            bank4.setDoubleCom(bank4arr[3]);
            bank4.setTripleCom(bank4arr[4]);
            bank4.setQuadraCom(bank4arr[5]);
            bank4.setBitComTemp1(bank4arr[6]);
            bank4.setBitComTemp2(bank4arr[7]);
            bank4.setChallenge(bank4arr[8]);
            bank4.setHash_challenge(bank4arr[9]);
            bank4.setBitComResponse1(bank4arr[10]);
            bank4.setBitComResponse2(bank4arr[11]);
            bank4.setBitComResponse3(bank4arr[12]);
            bank4.setTemp1(bank4arr[13]);
            bank4.setTemp1C(bank4arr[14]);
            bank4.setTemp1S(bank4arr[15]);
            bank4.setBitComMulTemp1(bank4arr[16]);
            bank4.setComTemp1(bank4arr[17]);
            bank4.setComTemp1C(bank4arr[18]);
            bank4.setComTemp1S(bank4arr[19]);
            bank4.setIndexEqualTemp1(bank4arr[20]);
            bank4.setIndexEqualTemp2(bank4arr[21]);
            bank4.setIndexEqualTemp3(bank4arr[22]);
            bank4.setIndexEqualTemp4(bank4arr[23]);
            bank4.setComSAddVIndex(bank4arr[24]);
            bank4.setComSIndex(bank4arr[25]);
            bank4.sethBaseIndex(bank4arr[26]);
            bank4.setHash_h(bank4arr[27]);
            bank4.setH_Response(bank4arr[28]);
            bank4.setComSAddVSquareIndex(bank4arr[29]);
            bank4.sethBaseIndex1(bank4arr[30]);
            bank4.setHash_h1(bank4arr[31]);
            bank4.setH_Response1(bank4arr[32]);
            bank4.setComSAddVTripleIndex(bank4arr[33]);
            bank4.sethBaseIndex2(bank4arr[34]);
            bank4.setHash_h2(bank4arr[35]);
            bank4.setH_Response2(bank4arr[36]);
            bank4.setA(bank4arr[37]);
            bank4.setS(bank4arr[38]);
            bank4.setyHash(bank4arr[39]);
            bank4.setzHash(bank4arr[40]);
            bank4.setxHash(bank4arr[41]);
            bank4.setT1(bank4arr[42]);
            bank4.setT2(bank4arr[43]);
            bank4.setLx(ReadFileLxAndRx.readFile("bank4FileLx.txt"));
//            System.out.println(bank4.getLx());
            bank4.setRx(ReadFileLxAndRx.readFile("bank4FileRx.txt"));
            bank4.setT(bank4arr[44]);
            bank4.setTaox(bank4arr[45]);
            bank4.setMiu(bank4arr[46]);

            /**
             * 该文件记录银行1的资产转给了哪个银行
             */
            FileWriter file = new FileWriter("bank1AssetsRecord1.txt",true);
            file.write("bank1 -> "+"bank4:" + value + "\n");
            file.close();


            mappingGen(bank1, bank2, bank3, bank4, str);

        }
    }


    /**
     * 该函数是为了把证明资产平衡证明和01比特证明的字段添加到具体交易信息的后部去，需要新建BankBalanceAndBitZKP类，然后把该类添加哈希表，转换成json,
     * 之后拼接到交易信息的json字符串之后。
     * 这里的str主要是在证明指数相等的时候，记录把钱转给了哪个银行。
     */
    public static String BalanceAndBitProofParam(String str) throws IOException {
        String str1 = null;

        //零知识证明的参数表
        BankBalanceAndBitZKP ZKPPraam = new BankBalanceAndBitZKP();
        BigInteger[] arr = balanceZkpGenerate.generateBanlanceProofField(Commit.readBase(),str);
        ZKPPraam.setC1(arr[0]);
        ZKPPraam.setD(arr[1]);
        ZKPPraam.setD1(arr[2]);
        ZKPPraam.setD2(arr[3]);
        ZKPPraam.setC2(arr[4]);
        ZKPPraam.setM(arr[5]);
        ZKPPraam.setM1(arr[6]);
        ZKPPraam.setM2(arr[7]);
        ZKPPraam.setTwoObject(arr[8]);
        ZKPPraam.setAllR2_Sum(arr[9]);
        ZKPPraam.setH2(arr[10]);
        ZKPPraam.setN(arr[11]);
        ZKPPraam.setN1(arr[12]);
        ZKPPraam.setN2(arr[13]);

        TreeMap<String, BankBalanceAndBitZKP> treemap1 = new TreeMap<>();
        treemap1.put("balProofParam", ZKPPraam);

        str1 = JSON.toJSONString(treemap1);

        return str1;
    }

    /**
     *
     * @param bank1
     * @param bank2
     * @param bank3
     * @param bank4
     * @throws IOException
     */

    static int numId = 0;
    //把四个银行的信息生成一个mapping
    public static void mappingGen(Bank bank1, Bank bank2, Bank bank3, Bank bank4, String bankStr) throws IOException {
        //银行与银行里边的每个字段对应是key-value,其中key是字符串"bank1", value是bank对象
        //新建TreeMap,按键值进行排序
        TreeMap<String, Bank> treemap = new TreeMap<>();
        treemap.put("bank1", bank1);
        treemap.put("bank2", bank2);
        treemap.put("bank3", bank3);
        treemap.put("bank4", bank4);
//        System.out.println(treemap);
//        for(String key: treemap.keySet()){
//            System.out.println("key:" + key + "\t" + "value:" + treemap.get(key));
//        }

        String str = JSON.toJSONString(treemap);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String time = "{" + "\"time\":" + "\"" + df.format(new Date()) + "\""+",\"bank1\"";
        str = str.replace("{\"bank1\"",time);


        //下面调用BalanceAndBitProofParam()，把生成的json字符串进行拼接
        String str1 = BalanceAndBitProofParam(bankStr);
        str1 = str1.replaceFirst("\\{","},");
//        System.out.println(str1);
        //将str字符串的最后一个}替换成str1
        str = str.replace("}}",str1);
//        FileWriter file1 = new FileWriter("oneTransaction.txt");
//        file1.write(str);
//        file1.close();


//        str = str.replaceAll("\"","\\\\\"");
//        System.out.println(getType(str));
        System.out.println(str);
//        FileWriter file1 = new FileWriter("bank11000元.txt");
//        file1.write(str);
//        file1.close();

        /**
         * 发送各个银行承诺的随机数到各个银行
         */
        SendMain.send();
        /**
         * 下面开始调用上链的invoke函数，数据上链
         */
        //链上保存的交易信息是键值对的形式，所以键我们设置成id,让它自增，然后转换成String形式，传给invoke函数作为参数
//        numId++;
//        String keyNum = String.valueOf(numId);

//        System.out.println(numId);
            Invoke.invoke(Connection.getNetwork(), str);

    }

    public static String getType(Object obj) {
        return obj.getClass().getName();
    }
}
