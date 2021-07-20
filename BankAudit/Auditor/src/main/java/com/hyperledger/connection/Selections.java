package com.hyperledger.connection;

import com.hyperledger.audit.AuditFunction;
import com.hyperledger.socket.InitializeAssets;
import com.hyperledger.zkp.ZKPProof;

import java.io.IOException;
import java.util.Scanner;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2021/1/20
 * \* Time: 14:50
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public class Selections {
    /**
     * 审计员进入系统之后选择模式，得先从账本上去获取数据，之后进行资产审计
     */
    public static void auditorSelect() throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("资产审计员选择模式：");
        while(true){
            System.out.println("============================");
            System.out.println("请输入模式：");
            System.out.println("1. 资产审计");
            System.out.println("2. 获取数据");
            System.out.println("3. 初始化账本");
            System.out.println("4. 退出审计");
            String str = sc.nextLine();
            if(str.equals("1")){
                AuditFunction.auditLedger();
            } else if (str.equals("2")){
                while(true){
                    System.out.println("1. 获取一条交易:");
//                    System.out.println("2. 获取多条交易:");
                    System.out.println("3. ZKP最新交易:");
                    System.out.println("4、退出");
                    String str2 = sc.nextLine();
                    if (str2.equals("1")) {
                        System.out.println("请输入读取交易的ID：");
                        int i = sc.nextInt();
                        Query.query(String.valueOf(i), Connection.getContract(Connection.getNetwork()));
                    } else if (str2.equals("2")) {
                        System.out.println("请输入查询交易起始ID和终止ID：");
                        //int startId = sc.nextInt();
                        int endId = sc.nextInt();
//                        Query.queryAll(endId, Connection.getContract(Connection.getNetwork()));
                    } else if (str2.equals("3")){
                        ZKPProof.ReadFieldAndZKP(ReadBase.readBase());
                    } else {
                        break;
                    }
                }
            } else if (str.equals("3")) {
                InitializeAssets.initializeAssets();
            }else {
                break;
            }
        }
    }

}
