package com.hyperledger.connection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hyperledger.test.FlushFile;
import com.hyperledger.zkp.ZKPProof;
import org.hyperledger.fabric.gateway.Contract;
import org.springframework.ui.ModelMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2021/3/19
 * \* Time: 19:14
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public class Query {
    public static void query(String str, Contract contract){
        //查询现有资产
        //注意更换调用链码的具体函数
        byte[] queryAllAssets = new byte[0];
        try {
            queryAllAssets = contract.evaluateTransaction("get",str);
//            System.out.println("所有资产："+new String(queryAllAssets, StandardCharsets.UTF_8));
            String result = new String(queryAllAssets, StandardCharsets.UTF_8);
            result = result.replaceAll("\\\\","");
            FileWriter file = new FileWriter("oneTransaction.txt");
            file.write(result+"\n");
            file.close();
            System.out.println("交易信息已成功获取！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void queryAll(int endId, Contract contract) throws IOException {
//        byte[] queryAllAssets = new byte[0];
        FlushFile.flushFile("allTransactions.txt");
        FileWriter file = new FileWriter("allTransactions.txt",true);

        FileReader file1 = new FileReader("initTransaction.txt");
        BufferedReader br = new BufferedReader(file1);
        String tempInput = br.readLine();
        file.write(tempInput+"\n");
        file.close();
        System.out.println("end"+endId);
        for (int id=1;id<=endId;id++) {
            System.out.println(id);
            query(String.valueOf(id),contract);
            ZKPProof.ReadFieldAndZKP(ReadBase.readBase());
        }

    }

    public static JSONObject queryForBrowser(int endId, Contract contract) throws IOException {
        JSONObject json = new JSONObject();
        json.put("code", 0);
        json.put("msg", "");
        json.put("count", endId);

        Map[] mapArray = new ModelMap[endId];

        Query.queryAll(endId, Connection.getContract(Connection.getNetwork()));
        FileReader file1 = new FileReader("allTransactions.txt");
        BufferedReader br = new BufferedReader(file1);
        String tempInput = br.readLine();
        int index = 1;
        try{
            while ((tempInput = br.readLine()) != null){
                Map bankMap = new ModelMap();
                JSONObject jsonbank1 = new JSONObject();
                JSONObject jsonbank2 = new JSONObject();
                JSONObject jsonbank3 = new JSONObject();
                JSONObject jsonbank4 = new JSONObject();
                String height = String.valueOf(index);
                String time = JSON.parseObject(tempInput).getString("time");
                //读进来的字符串转换去掉前面的时间字段
                String str1 = tempInput.replaceFirst("\\{\"time\":\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\",", "{");
                System.out.println("str1:"+str1);
                JSONObject jsonObject = JSON.parseObject(str1);
                jsonbank1 = jsonObject.getJSONObject("bank1");
                jsonbank2 = jsonObject.getJSONObject("bank2");
                jsonbank3 = jsonObject.getJSONObject("bank3");
                jsonbank4 = jsonObject.getJSONObject("bank4");
                System.out.println(jsonbank1);
                String bank1bitcom = jsonbank1.getString("bitCom");
                String bank2bitcom = jsonbank2.getString("bitCom");
                String bank3bitcom = jsonbank3.getString("bitCom");
                String bank4bitcom = jsonbank4.getString("bitCom");

                bankMap.put("height",height);
                bankMap.put("timestamp",time);
                bankMap.put("bank1bitcom",bank1bitcom);
                bankMap.put("bank2bitcom",bank2bitcom);
                bankMap.put("bank3bitcom",bank3bitcom);
                bankMap.put("bank4bitcom",bank4bitcom);

                mapArray[index-1] = bankMap;
                index++;
            }
            br.close();
            file1.close();
        }catch (IOException e) {
            System.out.println("读取失败！");
        }
        json.put("data",JSON.toJSON(mapArray));
        return json;
    }
}
