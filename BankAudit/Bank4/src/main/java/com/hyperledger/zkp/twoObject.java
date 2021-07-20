package com.hyperledger.zkp;

import java.math.BigInteger;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2021/3/4
 * \* Time: 11:41
 * \* To change this template use File | Settings | File Templates.
 * \* Description:  一条交易只有两个对象参与
 * \
 */
public interface twoObject {
    //获取每一个对象的bitCom，相乘之后，与新的承诺进行比较
    static boolean twoObjectCompare(BigInteger bitCom1, BigInteger bitCom2, BigInteger bitCom3, BigInteger bitCom4, BigInteger bankNum,
                                           BigInteger r2Sum, BigInteger[] arr){
        boolean result;
        BigInteger temp1 = bitCom1.multiply(bitCom2).multiply(bitCom3).multiply(bitCom4).mod(arr[0]);
        BigInteger temp2 = arr[2].modPow(bankNum, arr[0]).multiply(arr[3].modPow(r2Sum, arr[0])).mod(arr[0]);
        if(temp1.equals(temp2)){
            result = true;
        }else{
            result = false;
        }
        return result;
    }
}
