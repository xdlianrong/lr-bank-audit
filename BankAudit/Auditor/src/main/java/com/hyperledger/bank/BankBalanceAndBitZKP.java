package com.hyperledger.bank;

import java.math.BigInteger;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2021/2/26
 * \* Time: 17:27
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public class BankBalanceAndBitZKP {
    //此类的字段包含的是银行资产平衡证明和01比特证明生成的字段
    private BigInteger c1;              //验证交易金额生成的哈希
    private BigInteger D;               //第一个参数
    private BigInteger D1;
    private BigInteger D2;
    private BigInteger c2;              //验证bit承诺生成表的哈希
    private BigInteger M;
    private BigInteger M1;
    private BigInteger M2;
    private BigInteger twoObject;        //一笔交易当中，参与者只有两人
    private BigInteger allR2_Sum;        //所有bitCom的和
    private BigInteger h2;              //只转给一个人验证的hash
    private BigInteger N;
    private BigInteger N1;
    private BigInteger N2;

    public BankBalanceAndBitZKP() {
    }

    public BankBalanceAndBitZKP(BigInteger c1, BigInteger d, BigInteger d1, BigInteger d2, BigInteger c2, BigInteger m, BigInteger m1, BigInteger m2,
                                BigInteger twoObject, BigInteger allR2_Sum, BigInteger h2, BigInteger n, BigInteger n1, BigInteger n2) {
        this.c1 = c1;
        D = d;
        D1 = d1;
        D2 = d2;
        this.c2 = c2;
        M = m;
        M1 = m1;
        M2 = m2;
        this.twoObject = twoObject;
        this.allR2_Sum = allR2_Sum;
        this.h2 = h2;
        N = n;
        N1 = n1;
        N2 = n2;
    }

    public BigInteger getC1() {
        return c1;
    }

    public void setC1(BigInteger c1) {
        this.c1 = c1;
    }

    public BigInteger getD() {
        return D;
    }

    public void setD(BigInteger d) {
        D = d;
    }

    public BigInteger getD1() {
        return D1;
    }

    public void setD1(BigInteger d1) {
        D1 = d1;
    }

    public BigInteger getD2() {
        return D2;
    }

    public void setD2(BigInteger d2) {
        D2 = d2;
    }

    public BigInteger getC2() {
        return c2;
    }

    public void setC2(BigInteger c2) {
        this.c2 = c2;
    }

    public BigInteger getM() {
        return M;
    }

    public void setM(BigInteger m) {
        M = m;
    }

    public BigInteger getM1() {
        return M1;
    }

    public void setM1(BigInteger m1) {
        M1 = m1;
    }

    public BigInteger getM2() {
        return M2;
    }

    public void setM2(BigInteger m2) {
        M2 = m2;
    }

    public BigInteger getTwoObject() {
        return twoObject;
    }

    public void setTwoObject(BigInteger twoObject) {
        this.twoObject = twoObject;
    }

    public BigInteger getAllR2_Sum() {
        return allR2_Sum;
    }

    public void setAllR2_Sum(BigInteger allR2_Sum) {
        this.allR2_Sum = allR2_Sum;
    }

    public BigInteger getH2() {
        return h2;
    }

    public void setH2(BigInteger h2) {
        this.h2 = h2;
    }

    public BigInteger getN() {
        return N;
    }

    public void setN(BigInteger n) {
        N = n;
    }

    public BigInteger getN1() {
        return N1;
    }

    public void setN1(BigInteger n1) {
        N1 = n1;
    }

    public BigInteger getN2() {
        return N2;
    }

    public void setN2(BigInteger n2) {
        N2 = n2;
    }

    @Override
    public String toString() {
        return "BankBalanceAndBitZKP{" +
                "c1=" + c1 +
                ", D=" + D +
                ", D1=" + D1 +
                ", D2=" + D2 +
                ", c2=" + c2 +
                ", M=" + M +
                ", M1=" + M1 +
                ", M2=" + M2 +
                ", twoObject=" + twoObject +
                ", allR2_Sum=" + allR2_Sum +
                ", h2=" + h2 +
                ", N=" + N +
                ", N1=" + N1 +
                ", N2=" + N2 +
                '}';
    }
}
