package com.hyperledger.commit;

import java.math.BigInteger;

public interface GetInnerProduct {
    /**
     * 得到向量的内积
     */
    static BigInteger getInnerProduct(BigInteger[] v1, BigInteger[] v2, BigInteger[] arr){
        BigInteger innerProduct = new BigInteger("0");
        for (int i = 0;i<v1.length;i++){
            innerProduct = (innerProduct.add(v1[i].multiply(v2[i]))).mod(arr[1]);
        }
        return innerProduct;
    }
}
