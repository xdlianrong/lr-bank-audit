package com.hyperledger.socket;

import com.hyperledger.commit.ReadFileLxAndRx;

/**
 * @ Author：lxgxgxgxg
 * @ Date：Created in 9:39 2021/4/29
 * @ Description：循环调用sendMessage方法，把随机数循环发送给相应的银行
 * @ Version: 1.0
 */
public class SendMain {

    public static void send(){
        /**
         * 读取三个信息
         */
        String bank1RandomInfor = ReadFileLxAndRx.readFile("bank1random.txt");
        String bank2RandomInfor = ReadFileLxAndRx.readFile("bank2random.txt");
        String bank4RandomInfor = ReadFileLxAndRx.readFile("bank4random.txt");
        //其他三个银行的信息
        String[] str = new String[3];
        str[0] = bank1RandomInfor;
        str[1] = bank2RandomInfor;
        str[2] = bank4RandomInfor;
        //其他三个银行的port
        Integer[] port = new Integer[3];
        port[0] = 8881;
        port[1] = 8882;
        port[2] = 8884;
        for (int i = 0; i < port.length; i++) {
            SendMessage.sendMesssage("1", str[i], "127.0.0.1", port[i]);
        }

    }
}
