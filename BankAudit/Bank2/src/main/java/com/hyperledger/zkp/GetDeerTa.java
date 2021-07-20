package com.hyperledger.zkp;

import com.hyperledger.commit.GetInnerProduct;
import com.hyperledger.commit.GetVarN;

import java.io.FileWriter;
import java.math.BigInteger;

public interface GetDeerTa {
    /**
     * 计算deerta
     */
    public static BigInteger getDeerTa(BigInteger y, BigInteger z, BigInteger[] al, BigInteger[] arr){
        BigInteger deerTa = null;
//        BigInteger z = new BigInteger(Verify.readFile("z.txt"));
//        BigInteger y = new BigInteger(Verify.readFile("y.txt"));
        BigInteger ztemp = z.subtract(z.pow(2)).mod(arr[1]);
//        System.out.println(ztemp);
        //生成y的N次方向量
        BigInteger[] yVector = GetVarN.getVarN(y,al,arr);
        //生成的1的向量
        BigInteger[] oneVector = new BigInteger[al.length];
        for (int i = 0;i<al.length;i++){
            oneVector[i] = BigInteger.valueOf(1);
        }
//        Verify.display(oneVector);
        //生成2的n次方向量
        BigInteger[] twoToVector = GetVarN.getVarN(BigInteger.valueOf(2),al,arr);
//        Verify.display(twoToVector);
        BigInteger left = ztemp.multiply(GetInnerProduct.getInnerProduct(oneVector, yVector,arr)).mod(arr[1]);
//        System.out.println(left);
        BigInteger right = z.pow(3).multiply(GetInnerProduct.getInnerProduct(oneVector, twoToVector,arr)).mod(arr[1]);
//        System.out.println(right);
        deerTa = (left.subtract(right)).mod(arr[1]);
//        System.out.println(deerTa);
        FileWriter file = null;
//        try {
//            file = new FileWriter("deerTa.txt");
//            file.write(deerTa+"\n");
//            file.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return deerTa;

    }
}
