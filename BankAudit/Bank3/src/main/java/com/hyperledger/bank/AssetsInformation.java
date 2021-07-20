package com.hyperledger.bank;

import java.math.BigInteger;

/**
 * @ Author：lxgxgxgxg
 * @ Date：Created in 16:56 2021/5/11
 * @ Description：这个资产类主要是审计员审计的时候，把资产和、交易次数、二次方和，三次方和、四次方和对象化
 * @ Version: 1.0
 */
public class AssetsInformation {
    private BigInteger ComSum;
    private BigInteger r1;
    private int transactionTime;
    private BigInteger r2;
    private BigInteger doubleComSum;
    private BigInteger x;
    private BigInteger tripleComSum;
    private BigInteger y;
    private BigInteger quadraComSum;
    private BigInteger z;

    public AssetsInformation() {
    }

    public AssetsInformation(BigInteger comSum, BigInteger r1, int transactionTime, BigInteger r2, BigInteger doubleComSum,
                             BigInteger x, BigInteger tripleComSum, BigInteger y, BigInteger quadraComSum, BigInteger z) {
        ComSum = comSum;
        this.r1 = r1;
        this.transactionTime = transactionTime;
        this.r2 = r2;
        this.doubleComSum = doubleComSum;
        this.x = x;
        this.tripleComSum = tripleComSum;
        this.y = y;
        this.quadraComSum = quadraComSum;
        this.z = z;
    }

    public BigInteger getComSum() {
        return ComSum;
    }

    public void setComSum(BigInteger comSum) {
        ComSum = comSum;
    }

    public BigInteger getR1() {
        return r1;
    }

    public void setR1(BigInteger r1) {
        this.r1 = r1;
    }

    public int getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(int transactionTime) {
        this.transactionTime = transactionTime;
    }

    public BigInteger getR2() {
        return r2;
    }

    public void setR2(BigInteger r2) {
        this.r2 = r2;
    }

    public BigInteger getDoubleComSum() {
        return doubleComSum;
    }

    public void setDoubleComSum(BigInteger doubleComSum) {
        this.doubleComSum = doubleComSum;
    }

    public BigInteger getX() {
        return x;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public BigInteger getTripleComSum() {
        return tripleComSum;
    }

    public void setTripleComSum(BigInteger tripleComSum) {
        this.tripleComSum = tripleComSum;
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public BigInteger getQuadraComSum() {
        return quadraComSum;
    }

    public void setQuadraComSum(BigInteger quadraComSum) {
        this.quadraComSum = quadraComSum;
    }

    public BigInteger getZ() {
        return z;
    }

    public void setZ(BigInteger z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "AssetsInformation{" +
                "ComSum=" + ComSum +
                ", r1=" + r1 +
                ", transactionTime=" + transactionTime +
                ", r2=" + r2 +
                ", doubleComSum=" + doubleComSum +
                ", x=" + x +
                ", tripleComSum=" + tripleComSum +
                ", y=" + y +
                ", quadraComSum=" + quadraComSum +
                ", z=" + z +
                '}';
    }
}
