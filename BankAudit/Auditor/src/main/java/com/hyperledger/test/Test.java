package com.hyperledger.test;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyperledger.audit.*;
import com.hyperledger.connection.Connection;
import com.hyperledger.connection.Query;
import com.hyperledger.connection.Selections;
import com.hyperledger.socket.Client;
import com.hyperledger.socket.InitializeAssets;
import com.hyperledger.socket.ReadBigInteger;
import com.sun.javafx.collections.MappingChange;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Scanner;

import static com.hyperledger.connection.ReadBase.readBase;

//@RunWith(SpringRunner.class)
//@SpringBootTest
@RestController
@CrossOrigin
public class Test {

    @RequestMapping("/break")
    public String exit(@RequestBody Map map) {
        String mInput = map.get("break").toString();
        System.out.println(mInput);
        FlushFile.flushFile("initTransaction.txt");
        FlushFile.flushFile("allTransactions.txt");
        FlushFile.flushFile("oneTransaction.txt");
        FlushFile.flushFile("initializeRandom.txt");
        return "Successful exit!";
    }

    @RequestMapping("/start")
    public String initLedger(@RequestBody Map map) {
        String mInput = map.get("init").toString();
        System.out.println(mInput);
        InitializeAssets.initializeAssets();
        return "初始化成功！";
    }

