package com.hyperledger.bank;

import java.math.BigInteger;

public class Bank {
    //四个银行之间的零知识资产结算审计系统
    private BigInteger com;                 //交易金额的承诺
    private BigInteger bitCom;              //比特承诺，是否参与了这次交易
    private int isFrom;                     //是否是这笔交易的支出银行
    private BigInteger doubleCom;           //隐藏值的二次方的承诺
    private BigInteger tripleCom;           //隐藏值的三次方的承诺
    private BigInteger quadraCom;           //隐藏值的四次方的承诺

    /**
     * 下面的字段用于证明bitCom中的隐藏值非0即1
     */
    private BigInteger bitComTemp1;         //中间承诺变量1
    private BigInteger bitComTemp2;         //中间承诺变量2
    private BigInteger challenge;           //挑战
    private BigInteger hash_challenge;      //挑战的hash
    private BigInteger bitComResponse1;     //响应1
    private BigInteger bitComResponse2;     //响应2
    private BigInteger bitComResponse3;     //响应3

    /**
     * 下面的字段用于证明资产只转给一个银行
     */
    private BigInteger temp1;               //中间变量 g的s次方
    private BigInteger temp1C;              //pyt的哈希
    private BigInteger temp1S;              //证明temp1中间变量1
    private BigInteger bitComMulTemp1;      //中间变量，bitCom和temp相乘

    private BigInteger comTemp1;            //中间变量，com的1+s或者0+s次方
    private BigInteger comTemp1C;           //证明comTemp1是1/0+s次方 pyt的哈希
    private BigInteger comTemp1S;           //证明comTemp1是1/0+s次方的中间变量1

    /**
     * 下面的字段用于证明指数相等的证明
     */
    private BigInteger indexEqualTemp1;     //指数相等中间变量1
    private BigInteger indexEqualTemp2;     //指数相等中间变量2
    private BigInteger indexEqualTemp3;     //指数相等中间变量3
    private BigInteger indexEqualTemp4;     //指数相等中间变量4

    /**
     * 下面的字段用于证明承诺平方的零知识证明，即doubleCom中的隐藏值是com隐藏值的平方
     */
    private BigInteger comSAddVIndex;       //com的s+v次方
    private BigInteger comSIndex;           //com的s次方
    private BigInteger hBaseIndex;          //h的原始次方
    private BigInteger hash_h;              //h的相关hash
    private BigInteger h_Response;          //h的相关响应

    /**
     * 下面的字段用于证明承诺三次方的零知识证明，即tripleCom中的隐藏值是com隐藏值的立方
     */
    private BigInteger comSAddVSquareIndex; //com的s+v^2次方
    private BigInteger hBaseIndex1;         //h的原始次方
    private BigInteger hash_h1;             //h的相关hash
    private BigInteger h_Response1;         //h的相关相应

    /**
     * 下面的字段用于证明承诺四次方的零知识，即quadraCom中的隐藏值是com隐藏值上午四次方
     */
    private BigInteger comSAddVTripleIndex; //com的s+v^3次方
    private BigInteger hBaseIndex2;
    private BigInteger hash_h2;
    private BigInteger h_Response2;

    /**
     * 下面的字段用于证明v的四次方的范围证明
     */
    private BigInteger A;
    private BigInteger S;
    private BigInteger yHash;
    private BigInteger zHash;
    private BigInteger xHash;
    private BigInteger T1;
    private BigInteger T2;
    private String lx;
    private String rx;
    private BigInteger t;
    private BigInteger taox;
    private BigInteger miu;

    public Bank(){}

