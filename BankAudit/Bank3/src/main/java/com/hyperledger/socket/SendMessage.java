package com.hyperledger.socket;

import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @ Author：lxgxgxgxg
 * @ Date：Created in 9:27 2021/4/29
 * @ Description：把产生一条交易各个银行的承诺的随机数发送给各自的银行
 * @ Version: 1.0
 */
public interface SendMessage {

    /**
     * 银行1产生交易之后，发送承诺随机数给其他银行的函数
     * @param infor 发送的信息
     * @param IP    目标银行服务器的ip
     * @param port  目标银行服务器的port
     */
    public static void sendMesssage(String flag, String infor, String IP, Integer port){
        try {
            InetAddress inet = InetAddress.getByName("127.0.0.1");
            Socket socket = new Socket(inet, port);
            OutputStream os = socket.getOutputStream();
            os.write((flag+infor).getBytes());

            os.close();
            socket.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