    @RequestMapping("/auditRequestBank")
    public JSONObject auditRequestBank(@RequestBody Map map) throws IOException{
        String object = map.get("object").toString();
        String type = map.get("type").toString();
        JSONObject json = new JSONObject();
        Map result = new ModelMap();
        String strRequest = new String();

        String[] bankNum = {"bank1", "bank2", "bank3", "bank4"};
        Integer[] portNum = {8881, 8882, 8883, 8884};
        for (int i = 0; i < portNum.length; i++) {
            Client.client("2", bankNum[i], "127.0.0.1", portNum[i]);
        }

        //获取交易次数
        Map bank1Sum = new ModelMap();
        Map bank2Sum = new ModelMap();
        Map bank3Sum = new ModelMap();
        Map bank4Sum = new ModelMap();

        bank1Sum = getBankStr("bank1");
        bank2Sum = getBankStr("bank2");
        bank3Sum = getBankStr("bank3");
        bank4Sum = getBankStr("bank4");

        BigInteger bank1TransactionTime = new BigInteger(bank1Sum.get("transactionTime").toString());
        BigInteger bank2TransactionTime = new BigInteger(bank2Sum.get("transactionTime").toString());
        BigInteger bank3TransactionTime = new BigInteger(bank3Sum.get("transactionTime").toString());
        BigInteger bank4TransactionTime = new BigInteger(bank4Sum.get("transactionTime").toString());
        int endId = (bank1TransactionTime.intValue()+bank2TransactionTime.intValue()+bank3TransactionTime.intValue()+bank4TransactionTime.intValue())/2;

        //获取交易
        Query.queryAll(endId, Connection.getContract(Connection.getNetwork()));

        //获取银行的需要审计的值和盲签名
        Map bankSum = new ModelMap();
        bankSum = getBankStr(object);
        System.out.println(bankSum);

        //对银行进行审计，并输出到map中

        switch (type){
            case "0":
                result.put("AssetsSum",Sum.bankAssetsSum(object,new BigInteger(bankSum.get("comSum").toString()),new BigInteger(bankSum.get("r1").toString()), readBase()));
                result.put("TransactionAverageValue",Sum.bankTransactionAverageValue(object, new BigInteger(bankSum.get("transactionTime").toString()), new BigInteger(bankSum.get("r2").toString()), readBase()).toString());
                result.put("TransactionVariance", Variance.calculateVariance(object, new BigInteger(bankSum.get("doubleComSum").toString()),new BigInteger(bankSum.get("x").toString()), readBase()).toString());
                result.put("TransactionSkewness", Skewness.calaulateSkewness(object, new BigInteger(bankSum.get("tripleComSum").toString()), new BigInteger(bankSum.get("y").toString()), readBase()).toString());
                result.put("TransactionKurtosis", Kurtosis.calculateKurtosis(object, new BigInteger(bankSum.get("quadraComSum").toString()), new BigInteger(bankSum.get("z").toString()), readBase()).toString());

                strRequest = object + result.get("AssetsSum").toString()+"<br>";
                strRequest = strRequest + object + "交易平均值：" + result.get("TransactionAverageValue") + "<br>";
                strRequest = strRequest + object + "交易方差：" + result.get("TransactionVariance") + "<br>";
                strRequest = strRequest + object + "交易偏度：" + result.get("TransactionSkewness") + "<br>";
                strRequest = strRequest + object + "交易峰度：" + result.get("TransactionKurtosis") + "<br>";

//                json.put("comSum",bankSum.get("comSum").toString());
                json.put("r1",object+"总资产承诺值："+bankSum.get("r1").toString());
//                json.put("transactionTime",bankSum.get("transactionTime").toString());
                json.put("r2",object+"交易次数承诺："+bankSum.get("r2").toString());
//                json.put("doubleComSum",bankSum.get("doubleComSum").toString());
                json.put("x",object+"交易平方和承诺："+bankSum.get("x").toString());
//                json.put("tripleComSum",bankSum.get("tripleComSum").toString());
                json.put("y",object+"交易立方和承诺："+bankSum.get("y").toString());
//                json.put("quadraComSum",bankSum.get("quadraComSum").toString());
                json.put("z",object+"交易四次方和承诺："+bankSum.get("z").toString());
                break;

            case "1":
                result.put("AssetsSum",Sum.bankAssetsSum(object,new BigInteger(bankSum.get("comSum").toString()),new BigInteger(bankSum.get("r1").toString()), readBase()));
                strRequest = object + result.get("AssetsSum").toString()+"<br>";

                json.put("blinding","对系统资产进行审计...<br>"+object+"总资产承诺值："+bankSum.get("r1").toString());
                break;

            case "2":
                result.put("TransactionAverageValue",Sum.bankTransactionAverageValue(object, new BigInteger(bankSum.get("transactionTime").toString()), new BigInteger(bankSum.get("r2").toString()), readBase()).toString());
                strRequest = object + "交易平均值：" + result.get("TransactionAverageValue") + "<br>";

                json.put("blinding","对交易次数进行审计...<br>"+object+"交易次数承诺："+bankSum.get("r2").toString());
                break;

            case "3":
                result.put("TransactionVariance", Variance.calculateVariance(object, new BigInteger(bankSum.get("doubleComSum").toString()),new BigInteger(bankSum.get("x").toString()), readBase()).toString());
                strRequest = object + "交易方差：" + result.get("TransactionVariance") + "<br>";

                json.put("blinding","对交易平方和进行审计...<br>"+object+"交易平方和承诺："+bankSum.get("x").toString());
                break;

            case "4":
                result.put("TransactionSkewness", Skewness.calaulateSkewness(object, new BigInteger(bankSum.get("tripleComSum").toString()), new BigInteger(bankSum.get("y").toString()), readBase()).toString());
                strRequest = object + "交易偏度：" + result.get("TransactionSkewness") + "<br>";

                json.put("blinding","对交易立方和进行审计...<br>"+object+"交易立方和承诺："+bankSum.get("y").toString());
                break;

            case "5":
                result.put("TransactionKurtosis", Kurtosis.calculateKurtosis(object, new BigInteger(bankSum.get("quadraComSum").toString()), new BigInteger(bankSum.get("z").toString()), readBase()).toString());
                strRequest = object + "交易峰度：" + result.get("TransactionKurtosis") + "<br>";

                json.put("blinding","对交易四次方和进行审计...<br>"+object+"交易四次方和承诺："+bankSum.get("z").toString());
                break;
        }

        json.put("strRequest",strRequest);
        return json;
    }

