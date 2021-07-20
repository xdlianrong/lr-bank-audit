package com.hyperledger.commit;

import java.math.BigInteger;

public interface GetTaox {
    /**
     * 求taox
     */
    static BigInteger getTaox(BigInteger x, BigInteger tao1, BigInteger tao2, BigInteger z, BigInteger r, BigInteger[] arr){
//        BigInteger x = new BigInteger(Verify.readFile("x.txt"));
//        BigInteger tao1 = new BigInteger(Verify.readFile("tao1.txt"));
//        BigInteger tao2 = new BigInteger(Verify.readFile("tao2.txt"));
//        BigInteger z = new BigInteger(Verify.readFile("z.txt"));
//        BigInteger r = new BigInteger(Verify.readFile("random.txt"));

        BigInteger taox = (tao2.multiply(x.pow(2)).add(tao1.multiply(x)).add(z.pow(2).multiply(r))).mod(arr[1]);
//        try {
//            FileWriter file5 = new FileWriter("taox.txt");
//            file5.write(taox+"\n");
//            file5.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(taox);
        return taox;
    }
}