    public Bank(BigInteger com, BigInteger bitCom, int isFrom, BigInteger doubleCom, BigInteger tripleCom, BigInteger quadraCom, BigInteger bitComTemp1,
                BigInteger bitComTemp2, BigInteger challenge, BigInteger hash_challenge, BigInteger bitComResponse1, BigInteger bitComResponse2,
                BigInteger bitComResponse3, BigInteger temp1, BigInteger temp1C, BigInteger temp1S, BigInteger bitComMulTemp1, BigInteger comTemp1,
                BigInteger comTemp1C, BigInteger comTemp1S, BigInteger indexEqualTemp1, BigInteger indexEqualTemp2, BigInteger indexEqualTemp3,
                BigInteger indexEqualTemp4, BigInteger comSAddVIndex, BigInteger comSIndex, BigInteger hBaseIndex, BigInteger hash_h, BigInteger h_Response,
                BigInteger comSAddVSquareIndex, BigInteger hBaseIndex1, BigInteger hash_h1, BigInteger h_Response1, BigInteger comSAddVTripleIndex,
                BigInteger hBaseIndex2, BigInteger hash_h2, BigInteger h_Response2, BigInteger a, BigInteger s, BigInteger yHash, BigInteger zHash,
                BigInteger xHash, BigInteger t1, BigInteger t2, String lx, String rx, BigInteger t, BigInteger taox, BigInteger miu) {
        this.com = com;
        this.bitCom = bitCom;
        this.isFrom = isFrom;
        this.doubleCom = doubleCom;
        this.tripleCom = tripleCom;
        this.quadraCom = quadraCom;
        this.bitComTemp1 = bitComTemp1;
        this.bitComTemp2 = bitComTemp2;
        this.challenge = challenge;
        this.hash_challenge = hash_challenge;
        this.bitComResponse1 = bitComResponse1;
        this.bitComResponse2 = bitComResponse2;
        this.bitComResponse3 = bitComResponse3;
        this.temp1 = temp1;
        this.temp1C = temp1C;
        this.temp1S = temp1S;
        this.bitComMulTemp1 = bitComMulTemp1;
        this.comTemp1 = comTemp1;
        this.comTemp1C = comTemp1C;
        this.comTemp1S = comTemp1S;
        this.indexEqualTemp1 = indexEqualTemp1;
        this.indexEqualTemp2 = indexEqualTemp2;
        this.indexEqualTemp3 = indexEqualTemp3;
        this.indexEqualTemp4 = indexEqualTemp4;
        this.comSAddVIndex = comSAddVIndex;
        this.comSIndex = comSIndex;
        this.hBaseIndex = hBaseIndex;
        this.hash_h = hash_h;
        this.h_Response = h_Response;
        this.comSAddVSquareIndex = comSAddVSquareIndex;
        this.hBaseIndex1 = hBaseIndex1;
        this.hash_h1 = hash_h1;
        this.h_Response1 = h_Response1;
        this.comSAddVTripleIndex = comSAddVTripleIndex;
        this.hBaseIndex2 = hBaseIndex2;
        this.hash_h2 = hash_h2;
        this.h_Response2 = h_Response2;
        A = a;
        S = s;
        this.yHash = yHash;
        this.zHash = zHash;
        this.xHash = xHash;
        T1 = t1;
        T2 = t2;
        this.lx = lx;
        this.rx = rx;
        this.t = t;
        this.taox = taox;
        this.miu = miu;
    }

    public BigInteger getCom() {
        return com;
    }

    public void setCom(BigInteger com) {
        this.com = com;
    }

    public BigInteger getBitCom() {
        return bitCom;
    }

    public void setBitCom(BigInteger bitCom) {
        this.bitCom = bitCom;
    }

    public int getisFrom() {
        return isFrom;
    }

    public void setisFrom(int isFrom) {
        this.isFrom = isFrom;
    }

    public BigInteger getDoubleCom() {
        return doubleCom;
    }

    public void setDoubleCom(BigInteger doubleCom) {
        this.doubleCom = doubleCom;
    }

    public BigInteger getTripleCom() {
        return tripleCom;
    }

    public void setTripleCom(BigInteger tripleCom) {
        this.tripleCom = tripleCom;
    }

    public BigInteger getQuadraCom() {
        return quadraCom;
    }

    public void setQuadraCom(BigInteger quadraCom) {
        this.quadraCom = quadraCom;
    }

    public BigInteger getBitComTemp1() {
        return bitComTemp1;
    }

    public void setBitComTemp1(BigInteger bitComTemp1) {
        this.bitComTemp1 = bitComTemp1;
    }

    public BigInteger getBitComTemp2() {
        return bitComTemp2;
    }

    public void setBitComTemp2(BigInteger bitComTemp2) {
        this.bitComTemp2 = bitComTemp2;
    }

    public BigInteger getChallenge() {
        return challenge;
    }

    public void setChallenge(BigInteger challenge) {
        this.challenge = challenge;
    }

    public BigInteger getHash_challenge() {
        return hash_challenge;
    }

    public void setHash_challenge(BigInteger hash_challenge) {
        this.hash_challenge = hash_challenge;
    }

    public BigInteger getBitComResponse1() {
        return bitComResponse1;
    }

    public void setBitComResponse1(BigInteger bitComResponse1) {
        this.bitComResponse1 = bitComResponse1;
    }

    public BigInteger getBitComResponse2() {
        return bitComResponse2;
    }

