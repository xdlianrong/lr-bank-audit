package com.hyperledger.commit;

import java.math.BigInteger;

public interface GetAr {
    /**
     * 得到Ar
     */
    static BigInteger[] getAr(BigInteger[] al, BigInteger[] arr){
        BigInteger[] Ar = new BigInteger[al.length];
        for(int i = 0; i< al.length;i++){
//            if (al[i].compareTo(BigInteger.valueOf(1)) == 0){
//                Ar[i] = BigInteger.valueOf(0);
//            }else {
//                Ar[i] = arr[1].subtract(BigInteger.valueOf(1));
//            }
            Ar[i] = (al[i].subtract(BigInteger.valueOf(1))).mod(arr[1]);
        }
        return Ar;
    }
}
