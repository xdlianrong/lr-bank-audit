package com.hyperledger.zkp;

import java.math.BigInteger;

public interface GetVarN {
    /**
     *递乘的向量
     */
    public static BigInteger[] getVarN(BigInteger x, BigInteger[] al, BigInteger[] arr){
        BigInteger[] Vector = new BigInteger[al.length];
        Vector[0] = new BigInteger("1");
        for (int i = 1;i<al.length;i++){
            Vector[i] = Vector[i-1].multiply(x).mod(arr[1]);
        }
        return Vector;
    }
}