    public void setBitComResponse2(BigInteger bitComResponse2) {
        this.bitComResponse2 = bitComResponse2;
    }

    public BigInteger getBitComResponse3() {
        return bitComResponse3;
    }

    public void setBitComResponse3(BigInteger bitComResponse3) {
        this.bitComResponse3 = bitComResponse3;
    }

    public BigInteger getTemp1() {
        return temp1;
    }

    public void setTemp1(BigInteger temp1) {
        this.temp1 = temp1;
    }

    public BigInteger getTemp1C() {
        return temp1C;
    }

    public void setTemp1C(BigInteger temp1C) {
        this.temp1C = temp1C;
    }

    public BigInteger getTemp1S() {
        return temp1S;
    }

    public void setTemp1S(BigInteger temp1S) {
        this.temp1S = temp1S;
    }

    public BigInteger getBitComMulTemp1() {
        return bitComMulTemp1;
    }

    public void setBitComMulTemp1(BigInteger bitComMulTemp1) {
        this.bitComMulTemp1 = bitComMulTemp1;
    }

    public BigInteger getComTemp1() {
        return comTemp1;
    }

    public void setComTemp1(BigInteger comTemp1) {
        this.comTemp1 = comTemp1;
    }

    public BigInteger getComTemp1C() {
        return comTemp1C;
    }

    public void setComTemp1C(BigInteger comTemp1C) {
        this.comTemp1C = comTemp1C;
    }

    public BigInteger getComTemp1S() {
        return comTemp1S;
    }

    public void setComTemp1S(BigInteger comTemp1S) {
        this.comTemp1S = comTemp1S;
    }

    public BigInteger getIndexEqualTemp1() {
        return indexEqualTemp1;
    }

    public void setIndexEqualTemp1(BigInteger indexEqualTemp1) {
        this.indexEqualTemp1 = indexEqualTemp1;
    }

    public BigInteger getIndexEqualTemp2() {
        return indexEqualTemp2;
    }

    public void setIndexEqualTemp2(BigInteger indexEqualTemp2) {
        this.indexEqualTemp2 = indexEqualTemp2;
    }

    public BigInteger getIndexEqualTemp3() {
        return indexEqualTemp3;
    }

    public void setIndexEqualTemp3(BigInteger indexEqualTemp3) {
        this.indexEqualTemp3 = indexEqualTemp3;
    }

    public BigInteger getIndexEqualTemp4() {
        return indexEqualTemp4;
    }

    public void setIndexEqualTemp4(BigInteger indexEqualTemp4) {
        this.indexEqualTemp4 = indexEqualTemp4;
    }

    public BigInteger getComSAddVIndex() {
        return comSAddVIndex;
    }

    public void setComSAddVIndex(BigInteger comSAddVIndex) {
        this.comSAddVIndex = comSAddVIndex;
    }

    public BigInteger getComSIndex() {
        return comSIndex;
    }

    public void setComSIndex(BigInteger comSIndex) {
        this.comSIndex = comSIndex;
    }

    public BigInteger gethBaseIndex() {
        return hBaseIndex;
    }

    public void sethBaseIndex(BigInteger hBaseIndex) {
        this.hBaseIndex = hBaseIndex;
    }

    public BigInteger getHash_h() {
        return hash_h;
    }

    public void setHash_h(BigInteger hash_h) {
        this.hash_h = hash_h;
    }

    public BigInteger getH_Response() {
        return h_Response;
    }

    public void setH_Response(BigInteger h_Response) {
        this.h_Response = h_Response;
    }

    public BigInteger getComSAddVSquareIndex() {
        return comSAddVSquareIndex;
    }

    public void setComSAddVSquareIndex(BigInteger comSAddVSquareIndex) {
        this.comSAddVSquareIndex = comSAddVSquareIndex;
    }

    public BigInteger gethBaseIndex1() {
        return hBaseIndex1;
    }

    public void sethBaseIndex1(BigInteger hBaseIndex1) {
        this.hBaseIndex1 = hBaseIndex1;
    }

    public BigInteger getHash_h1() {
        return hash_h1;
    }

    public void setHash_h1(BigInteger hash_h1) {
        this.hash_h1 = hash_h1;
    }

    public BigInteger getH_Response1() {
        return h_Response1;
    }

    public void setH_Response1(BigInteger h_Response1) {
        this.h_Response1 = h_Response1;
    }

    public BigInteger getComSAddVTripleIndex() {
        return comSAddVTripleIndex;
    }

    public void setComSAddVTripleIndex(BigInteger comSAddVTripleIndex) {
        this.comSAddVTripleIndex = comSAddVTripleIndex;
    }

