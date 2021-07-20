package com.hyperledger.zkp;

import java.math.BigInteger;

public interface RangeProof {
    /**
     * 最后的验证阶段
     */
    static boolean isTresult(BigInteger z, BigInteger x, BigInteger t, BigInteger V, BigInteger taox, BigInteger T1, BigInteger T2, BigInteger deerTa, BigInteger[] arr){
        //首先读取t
//        BigInteger t = new BigInteger(readFile("t.txt"));
//        BigInteger V = new BigInteger(readFile("com.txt"));
//        BigInteger taox = new BigInteger(readFile("taox.txt"));
//        System.out.println("taox:"+taox);
//        BigInteger z =  new BigInteger(readFile("z.txt"));
//        BigInteger x = new BigInteger(readFile("x.txt"));
//        BigInteger T1 = new BigInteger(readFile("T1.txt"));
//        BigInteger T2 = new BigInteger(readFile("T2.txt"));
//        BigInteger deerTa = new BigInteger(readFile("deerTa.txt"));
        BigInteger left = arr[2].modPow(t, arr[0]).multiply(arr[3].modPow(taox, arr[0])).mod(arr[0]);
//        System.out.println("left:"+left);
        BigInteger right = V.modPow(z.pow(2), arr[0]).multiply(arr[2].modPow(deerTa, arr[0])).multiply(T1.modPow(x, arr[0])).multiply(T2.modPow(x.pow(2), arr[0])).mod(arr[0]);
//        System.out.println("right:"+right);
        if (left.equals(right)){
//            System.out.println("Tyes");
            return true;
        }else {
//            System.out.println("Tno");
            return false;
        }
    }

    /**
     * 验证P
     */
    static boolean verifierP(BigInteger A,BigInteger S,BigInteger[] g,BigInteger[] h, BigInteger[] lx,
                                    BigInteger[] rx, BigInteger y, BigInteger z, BigInteger x, BigInteger miu, BigInteger[] arr){
//        BigInteger z =  new BigInteger(readFile("z.txt"));
//        BigInteger x = new BigInteger(readFile("x.txt"));
//        BigInteger y = new BigInteger(readFile("y.txt"));
//        BigInteger miu = new BigInteger(readFile("miu.txt"));

        BigInteger newS = S.modPow(x,arr[0]);
//        BigInteger[] newG = new BigInteger[g.length];
//        for (int i = 0;i<g.length;i++){
//            newG[i] = g[i].modPow(BigInteger.valueOf(0).subtract(z), arr[0]);
//        }

        BigInteger[] h_index = new BigInteger[g.length];
        BigInteger[] yVector = GetVarN.getVarN(y, g, arr);
//        display(yVector);
        BigInteger[] left = new BigInteger[g.length];
        for (int i = 0;i<g.length;i++){
            left[i] = yVector[i].multiply(z).mod(arr[1]);
        }
//        display(left);
        BigInteger[] twoN = GetVarN.getVarN(BigInteger.valueOf(2), g, arr);
        BigInteger[] right = new BigInteger[g.length];
        for (int i = 0;i<g.length;i++){
            right[i] = (z.pow(2).multiply(twoN[i])).mod(arr[1]);
            h_index[i] = (left[i].add(right[i])).mod(arr[1]);
        }
//        display(right);
//        display(h_index);
        //求最后的h的向量
//        BigInteger[] newH = new BigInteger[g.length];
//        for (int i = 0; i<g.length;i++){
//            newH[i] = h[i].modPow(h_index[i], arr[0]);
//        }

        //得到后两个gh向量的和
        BigInteger g_newHInnerProduct = new BigInteger("1");
        for (int i = 0;i<g.length;i++){
            g_newHInnerProduct = g_newHInnerProduct.multiply(g[i].modPow(BigInteger.valueOf(0).subtract(z),arr[0]).multiply(h[i].modPow(h_index[i], arr[0]))).mod(arr[0]);
        }
        BigInteger leftP = A.multiply(newS).multiply(g_newHInnerProduct).mod(arr[0]);
//        System.out.println(leftP);

        BigInteger rightP = new BigInteger("1");
        for (int i = 0;i<g.length;i++){
            rightP = rightP.multiply(g[i].modPow(lx[i],arr[0]).multiply(h[i].modPow(rx[i], arr[0]))).mod(arr[0]);
        }
        rightP = rightP.multiply(arr[3].modPow(miu, arr[0])).mod(arr[0]);
//        System.out.println(rightP);

        /**
         * 加入三个hash的比较
         */
        BigInteger newYHash = new BigInteger(HashFunction.getSHA256StrJava(A.toString() + S.toString())).mod(arr[1]);
        BigInteger newZHash = new BigInteger(HashFunction.getSHA256StrJava(A.toString() + S.toString() + y.toString())).mod(arr[1]);
        BigInteger newXHash = new BigInteger(HashFunction.getSHA256StrJava(A.toString() + S.toString() + y.toString() + z.toString())).mod(arr[1]);


        if (leftP.equals(rightP) && y.equals(newYHash) && z.equals(newZHash) && x.equals(newXHash)){
//            System.out.println("Pyes");
            return true;
        }else {
//            System.out.println("Pno");
            return false;
        }
    }

    /**
     * 验证lx,rx,t
     */
    static boolean verLx_Rx_T(BigInteger[] lx, BigInteger[] rx, BigInteger t, BigInteger[] arr){
        BigInteger innerProduct = GetInnerProduct.getInnerProduct(lx,rx,arr);
//        System.out.println(innerProduct);
//        System.out.println(t);
        if (t.equals(innerProduct)){
//            System.out.println("Vectoryes");
            return true;
        }else {
//            System.out.println("VectorTno");
            return false;
        }
    }
}
