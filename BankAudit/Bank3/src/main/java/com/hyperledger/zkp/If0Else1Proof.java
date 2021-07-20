package com.hyperledger.zkp;

import java.math.BigInteger;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2021/3/3
 * \* Time: 18:25
 * \* To change this template use File | Settings | File Templates.
 * \* Description:  证明银行的一笔交易中比特承诺的隐藏值非0即1
 * \
 */
public interface If0Else1Proof {
    //证明非0即1
    static boolean bit01Proof(BigInteger C, BigInteger C1, BigInteger C2, BigInteger x, BigInteger hash_x, BigInteger f, BigInteger Z1, BigInteger Z2, BigInteger[] arr){
        boolean result;
        BigInteger temp1 = C.modPow(x, arr[0]).multiply(C1).mod(arr[0]);
//        System.out.println("temp1="+temp1);
        BigInteger temp2 = (arr[2].modPow(f, arr[0]).multiply(arr[3].modPow(Z1, arr[0]))).mod(arr[0]);
//        System.out.println("temp2="+temp2);

        BigInteger temp3 = C.modPow(x.subtract(f), arr[0]).multiply(C2).mod(arr[0]);
//        System.out.println("temp3="+temp3);
        BigInteger temp4 = (arr[2].modPow(BigInteger.valueOf(0), arr[0]).multiply(arr[3].modPow(Z2, arr[0]))).mod(arr[0]);
//        System.out.println("temp4="+temp4);

        BigInteger new_hash_x = new BigInteger(HashFunction.getSHA256StrJava(x.toString())).mod(arr[1]);
//        System.out.println("hash      =" + hash_x);
//        System.out.println("new_hash_x="+new_hash_x);

        if(temp1.equals(temp2) && temp3.equals(temp4) && hash_x.equals(new_hash_x)){
            result = true;
        }else{
            result = false;
        }

        return result;
    }
}