    @RequestMapping("/auditRequestSystem")
    public JSONObject auditRequestSystem(@RequestBody Map map) throws IOException {
        String type = map.get("type").toString();
        JSONObject json = new JSONObject();
        Map result = new ModelMap();
        String strRequest = new String();

        bankAudit2("bank1");
        bankAudit2("bank2");
        bankAudit2("bank3");
        bankAudit2("bank4");

        Map bank1Sum = new ModelMap();
        Map bank2Sum = new ModelMap();
        Map bank3Sum = new ModelMap();
        Map bank4Sum = new ModelMap();

        bank1Sum = getBankStr("bank1");
        bank2Sum = getBankStr("bank2");
        bank3Sum = getBankStr("bank3");
        bank4Sum = getBankStr("bank4");

        json.put("bank1Blinding","bank1的总资产承诺："+bank1Sum.get("r1").toString());
        json.put("bank2Blinding","bank2的总资产承诺："+bank2Sum.get("r1").toString());
        json.put("bank3Blinding","bank3的总资产承诺："+bank3Sum.get("r1").toString());
        json.put("bank4Blinding","bank4的总资产承诺："+bank4Sum.get("r1").toString());

        switch (type){
            case "0":
                //求系统指标
                BigInteger systemSum = new BigInteger("0");
                try {
                    systemSum = MarketRate.systemAssetsSum("bank1", "bank2", "bank3", "bank4");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                System.out.println("整个系统的资产和= " + systemSum);
                    strRequest = "整个系统的资产和为： " + systemSum + "<br>";
                }
                result.put("SystemAssetsSum",systemSum.toString());


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
                    strRequest = strRequest + "bank1的市场占有率：" + bank1MarketRate + "<br>";
                    strRequest = strRequest + "bank2的市场占有率：" + bank2MarketRate + "<br>";
                    strRequest = strRequest + "bank3的市场占有率：" + bank3MarketRate + "<br>";
                    strRequest = strRequest + "bank4的市场占有率：" + bank4MarketRate + "<br>";
//                System.out.println("bank1的市场占有率：" + bank1MarketRate);
//                System.out.println("bank2的市场占有率：" + bank2MarketRate);
//                System.out.println("bank3的市场占有率：" + bank3MarketRate);
//                System.out.println("bank4的市场占有率：" + bank4MarketRate);
                }
                result.put("bank1MarketRate",bank1MarketRate.toString());
                result.put("bank2MarketRate",bank2MarketRate.toString());
                result.put("bank3MarketRate",bank3MarketRate.toString());
                result.put("bank4MarketRate",bank4MarketRate.toString());

                BigDecimal rTSAV = new BigDecimal("0");
                try {
                    rTSAV = RealTimeAveragePrice.realTimeAveragePrice("bank1", "bank2", "bank3", "bank4");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                System.out.println("系统的实时平均价格：" + rTSAV);
                    strRequest = strRequest + "系统的实时平均价格：" + rTSAV +"<br>";
                }
                result.put("RealTimeTransactionAverageValue",rTSAV);

                BigDecimal hhi = new BigDecimal("0");
                try {
                    hhi = HHI.HHIredix();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                System.out.println("系统的HHI指数：" + hhi);
                    strRequest = strRequest + "系统的HHI指数：" + hhi +"<br>";
                }
                result.put("HHIIndex",hhi);
                System.out.println(result);

                break;

            case "1":
                BigInteger systemSum1 = new BigInteger("0");
                try {
                    systemSum1 = MarketRate.systemAssetsSum("bank1", "bank2", "bank3", "bank4");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                System.out.println("整个系统的资产和= " + systemSum);
                    strRequest = "整个系统的资产和为： " + systemSum1 + "<br>";
                }
                result.put("SystemAssetsSum",systemSum1.toString());
                break;

            case "2":
                BigDecimal bank1MarketRate2 = new BigDecimal("0");
                BigDecimal bank2MarketRate2 = new BigDecimal("0");
                BigDecimal bank3MarketRate2 = new BigDecimal("0");
                BigDecimal bank4MarketRate2 = new BigDecimal("0");
                try {
                    bank1MarketRate2 = MarketRate.marketRate("bank1");
                    bank2MarketRate2 = MarketRate.marketRate("bank2");
                    bank3MarketRate2 = MarketRate.marketRate("bank3");
                    bank4MarketRate2 = MarketRate.marketRate("bank4");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    strRequest ="bank1的市场占有率：" + bank1MarketRate2 + "<br>";
                    strRequest = strRequest + "bank2的市场占有率：" + bank2MarketRate2 + "<br>";
                    strRequest = strRequest + "bank3的市场占有率：" + bank3MarketRate2 + "<br>";
                    strRequest = strRequest + "bank4的市场占有率：" + bank4MarketRate2 + "<br>";
//                System.out.println("bank1的市场占有率：" + bank1MarketRate);
//                System.out.println("bank2的市场占有率：" + bank2MarketRate);
//                System.out.println("bank3的市场占有率：" + bank3MarketRate);
//                System.out.println("bank4的市场占有率：" + bank4MarketRate);
                }
                result.put("bank1MarketRate",bank1MarketRate2.toString());
                result.put("bank2MarketRate",bank2MarketRate2.toString());
                result.put("bank3MarketRate",bank3MarketRate2.toString());
                result.put("bank4MarketRate",bank4MarketRate2.toString());
                break;

            case "3":
                BigDecimal rTSAV3 = new BigDecimal("0");
                try {
                    rTSAV3 = RealTimeAveragePrice.realTimeAveragePrice("bank1", "bank2", "bank3", "bank4");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                System.out.println("系统的实时平均价格：" + rTSAV);
                    strRequest ="系统的实时平均价格：" + rTSAV3 +"<br>";
                }
                result.put("RealTimeTransactionAverageValue",rTSAV3);
                break;

            case "4":
                BigDecimal hhi4 = new BigDecimal("0");
                try {
                    hhi4 = HHI.HHIredix();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
//                System.out.println("系统的HHI指数：" + hhi);
                    strRequest = "系统的HHI指数：" + hhi4 +"<br>";
                }
                result.put("HHIIndex",hhi4);
                break;
        }

