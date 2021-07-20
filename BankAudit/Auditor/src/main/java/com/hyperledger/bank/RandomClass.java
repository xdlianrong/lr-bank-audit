package com.hyperledger.bank;

import java.math.BigInteger;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2021/2/26
 * \* Time: 16:14
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public class RandomClass {
    //随机数类
    private BigInteger r1;
    private BigInteger r2;
    private BigInteger x;
    private BigInteger y;
    private BigInteger z;

    public RandomClass() {
    }

    public RandomClass(BigInteger r1, BigInteger r2, BigInteger x, BigInteger y, BigInteger z) {
        this.r1 = r1;
        this.r2 = r2;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public BigInteger getR1() {
        return r1;
    }

    public void setR1(BigInteger r1) {
        this.r1 = r1;
    }

    public BigInteger getR2() {
        return r2;
    }

    public void setR2(BigInteger r2) {
        this.r2 = r2;
    }

    public BigInteger getX() {
        return x;
    }

    public void setX(BigInteger x) {
        this.x = x;
    }

    public BigInteger getY() {
        return y;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }

    public BigInteger getZ() {
        return z;
    }

    public void setZ(BigInteger z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Random{" +
                "r1=" + r1 +
                ", r2=" + r2 +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
