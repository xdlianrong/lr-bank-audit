package com.hyperledger.commit;

import java.math.BigInteger;

public interface GetMiu {
    /**
     * 得到μ
     */
    public static BigInteger getMiu(BigInteger alpha, BigInteger rho, BigInteger x, BigInteger[] arr){
//        BigInteger alpha = new BigInteger(Verify.readFile("alpha.txt"));
//        BigInteger rho = new BigInteger(Verify.readFile("rho.txt"));
//        BigInteger x = new BigInteger(Verify.readFile("x.txt"));

        BigInteger miu = (rho.multiply(x).add(alpha)).mod(arr[1]);
//        try {
//            FileWriter file3 = new FileWriter("miu.txt");
//            file3.write(miu+"\n");
//            file3.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(miu);
        return miu;
    }
}
