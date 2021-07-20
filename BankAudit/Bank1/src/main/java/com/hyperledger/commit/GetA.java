package com.hyperledger.commit;

import java.math.BigInteger;

public interface GetA {
    /**
     *得到A
     */
    public static BigInteger getA(BigInteger[] g, BigInteger[] h, BigInteger[] al, BigInteger[] ar, BigInteger Alpha, BigInteger[] arr){
        BigInteger A = new BigInteger("1");
        //获取α
//        BigInteger Alpha = GetRandom.getRandom(arr);
//        FileWriter file = null;
//        try {
//            file = new FileWriter("Alpha.txt");
//            file.write(Alpha+"\n");
//            file.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        for (int i = 0; i < al.length; i++){
            A = A.multiply(g[i].modPow(al[i], arr[0])).multiply(h[i].modPow(ar[i], arr[0])).mod(arr[0]);
        }
        A = A.multiply(arr[3].modPow(Alpha, arr[0])).mod(arr[0]);
        return A;
    }
}
