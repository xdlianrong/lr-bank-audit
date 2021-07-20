package com.hyperledger.test;

import com.hyperledger.commit.CommitGenerateAll;
import com.hyperledger.socket.Server;

import java.io.IOException;
import java.util.Scanner;

public class Test {

    public static void main(String[] args) throws IOException {
        /**
         * 首先进入的是选择者模式
         */
        Scanner scanner = new Scanner(System.in);
        Thread t1 = new Thread(new Server());
        t1.start();
//        Curve c1 = new Curve();
        System.out.println("进入选择者模式：");
        System.out.println("============================");
        while (true) {
            System.out.println("1. bank1");
//            System.out.println("2. auditor");
            System.out.println("3. break");

            String selection = scanner.nextLine();
            if (selection.equals("1")) {
                CommitGenerateAll.transcation();
            } else if (selection.equals("2")) {
//                Selections.auditorSelect();
            } else if (selection.equals("3")){
                t1.interrupt();
                FlushFile.flushFile("allTransactions.txt");
                FlushFile.flushFile("oneTransaction.txt");
                FlushFile.flushFile("bank1FileLx.txt");
                FlushFile.flushFile("bank1FileRx.txt");
                FlushFile.flushFile("bank1random.txt");
                FlushFile.flushFile("bank2FileLx.txt");
                FlushFile.flushFile("bank2FileRx.txt");
                FlushFile.flushFile("bank2random.txt");
                FlushFile.flushFile("bank3FileLx.txt");
                FlushFile.flushFile("bank3FileRx.txt");
                FlushFile.flushFile("bank3random.txt");
                FlushFile.flushFile("bank4FileLx.txt");
                FlushFile.flushFile("bank4FileRx.txt");
                FlushFile.flushFile("bank4random.txt");
                FlushFile.flushFile("bankAllRandom.txt");
                AssetsReSet.assetsReset("bank1AssetsRecord.txt", "1000");
                AssetsReSet.assetsReset("bank1TransactionTime.txt", "0");
                AssetsReSet.assetsReset("bank1DoubleComSum.txt","0");
                AssetsReSet.assetsReset("bank1TripleComSum.txt","0");
                AssetsReSet.assetsReset("bank1QuadraComSum.txt","0");
                FlushFile.flushFile("bank1AssetsRecord1.txt");
                System.exit(0);
            } else {
                System.out.println("输入正确的选项参数！");
            }
        }
    }
}
