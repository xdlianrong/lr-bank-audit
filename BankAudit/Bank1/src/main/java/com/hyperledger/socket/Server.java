package com.hyperledger.socket;


import com.hyperledger.commit.Commit;
import com.hyperledger.commit.ReadBigInteger;
import com.hyperledger.rsum.SendAssetsInformation;
import com.hyperledger.rsum.randomSum;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @ Author：lxgxgxgxg
 * @ Date：Created in 9:52 2021/4/29
 * @ Description：给银行的主机服务器，开启一个子线程，让它一直监听其他银行发来的信息
 * @ Version: 1.0
 */
public class Server implements Runnable{

    @Override
    public void run() {
        listen();
    }

    public static void listen(){
        ServerSocket ss = null;
        Socket socket = null;
        InputStream is = null;
        ByteArrayOutputStream bas = null;
        OutputStream os = null;
        while (true){
            try {
                ss = new ServerSocket(8881);
                socket = ss.accept();
                is = socket.getInputStream();

                bas = new ByteArrayOutputStream();
                byte[] buffer = new byte[200];
                int len = 0;
                while((len = is.read(buffer)) != -1){
                    bas.write(buffer, 0, len);
                }
                String str = bas.toString();
                /**
                 * 这个地方要进行字符串的判断，假如是其他银行发来的致盲因子，则直接写入文件，如果是审计员发来的审计请求，
                 * 则发送自己的致盲因子的和给审计员。
                 */
                String str1 = str.substring(0,1);
                if (str1.equals("1")){
                    /**
                     * 其他银行发来的每个承诺的随机数
                     */
                    FileWriter file = new FileWriter("bankAllRandom.txt", true);
                    String str2 = str.substring(1, str.length());
                    file.write(str2+"\n");
                    file.close();
                } else if (str1.equals("2")){
                    /**
                     * 这个判断是审计员的审计请求，发送给审计员
                     */
                    String temp = SendAssetsInformation.send();
                    os = socket.getOutputStream();
                    os.write(("random"+temp.toString()).getBytes());
                } else if (str1.equals("3")){
                    /**
                     * 这里是进行初始化的flag
                     */
                    FileReader file = new FileReader("bank1AssetsRecord.txt");
                    BufferedReader br = new BufferedReader(file);
                    String assetsSum = br.readLine();
                    os = socket.getOutputStream();
                    os.write(("assets"+assetsSum).getBytes());
                } else if (str1.equals("4")){
                    /**
                     * 这里是对面银行发来的转账信息，把发来的金额加到资产和中,并记录交易
                     */
                    BigInteger assetsSum = ReadBigInteger.readBigInteger("bank1AssetsRecord.txt");
                    BigInteger newAssets = new BigInteger(str.substring(6, str.length()));
                    String str2 = str.substring(1,6);
                    FileWriter file1 = new FileWriter("bank1AssetsRecord.txt");
                    file1.write(assetsSum.add(newAssets)+"\n");
                    file1.close();
                    FileWriter file2 = new FileWriter("bank1AssetsRecord1.txt",true);
                    file2.write(str2+" -> "+"bank1:" + newAssets + "\n");
                    file2.close();
                    /**
                     * 因为审计的时候，需要发送参与的次数，所以需要改写参与交易次数的文件
                     */
                    if (newAssets.compareTo(BigInteger.valueOf(0)) == 1){
                        //说明转账的金额是大于0
                        //先读取原值
                        BigInteger transactionTime = ReadBigInteger.readBigInteger("bank1TransactionTime.txt");
//                        System.out.println(transactionTime);
                        BigInteger doubleComSum = ReadBigInteger.readBigInteger("bank1DoubleComSum.txt");
//                        System.out.println(doubleComSum);
                        BigInteger tripleComSum = ReadBigInteger.readBigInteger("bank1TripleComSum.txt");
//                        System.out.println(tripleComSum);
                        BigInteger quadraComSum = ReadBigInteger.readBigInteger("bank1QuadraComSum.txt");
//                        System.out.println(quadraComSum);
                        FileWriter file3 = new FileWriter("bank1TransactionTime.txt");
                        FileWriter file4 = new FileWriter("bank1DoubleComSum.txt");
                        FileWriter file5 = new FileWriter("bank1TripleComSum.txt");
                        FileWriter file6 = new FileWriter("bank1QuadraComSum.txt");
                        file3.write(transactionTime.add(BigInteger.valueOf(1))+"\n");
                        file4.write(doubleComSum.add(newAssets.pow(2))+"\n");
                        file5.write(tripleComSum.add(newAssets.pow(3))+"\n");
                        file6.write(quadraComSum.add(newAssets.pow(4))+"\n");
                        file3.close();
                        file4.close();
                        file5.close();
                        file6.close();
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }finally {

                if ( os != null){
                    try {
                        os.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

                if (bas != null){
                    try {
                        bas.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

                if (is != null){
                    try {
                        is.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

                if (socket != null){
                    try {
                        socket.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }

                if (ss != null){
                    try{
                        ss.close();
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
