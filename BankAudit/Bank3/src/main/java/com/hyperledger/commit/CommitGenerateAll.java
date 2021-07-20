package com.hyperledger.commit;

import com.hyperledger.connection.Connection;
import com.hyperledger.connection.Query;
import com.hyperledger.zkp.ZKPProof;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Scanner;

public class CommitGenerateAll {
    //这个函数就是进入选择模式
    public static void transcation() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.println("1. 转账交易");
            System.out.println("2. 获取数据");
            System.out.println("3. 零知识证明");
            System.out.println("4. 退    出");
            String selection1 = scanner.nextLine();
            if(selection1.equals("1")){
                System.out.println("============================");
                System.out.println("输入转账交易的接受者：");
                System.out.println("1. bank1");
                System.out.println("2. bank2");
                System.out.println("4. bank4");
                String str = scanner.nextLine();
                if(str.equals("1")) {
                    System.out.println("输入转账的金额：");
                    String mInput = scanner.nextLine();
                    BigInteger m;
                    try {
                        m = new BigInteger(mInput);
                    } catch (NumberFormatException e) {
                        System.out.println("输入金额类型错误！");
                        break;
                    }
                    if(m.compareTo(BigInteger.valueOf(0)) == 1){
                        Commit.commitAll("bank1", m, Commit.readBase());
                    }else{
                        System.out.println("输入的金额有误！");
                    }
                } else if (str.equals("2")) {
                    System.out.println("输入转账的金额：");
                    String mInput = scanner.nextLine();
                    BigInteger m;
                    try{
                        m = new BigInteger(mInput);
                    } catch (NumberFormatException e){
                        System.out.println("输入金额类型错误！");
                        break;
                    }
                    if(m.compareTo(BigInteger.valueOf(0)) == 1){
                        Commit.commitAll("bank2", m, Commit.readBase());
                    }else{
                        System.out.println("输入的金额有误！");
                    }
                } else if (str.equals("4")){
                    System.out.println("输入转账的金额：");
                    String mInput = scanner.nextLine();
                    BigInteger m;
                    try {
                        m = new BigInteger(mInput);
                    } catch (NumberFormatException e) {
                        System.out.println("输入金额类型错误！");
                        break;
                    }
                    if(m.compareTo(BigInteger.valueOf(0)) == 1){
                        Commit.commitAll("bank4", m, Commit.readBase());
                    }else{
                        System.out.println("输入的金额有误！");
                    }
                }else{
                    System.out.println("输入的银行不存在！");
                }
            } else if (selection1.equals("2")) {
                System.out.println("请输入读取交易的ID：");
                int i = scanner.nextInt();
                Query.query(String.valueOf(i), Connection.getContract(Connection.getNetwork()));
            } else if (selection1.equals("3")){
                ZKPProof.ReadFieldAndZKP(Commit.readBase());
            }else if (selection1.equals("4")){
                break;
            }
        }
    }
}
