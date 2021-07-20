package com.hyperledger.zkp;

import java.math.BigInteger;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2021/3/5
 * \* Time: 15:36
 * \* To change this template use File | Settings | File Templates.
 * \* Description: 证明指数相等，comarr[16] = g^(1+s)^ * h^r2^ comarr[17] = (g^v^ * h^r^)^(1+s)^，其中1+s有时是0+s
 * \
 */
public interface IndexEqualsProof {
    //指数相等证明
    static boolean indexEqualsProof(BigInteger com1, BigInteger com2, BigInteger com3, BigInteger t, BigInteger s1, BigInteger s2, BigInteger s3, BigInteger[] arr){
        boolean result;
        String str2 = arr[3].toString() + arr[2].toString() + com1.toString()+ t.toString();
        BigInteger c = new BigInteger(HashFunction.getSHA256StrJava(str2)).mod(arr[1]);
//        System.out.println(c);
        BigInteger t1 = arr[3].modPow(s1, arr[0]).multiply(arr[2].modPow(s2, arr[0])).multiply(com1.modPow(s3, arr[0])).multiply((com2.multiply(com3)).modPow(c,arr[0])).mod(arr[0]);
        BigInteger t2 = s1.multiply(BigInteger.valueOf(0)).add(s2.multiply(BigInteger.valueOf(1))).add(s3.multiply(BigInteger.valueOf(-1)));
//        System.out.println("t:"+t);
//        System.out.println("t1:"+t1);
//        System.out.println("t2:"+t2);
        if (t.equals(t1) && t2.equals(BigInteger.valueOf(0))){
            result = true;
        }else{
            result = false;
        }
        return result;
    }
}
