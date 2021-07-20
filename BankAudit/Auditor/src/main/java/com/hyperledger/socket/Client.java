package com.hyperledger.socket;

import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @ Author：lxgxgxgxg
 * @ Date：Created in 16:21 2021/4/29
 * @ Description：这里主要是审计员和银行进行交互，得到相应的致盲因子和的信息
 * @ Version: 1.0
 */
public class Client {

    public static void client(String flag, String str, String IP, Integer port){
        try {
            InetAddress inet = InetAddress.getByName(IP);
            Socket socket = new Socket(inet, port);
            /**
             * 审计员发送审计请求
             */
            OutputStream os = socket.getOutputStream();
            os.write(flag.getBytes());
            socket.shutdownOutput();
            /**
             * 审计员接受银行返回的信息
             */
            InputStream is = socket.getInputStream();
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            byte[] buffer = new byte[200];
            int len = 0;
            while ((len = is.read(buffer)) != -1){
                bas.write(buffer, 0, len);
            }

            String infor = bas.toString();

            String str1 = infor.substring(0,6);
//            System.out.println(str1);
//            System.out.println(infor.substring(6,infor.length()));
            if (str1.equals("random")){
                /**
                 * 将结果写入文件
                 */
                FileWriter file = new FileWriter(str+"RandomSum.txt");
                file.write(infor.substring(6, infor.length())+"\n");
                file.close();
            }else {
                FileWriter file = new FileWriter(str+"InitializeAssets.txt");
                file.write(infor.substring(6,infor.length())+"\n");
                file.close();
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