        json.put("strRequest",strRequest);
        return json;
    }

    @RequestMapping("/auditRequest")
    public String auditRequest(@RequestBody Map map) throws IOException {
        String object = map.get("object").toString();
        String time = map.get("time").toString();
        Map result = new ModelMap();
        String strRequest = new String();

        if (object.equals("system")) {
            //对四个银行分别进行一次审计
            bankAudit2("bank1");
            bankAudit2("bank2");
            bankAudit2("bank3");
            bankAudit2("bank4");

            //求系统指标
            BigInteger systemSum = new BigInteger("0");
            try {
                systemSum = MarketRate.systemAssetsSum("bank1", "bank2", "bank3", "bank4");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
//                System.out.println("整个系统的资产和= " + systemSum);
                strRequest = "整个系统的资产和= " + systemSum + "<br>";
            }
            result.put("SystemAssetsSum",systemSum.toString());


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
                strRequest = strRequest + "bank1的市场占有率：" + bank1MarketRate + "<br>";
                strRequest = strRequest + "bank2的市场占有率：" + bank2MarketRate + "<br>";
                strRequest = strRequest + "bank3的市场占有率：" + bank3MarketRate + "<br>";
                strRequest = strRequest + "bank4的市场占有率：" + bank4MarketRate + "<br>";
//                System.out.println("bank1的市场占有率：" + bank1MarketRate);
//                System.out.println("bank2的市场占有率：" + bank2MarketRate);
//                System.out.println("bank3的市场占有率：" + bank3MarketRate);
//                System.out.println("bank4的市场占有率：" + bank4MarketRate);
            }
            result.put("bank1MarketRate",bank1MarketRate.toString());
            result.put("bank2MarketRate",bank2MarketRate.toString());
            result.put("bank3MarketRate",bank3MarketRate.toString());
            result.put("bank4MarketRate",bank4MarketRate.toString());

            BigDecimal rTSAV = new BigDecimal("0");
            try {
                rTSAV = RealTimeAveragePrice.realTimeAveragePrice("bank1", "bank2", "bank3", "bank4");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
//                System.out.println("系统的实时平均价格：" + rTSAV);
                strRequest = strRequest + "系统的实时平均价格：" + rTSAV +"<br>";
            }
            result.put("RealTimeTransactionAverageValue",rTSAV);

            BigDecimal hhi = new BigDecimal("0");
            try {
                hhi = HHI.HHIredix();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
//                System.out.println("系统的HHI指数：" + hhi);
                strRequest = strRequest + "系统的HHI指数：" + hhi +"<br>";
            }
            result.put("HHIIndex",hhi);
        }else{

            String[] bankNum = {"bank1", "bank2", "bank3", "bank4"};
            Integer[] portNum = {8881, 8882, 8883, 8884};
            for (int i = 0; i < portNum.length; i++) {
                Client.client("2", bankNum[i], "127.0.0.1", portNum[i]);
            }



            //获取交易次数
            Map bank1Sum = new ModelMap();
            Map bank2Sum = new ModelMap();
            Map bank3Sum = new ModelMap();
            Map bank4Sum = new ModelMap();

            bank1Sum = getBankStr("bank1");
            bank2Sum = getBankStr("bank2");
            bank3Sum = getBankStr("bank3");
            bank4Sum = getBankStr("bank4");

            BigInteger bank1TransactionTime = new BigInteger(bank1Sum.get("transactionTime").toString());
            BigInteger bank2TransactionTime = new BigInteger(bank2Sum.get("transactionTime").toString());
            BigInteger bank3TransactionTime = new BigInteger(bank3Sum.get("transactionTime").toString());
            BigInteger bank4TransactionTime = new BigInteger(bank4Sum.get("transactionTime").toString());


//            BigInteger bank1TransactionTime = ReadBigInteger.readBigInteger("bank1TransactionTime.txt");
//            BigInteger bank2TransactionTime = ReadBigInteger.readBigInteger("bank2TransactionTime.txt");
//            BigInteger bank3TransactionTime = ReadBigInteger.readBigInteger("bank3TransactionTime.txt");
//            BigInteger bank4TransactionTime = ReadBigInteger.readBigInteger("bank4TransactionTime.txt");
            int endId = (bank1TransactionTime.intValue()+bank2TransactionTime.intValue()+bank3TransactionTime.intValue()+bank4TransactionTime.intValue())/2;

            //获取交易
            Query.queryAll(endId, Connection.getContract(Connection.getNetwork()));

            //获取银行的需要审计的值和盲签名
            Map bankSum = new ModelMap();
            bankSum = getBankStr(object);
            System.out.println(bankSum);

            //对银行进行审计，并输出到map中
            result.put("AssetsSum",Sum.bankAssetsSum(object,new BigInteger(bankSum.get("comSum").toString()),new BigInteger(bankSum.get("r1").toString()), readBase()));
            result.put("TransactionAverageValue",Sum.bankTransactionAverageValue(object, new BigInteger(bankSum.get("transactionTime").toString()), new BigInteger(bankSum.get("r2").toString()), readBase()).toString());
            result.put("TransactionVariance", Variance.calculateVariance(object, new BigInteger(bankSum.get("doubleComSum").toString()),new BigInteger(bankSum.get("x").toString()), readBase()).toString());
            result.put("TransactionSkewness", Skewness.calaulateSkewness(object, new BigInteger(bankSum.get("tripleComSum").toString()), new BigInteger(bankSum.get("y").toString()), readBase()).toString());
            result.put("TransactionKurtosis", Kurtosis.calculateKurtosis(object, new BigInteger(bankSum.get("quadraComSum").toString()), new BigInteger(bankSum.get("z").toString()), readBase()).toString());

            strRequest = object + result.get("AssetsSum").toString()+"<br>";
            strRequest = strRequest + object + "交易平均值：" + result.get("TransactionAverageValue") + "<br>";
            strRequest = strRequest + object + "交易方差：" + result.get("TransactionVariance") + "<br>";
            strRequest = strRequest + object + "交易偏度：" + result.get("TransactionSkewness") + "<br>";
            strRequest = strRequest + object + "交易峰度：" + result.get("TransactionKurtosis") + "<br>";
//            result.put("AssetsSum","资产和验证通过");
//            result.put("TransactionAverageValue","40000");
//            result.put("TransactionVariance","3000");
//            strRequest = result.get("AssetsSum").toString()+"<br>"+result.get("TransactionAverageValue").toString()+"<br>";
        }

        System.out.println(result);
        System.out.println(strRequest);
        return strRequest;
    }

