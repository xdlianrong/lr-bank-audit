package com.hyperledger.socket;

import com.hyperledger.connection.ReadBase;

import java.io.IOException;

/**
 * @ Author：lxgxgxgxg
 * @ Date：Created in 18:11 2021/5/10
 * @ Description：
 * @ Version: 1.0
 */
public class InitializeAssets {
    public static void initializeAssets(){
        /**
         * 审计员和银行进行交互，进行资产的初始化
         */
        String[] bankNum = {"bank1", "bank2", "bank3", "bank4"};
        Integer[] portNum = {8881, 8882, 8883, 8884};
        for (int i = 0; i < portNum.length; i++) {
            //flag=3就是进行初始化
            Client.client("3", bankNum[i], "127.0.0.1", portNum[i]);
        }
        try {
            Commit.commitAll(ReadBase.readBase());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
