package com.hyperledger.commit;

import java.math.BigInteger;

public interface Get_SlAnd_SrAnd_S {
    /**
     * 得到Sr
     */
    static BigInteger[] getSr(BigInteger[] al, BigInteger[] arr){
        //得到Sr
        BigInteger[] Sr = new BigInteger[al.length];
        for (int i = 0; i<al.length; i++){
            Sr[i] = GetRandom.getRandom(arr);
        }
//        System.out.println("sr:"+Sr[1]);
        return Sr;
    }
    /**
     * 得到Sl
     */
    static BigInteger[] getSl(BigInteger[] al, BigInteger[] arr){
        //得到Sl
        BigInteger[] Sl = new BigInteger[al.length];
        for (int i = 0; i<al.length; i++){
            Sl[i] = GetRandom.getRandom(arr);
//            System.out.println("Sl["+i+"]:" + Sl[i]);
        }
        return Sl;
    }

    /**
     * 得到S
     */
    static BigInteger getS(BigInteger[] g, BigInteger[] h,BigInteger[] Sl, BigInteger[] Sr, BigInteger Rho, BigInteger[] arr){
        BigInteger S = new BigInteger("1");
        //获取随机数ρ
//        BigInteger Rho = Verify.getRandom(arr);
//        FileWriter file = null;
//        try {
//            file = new FileWriter("Rho.txt");
//            file.write(Rho+"\n");
//            file.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        for (int i = 0;i<g.length;i++){
            S = S.multiply(g[i].modPow(Sl[i], arr[0])).multiply(h[i].modPow(Sr[i], arr[0])).mod(arr[0]);
        }
        S = S.multiply(arr[3].modPow(Rho, arr[0])).mod(arr[0]);
        return S;
    }
}
