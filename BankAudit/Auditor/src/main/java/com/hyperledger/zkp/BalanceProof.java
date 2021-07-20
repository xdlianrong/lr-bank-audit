package com.hyperledger.zkp;

import java.math.BigInteger;

public interface BalanceProof {
    /**
     * 比较两个Pedersen承诺的隐藏值是相等的
     */
    static boolean EqualityOfMessageInPedersenCoomiment(BigInteger com1, BigInteger com2, BigInteger c, BigInteger d,
                                             BigInteger d1, BigInteger d2, BigInteger[] arr){
        boolean result;
        /**
         * 平衡证明
         */
        BigInteger temp1 = (arr[2].modPow(d, arr[0]).multiply(arr[3].modPow(d1, arr[0])).multiply(com1.modPow(BigInteger.valueOf(0).subtract(c), arr[0]))).mod(arr[0]);
        BigInteger temp2 = (arr[2].modPow(d, arr[0]).multiply(arr[3].modPow(d2, arr[0])).multiply(com2.modPow(BigInteger.valueOf(0).subtract(c), arr[0]))).mod(arr[0]);
        String str = temp1.toString() + temp2.toString();
        String hashresult = new BigInteger(HashFunction.getSHA256StrJava(str)).mod(arr[1]).toString();
        BigInteger hashresult1 = new BigInteger(hashresult);
//        System.out.println("hash:" + hashresult1);
        if(hashresult1.equals(c)){
            result = true;
        }else{
            result = false;
        }
        return result;
    }


    /**
     * 资产只转给了一个人
     */
    //1、首先验证的是g的s次方
    static boolean SigmaProtocols(BigInteger temp1, BigInteger c, BigInteger s, BigInteger[] arr){
        boolean result;
        BigInteger t = arr[2].modPow(s, arr[0]).multiply(temp1.modPow(BigInteger.valueOf(0).subtract(c), arr[0])).mod(arr[0]);
        String str = new BigInteger(HashFunction.getSHA256StrJava(arr[2].toString() + temp1.toString() + t.toString())).mod(arr[1]).toString();
//        System.out.println(c);
//        System.out.println(str);
        if(c.toString().equals(str)){
            result = true;
        }else {
            result = false;
        }
        return result;
    }

    //2、再次验证的是comarr[17]是comarr[0]的1/0+s次方
    static boolean com1Or0AddsProtocols(BigInteger com, BigInteger comTemp1, BigInteger comTemp1C, BigInteger comTemp1S, BigInteger[] arr){
        boolean result;
        BigInteger t = com.modPow(comTemp1S, arr[0]).multiply(comTemp1.modPow(BigInteger.valueOf(0).subtract(comTemp1C), arr[0])).mod(arr[0]);
        String str = new BigInteger(HashFunction.getSHA256StrJava(com.toString() + comTemp1.toString() + t.toString())).mod(arr[1]).toString();
//        System.out.println(comTemp1C);
//        System.out.println(str);
        if(comTemp1C.toString().equals(str)){
            result = true;
        }else {
            result = false;
        }
        return result;
    }
}