    public BigInteger gethBaseIndex2() {
        return hBaseIndex2;
    }

    public void sethBaseIndex2(BigInteger hBaseIndex2) {
        this.hBaseIndex2 = hBaseIndex2;
    }

    public BigInteger getHash_h2() {
        return hash_h2;
    }

    public void setHash_h2(BigInteger hash_h2) {
        this.hash_h2 = hash_h2;
    }

    public BigInteger getH_Response2() {
        return h_Response2;
    }

    public void setH_Response2(BigInteger h_Response2) {
        this.h_Response2 = h_Response2;
    }

    public BigInteger getA() {
        return A;
    }

    public void setA(BigInteger a) {
        A = a;
    }

    public BigInteger getS() {
        return S;
    }

    public void setS(BigInteger s) {
        S = s;
    }

    public BigInteger getyHash() {
        return yHash;
    }

    public void setyHash(BigInteger yHash) {
        this.yHash = yHash;
    }

    public BigInteger getzHash() {
        return zHash;
    }

    public void setzHash(BigInteger zHash) {
        this.zHash = zHash;
    }

    public BigInteger getxHash() {
        return xHash;
    }

    public void setxHash(BigInteger xHash) {
        this.xHash = xHash;
    }

    public BigInteger getT1() {
        return T1;
    }

    public void setT1(BigInteger t1) {
        T1 = t1;
    }

    public BigInteger getT2() {
        return T2;
    }

    public void setT2(BigInteger t2) {
        T2 = t2;
    }

    public String getLx() {
        return lx;
    }

    public void setLx(String lx) {
        this.lx = lx;
    }

    public String getRx() {
        return rx;
    }

    public void setRx(String rx) {
        this.rx = rx;
    }

    public BigInteger getT() {
        return t;
    }

    public void setT(BigInteger t) {
        this.t = t;
    }

    public BigInteger getTaox() {
        return taox;
    }

    public void setTaox(BigInteger taox) {
        this.taox = taox;
    }

    public BigInteger getMiu() {
        return miu;
    }

    public void setMiu(BigInteger miu) {
        this.miu = miu;
    }

    @Override
    public String toString() {
        return "Bank{" +
                "com=" + com +
                ", bitCom=" + bitCom +
                ", isFrom=" + isFrom +
                ", doubleCom=" + doubleCom +
                ", tripleCom=" + tripleCom +
                ", quadraCom=" + quadraCom +
                ", bitComTemp1=" + bitComTemp1 +
                ", bitComTemp2=" + bitComTemp2 +
                ", challenge=" + challenge +
                ", hash_challenge=" + hash_challenge +
                ", bitComResponse1=" + bitComResponse1 +
                ", bitComResponse2=" + bitComResponse2 +
                ", bitComResponse3=" + bitComResponse3 +
                ", temp1=" + temp1 +
                ", temp1C=" + temp1C +
                ", temp1S=" + temp1S +
                ", bitComMulTemp1=" + bitComMulTemp1 +
                ", comTemp1=" + comTemp1 +
                ", comTemp1C=" + comTemp1C +
                ", comTemp1S=" + comTemp1S +
                ", indexEqualTemp1=" + indexEqualTemp1 +
                ", indexEqualTemp2=" + indexEqualTemp2 +
                ", indexEqualTemp3=" + indexEqualTemp3 +
                ", indexEqualTemp4=" + indexEqualTemp4 +
                ", comSAddVIndex=" + comSAddVIndex +
                ", comSIndex=" + comSIndex +
                ", hBaseIndex=" + hBaseIndex +
                ", hash_h=" + hash_h +
                ", h_Response=" + h_Response +
                ", comSAddVSquareIndex=" + comSAddVSquareIndex +
                ", hBaseIndex1=" + hBaseIndex1 +
                ", hash_h1=" + hash_h1 +
                ", h_Response1=" + h_Response1 +
                ", comSAddVTripleIndex=" + comSAddVTripleIndex +
                ", hBaseIndex2=" + hBaseIndex2 +
                ", hash_h2=" + hash_h2 +
                ", h_Response2=" + h_Response2 +
                ", A=" + A +
                ", S=" + S +
                ", yHash=" + yHash +
                ", zHash=" + zHash +
                ", xHash=" + xHash +
                ", T1=" + T1 +
                ", T2=" + T2 +
                ", lx=" + lx +
                ", rx=" + rx +
                ", t=" + t +
                ", taox=" + taox +
                ", miu=" + miu +
                '}';
    }
}
