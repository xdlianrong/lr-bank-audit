package com.hyperledger.zkp;

import java.math.BigInteger;

public interface HVectorConvert {
    /**
     * 需要转换h的向量，在验证的时候
     */
    public static BigInteger[] hVectorConvert(BigInteger[] h, BigInteger y, BigInteger[] arr){
        BigInteger[] newH = new BigInteger[h.length];
//        BigInteger y = new BigInteger(Verify.readFile("y.txt"));
        BigInteger[] yVector = new BigInteger[h.length];
//        System.out.println(y);
        BigInteger index = new BigInteger("1");
        for (int i = 0; i < h.length;i++){
            yVector[i] = y.modPow(BigInteger.valueOf(0).subtract(index).add(BigInteger.valueOf(1)), arr[1]);
//            System.out.println(index);
            index = index.add(BigInteger.valueOf(1));

        }
//        Verify.display(yVector);

        for (int i = 0;i<h.length;i++){
            newH[i] = h[i].modPow(yVector[i], arr[0]);
        }
        return newH;
    }
}
