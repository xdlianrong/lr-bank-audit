package com.hyperledger.commit;

import java.math.BigInteger;

public interface GetAl {
    /**
     * 得到一个数的二进制序列，也就是Al
     */
    static BigInteger[] getAl(BigInteger x){
        String a1 = new BigInteger(x.toString()).toString(2);
        String a2 = reverse(a1);
        int a2L = a2.length();
        BigInteger[] arr = new BigInteger[94];

        for (int i =0;i<arr.length;i++){
            arr[i] = BigInteger.valueOf(0);
        }
//        Verify.display(arr);
        for(int i = 0; i<a2L;i++){
            arr[i] = new BigInteger(a2.charAt(i)+"");
//			System.out.print(arr[i]);
        }
        return arr;
    }

    //字符串反转
    static String reverse(String str) {
        char[] chars = str.toCharArray();
        String reverse = "";
        for (int i = chars.length - 1; i >= 0; i--) {
            reverse += chars[i];
        }
        return reverse;
    }
}
