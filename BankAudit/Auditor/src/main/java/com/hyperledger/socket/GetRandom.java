package com.hyperledger.socket;

import java.math.BigInteger;
import java.util.Random;

public interface GetRandom {
    /**
     * 产生随机数
     */
    static BigInteger getRandom(BigInteger[] arr){
        Random random = new Random();
        BigInteger r = new BigInteger(40,random);
        while(r.compareTo(arr[1].subtract(BigInteger.valueOf(2))) == 1 || r.compareTo(BigInteger.valueOf(2)) == -1){
            r = new BigInteger(40, random);
        }
//        r = BigInteger.valueOf(3);
        return r;
    }
}
