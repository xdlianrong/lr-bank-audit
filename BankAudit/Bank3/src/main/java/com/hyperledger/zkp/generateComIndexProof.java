package com.hyperledger.zkp;

import java.math.BigInteger;

public interface generateComIndexProof {
    /**
     * 实现doubleCom的隐藏值是com隐藏值的平方的证明
     */
    static boolean generateComIndexProof(BigInteger doubleCom, BigInteger comSAddVIndex, BigInteger comSIndex,
                                                  BigInteger hBaseIndex, BigInteger hash_h, BigInteger h_Response, BigInteger[] arr){
        boolean result = false;
        //计算h的表达
        BigInteger newHBaseIndex = comSAddVIndex.multiply(doubleCom.modPow(BigInteger.valueOf(-1), arr[0])).multiply(comSIndex.modPow(BigInteger.valueOf(-1), arr[0])).mod(arr[0]);
//        System.out.println(hBaseIndex);
//        System.out.println(newHBaseIndex);
        //计算新的hash
        BigInteger t = arr[3].modPow(h_Response, arr[0]).multiply(hBaseIndex.modPow(BigInteger.valueOf(0).subtract(hash_h), arr[0])).mod(arr[0]);
        BigInteger newHash_h = new BigInteger(HashFunction.getSHA256StrJava(arr[3].toString() + newHBaseIndex.toString() + t.toString())).mod(arr[1]);
//        System.out.println(hash_h);
//        System.out.println(newHash_h);
        if (hBaseIndex.equals(newHBaseIndex) && hash_h.equals(newHash_h)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }
}