//    @org.junit.Test
    @RequestMapping("/getChainData")
    public JSONObject getChainData() throws IOException {
        String[] bankNum = {"bank1", "bank2", "bank3", "bank4"};
        Integer[] portNum = {8881, 8882, 8883, 8884};
        for (int i = 0; i < portNum.length; i++) {
            Client.client("2", bankNum[i], "127.0.0.1", portNum[i]);
        }
//
//        //获取交易
        Map bank1Sum = new ModelMap();
        Map bank2Sum = new ModelMap();
        Map bank3Sum = new ModelMap();
        Map bank4Sum = new ModelMap();

        bank1Sum = getBankStr("bank1");
        bank2Sum = getBankStr("bank2");
        bank3Sum = getBankStr("bank3");
        bank4Sum = getBankStr("bank4");

        BigInteger bank1TransactionTime = new BigInteger(bank1Sum.get("transactionTime").toString());
        BigInteger bank2TransactionTime = new BigInteger(bank2Sum.get("transactionTime").toString());
        BigInteger bank3TransactionTime = new BigInteger(bank3Sum.get("transactionTime").toString());
        BigInteger bank4TransactionTime = new BigInteger(bank4Sum.get("transactionTime").toString());


//            BigInteger bank1TransactionTime = ReadBigInteger.readBigInteger("bank1TransactionTime.txt");
//            BigInteger bank2TransactionTime = ReadBigInteger.readBigInteger("bank2TransactionTime.txt");
//            BigInteger bank3TransactionTime = ReadBigInteger.readBigInteger("bank3TransactionTime.txt");
//            BigInteger bank4TransactionTime = ReadBigInteger.readBigInteger("bank4TransactionTime.txt");
        int endId = (bank1TransactionTime.intValue()+bank2TransactionTime.intValue()+bank3TransactionTime.intValue()+bank4TransactionTime.intValue())/2;
//        int endId = 4;
        JSONObject json = new JSONObject();
        json = Query.queryForBrowser(endId,Connection.getContract(Connection.getNetwork()));
        return json;
    }

    @RequestMapping("/getChainState")
    public JSONObject getChainState(@RequestBody Map map) throws IOException {
        System.out.println(map);

        JSONObject json = new JSONObject();
        Map stateMap = new ModelMap();

        int height = 0;
        int height24H = 0;

        //获取当前时间
        LocalDateTime dateTime = LocalDateTime.now();
        String timeFormat = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);
        String nowTime = dateTime.format(formatter);

        //遍历交易
        FileReader file1 = new FileReader("allTransactions.txt");
        BufferedReader br = new BufferedReader(file1);
        String tempInput = br.readLine();
        try{
            while ((tempInput = br.readLine()) != null){
                String time = JSON.parseObject(tempInput).getString("time");
                long secondDiff = dateDiff(time,nowTime,timeFormat);
//                System.out.println(secondDiff);
                //小于24小时
                if (secondDiff<86400){
                    height24H++;
                }
                height++;
            }
            br.close();
            file1.close();
        }catch (IOException e) {
            System.out.println("读取失败！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        json.put("height",height+7);
        json.put("txCounts24H",height24H);
        json.put("txCounts",height);

//        json.put("height", endId);
        return json;
    }

    @RequestMapping("/auditor")
    public Map bankAudit(@RequestBody Map map) throws IOException {
        String bank = map.get("bank").toString();
        Map bankVaule = new ModelMap();

        String[] bankNum = {"bank1", "bank2", "bank3", "bank4"};
        Integer[] portNum = {8881, 8882, 8883, 8884};
        for (int i = 0; i < portNum.length; i++) {
            Client.client("2", bankNum[i], "127.0.0.1", portNum[i]);
        }

        //获取交易
        Map bank1Sum = new ModelMap();
        Map bank2Sum = new ModelMap();
        Map bank3Sum = new ModelMap();
        Map bank4Sum = new ModelMap();

        bank1Sum = getBankStr("bank1");
        bank2Sum = getBankStr("bank2");
        bank3Sum = getBankStr("bank3");
        bank4Sum = getBankStr("bank4");

        BigInteger bank1TransactionTime = new BigInteger(bank1Sum.get("transactionTime").toString());
        BigInteger bank2TransactionTime = new BigInteger(bank2Sum.get("transactionTime").toString());
        BigInteger bank3TransactionTime = new BigInteger(bank3Sum.get("transactionTime").toString());
        BigInteger bank4TransactionTime = new BigInteger(bank4Sum.get("transactionTime").toString());


//            BigInteger bank1TransactionTime = ReadBigInteger.readBigInteger("bank1TransactionTime.txt");
//            BigInteger bank2TransactionTime = ReadBigInteger.readBigInteger("bank2TransactionTime.txt");
//            BigInteger bank3TransactionTime = ReadBigInteger.readBigInteger("bank3TransactionTime.txt");
//            BigInteger bank4TransactionTime = ReadBigInteger.readBigInteger("bank4TransactionTime.txt");
        int endId = (bank1TransactionTime.intValue()+bank2TransactionTime.intValue()+bank3TransactionTime.intValue()+bank4TransactionTime.intValue())/2;
        Query.queryAll(endId, Connection.getContract(Connection.getNetwork()));

        //获取银行的需要审计的值和盲签名
        Map bankSum = new ModelMap();
        bankSum = getBankStr(bank);
        System.out.println(bankSum);
        //对银行进行审计，并输出到map中
        bankVaule.put("AssetsSum",Sum.bankAssetsSum(bank,new BigInteger(bankSum.get("comSum").toString()),new BigInteger(bankSum.get("r1").toString()), readBase()));
        bankVaule.put("TransactionAverageValue",Sum.bankTransactionAverageValue(bank, new BigInteger(bankSum.get("transactionTime").toString()), new BigInteger(bankSum.get("r2").toString()), readBase()).toString());
        bankVaule.put("TransactionVariance", Variance.calculateVariance(bank, new BigInteger(bankSum.get("doubleComSum").toString()),new BigInteger(bankSum.get("x").toString()), readBase()).toString());
        bankVaule.put("TransactionSkewness", Skewness.calaulateSkewness(bank, new BigInteger(bankSum.get("tripleComSum").toString()), new BigInteger(bankSum.get("y").toString()), readBase()).toString());
        bankVaule.put("TransactionKurtosis", Kurtosis.calculateKurtosis(bank, new BigInteger(bankSum.get("quadraComSum").toString()), new BigInteger(bankSum.get("z").toString()), readBase()).toString());
        System.out.println(bankVaule);
        return bankVaule;
    }

    @RequestMapping("/system")
    public Map systemAudit(@RequestBody Map map) throws IOException {
        System.out.println(map);
        String audit = map.get("audit").toString();
        System.out.println(audit);
        Map systemValue = new ModelMap();

        //对四个银行分别进行一次审计
        bankAudit2("bank1");
        bankAudit2("bank2");
        bankAudit2("bank3");
        bankAudit2("bank4");

        //求系统指标
        BigInteger systemSum = new BigInteger("0");
        try {
            systemSum = MarketRate.systemAssetsSum("bank1", "bank2", "bank3", "bank4");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("整个系统的资产和= " + systemSum);
        }
        systemValue.put("SystemAssetsSum",systemSum.toString());

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
        systemValue.put("bank1MarketRate",bank1MarketRate.toString());
        systemValue.put("bank2MarketRate",bank2MarketRate.toString());
        systemValue.put("bank3MarketRate",bank3MarketRate.toString());
        systemValue.put("bank4MarketRate",bank4MarketRate.toString());

        BigDecimal rTSAV = new BigDecimal("0");
        try {
            rTSAV = RealTimeAveragePrice.realTimeAveragePrice("bank1", "bank2", "bank3", "bank4");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("系统的实时平均价格：" + rTSAV);
        }
        systemValue.put("RealTimeTransactionAverageValue",rTSAV);

        BigDecimal hhi = new BigDecimal("0");
        try {
            hhi = HHI.HHIredix();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("系统的HHI指数：" + hhi);
        }
        systemValue.put("HHIIndex",hhi);

        return systemValue;
    }

    public static long dateDiff(String startTime, String endTime, String format) throws Exception {
        //按照传入的格式生成一个simpledateformate对象
        SimpleDateFormat sd = new SimpleDateFormat(format);
        long nd = 1000*24*60*60;//一天的毫秒数
        long nh = 1000*60*60;//一小时的毫秒数
        long nm = 1000*60;//一分钟的毫秒数
        long ns = 1000;//一秒钟的毫秒数
        long diff;
        //获得两个时间的毫秒时间差异
        diff = sd.parse(endTime).getTime() - sd.parse(startTime).getTime();
        long day = diff/nd;//计算差多少天
        long hour = diff/nh;//计算差多少小时
        long min = diff/nm;//计算差多少分钟
        long sec = diff/ns;//计算差多少秒//输出结果
//        System.out.println("时间相差："+day+"天"+hour+"小时"+min+"分钟"+sec+"秒。");
        return sec;
    }

    public void bankAudit2(String bank) throws IOException {
//        String bank = map.get("bank").toString();
        Map bankVaule = new ModelMap();

        String[] bankNum = {"bank1", "bank2", "bank3", "bank4"};
        Integer[] portNum = {8881, 8882, 8883, 8884};
        for (int i = 0; i < portNum.length; i++) {
            Client.client("2", bankNum[i], "127.0.0.1", portNum[i]);
        }
        //获取交易
        Map bank1Sum = new ModelMap();
        Map bank2Sum = new ModelMap();
        Map bank3Sum = new ModelMap();
        Map bank4Sum = new ModelMap();

        bank1Sum = getBankStr("bank1");
        bank2Sum = getBankStr("bank2");
        bank3Sum = getBankStr("bank3");
        bank4Sum = getBankStr("bank4");

        BigInteger bank1TransactionTime = new BigInteger(bank1Sum.get("transactionTime").toString());
        BigInteger bank2TransactionTime = new BigInteger(bank2Sum.get("transactionTime").toString());
        BigInteger bank3TransactionTime = new BigInteger(bank3Sum.get("transactionTime").toString());
        BigInteger bank4TransactionTime = new BigInteger(bank4Sum.get("transactionTime").toString());


//            BigInteger bank1TransactionTime = ReadBigInteger.readBigInteger("bank1TransactionTime.txt");
//            BigInteger bank2TransactionTime = ReadBigInteger.readBigInteger("bank2TransactionTime.txt");
//            BigInteger bank3TransactionTime = ReadBigInteger.readBigInteger("bank3TransactionTime.txt");
//            BigInteger bank4TransactionTime = ReadBigInteger.readBigInteger("bank4TransactionTime.txt");
        int endId = (bank1TransactionTime.intValue()+bank2TransactionTime.intValue()+bank3TransactionTime.intValue()+bank4TransactionTime.intValue())/2;
        Query.queryAll(endId, Connection.getContract(Connection.getNetwork()));

        //获取银行的需要审计的值和盲签名
        Map bankSum = new ModelMap();
        bankSum = getBankStr(bank);

        //对银行进行审计，并输出到map中
        Sum.bankAssetsSum(bank,new BigInteger(bankSum.get("comSum").toString()),new BigInteger(bankSum.get("r1").toString()), readBase());
        Sum.bankTransactionAverageValue(bank, new BigInteger(bankSum.get("transactionTime").toString()), new BigInteger(bankSum.get("r2").toString()), readBase());
        Variance.calculateVariance(bank, new BigInteger(bankSum.get("doubleComSum").toString()),new BigInteger(bankSum.get("x").toString()), readBase());
        Skewness.calaulateSkewness(bank, new BigInteger(bankSum.get("tripleComSum").toString()), new BigInteger(bankSum.get("y").toString()), readBase());
        Kurtosis.calculateKurtosis(bank, new BigInteger(bankSum.get("quadraComSum").toString()), new BigInteger(bankSum.get("z").toString()), readBase());
//        bankVaule.put("AssetsSum",Sum.bankAssetsSum(bank,new BigInteger(bankSum.get("comSum").toString()),new BigInteger(bankSum.get("r1").toString()), readBase()));
//        bankVaule.put("TransactionAverageValue",Sum.bankTransactionAverageValue(bank, new BigInteger(bankSum.get("transactionTime").toString()), new BigInteger(bankSum.get("r2").toString()), readBase()).toString());
//        bankVaule.put("TransactionVariance", Variance.calculateVariance(bank, new BigInteger(bankSum.get("doubleComSum").toString()),new BigInteger(bankSum.get("x").toString()), readBase()).toString());
//        bankVaule.put("TransactionSkewness", Skewness.calaulateSkewness(bank, new BigInteger(bankSum.get("tripleComSum").toString()), new BigInteger(bankSum.get("y").toString()), readBase()).toString());
//        bankVaule.put("TransactionKurtosis", Kurtosis.calculateKurtosis(bank, new BigInteger(bankSum.get("quadraComSum").toString()), new BigInteger(bankSum.get("z").toString()), readBase()).toString());
    }

//@org.junit.Test
    public Map getBankStr(String bank) {
        //获取银行的需要审计的值和盲签名
        String bankStr = ReadString.readBigInteger(bank+"RandomSum.txt");
//        String bank1Str = ReadString.readBigInteger("bank1RandomSum.txt");
//        String bank1Str = ReadString.readBigInteger("bank1RandomSum.txt");
//        String bank1Str = ReadString.readBigInteger("bank1RandomSum.txt");
//        bank1Str = bank1Str.replaceAll("\"","\\\\\"");
        System.out.println(bankStr);
        Map bankMap = new ModelMap();
        String comSum = JSON.parseObject(bankStr).getString("comSum");
        String doubleComSum = JSON.parseObject(bankStr).getString("doubleComSum");
        String quadraComSum = JSON.parseObject(bankStr).getString("quadraComSum");
        String r1 = JSON.parseObject(bankStr).getString("r1");
        String r2 = JSON.parseObject(bankStr).getString("r2");
        String transactionTime = JSON.parseObject(bankStr).getString("transactionTime");
        String tripleComSum = JSON.parseObject(bankStr).getString("tripleComSum");
        String x = JSON.parseObject(bankStr).getString("x");
        String y = JSON.parseObject(bankStr).getString("y");
        String z = JSON.parseObject(bankStr).getString("z");

        bankMap.put("comSum",comSum);
        bankMap.put("doubleComSum",doubleComSum);
        bankMap.put("quadraComSum",quadraComSum);
        bankMap.put("r1",r1);
        bankMap.put("r2",r2);
        bankMap.put("transactionTime",transactionTime);
        bankMap.put("tripleComSum",tripleComSum);
        bankMap.put("x",x);
        bankMap.put("y",y);
        bankMap.put("z",z);

        System.out.println(bankMap);
        return bankMap;
    }

    public static void test(String[] args) throws IOException {
        /**
         * 首先进入的是选择者模式
         */

        Scanner scanner = new Scanner(System.in);
//        Curve c1 = new Curve();
        System.out.println("进入选择者模式：");
        System.out.println("============================");
        while (true) {
//            System.out.println("1. bank4");
            System.out.println("2. auditor");
            System.out.println("3. break");

            String selection = scanner.nextLine();
            if (selection.equals("1")) {
//                CommitGenerateAll.transcation();
            } else if (selection.equals("2")) {
                Selections.auditorSelect();
            } else if (selection.equals("3")){
                FlushFile.flushFile("allTransactions.txt");
                FlushFile.flushFile("oneTransaction.txt");
                FlushFile.flushFile("initializeRandom.txt");
                break;
            } else {
                System.out.println("输入正确的选项参数！");
            }
        }
    }
}
