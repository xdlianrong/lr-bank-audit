package com.hyperledger.bank;

import java.math.BigInteger;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2021/3/15
 * \* Time: 16:01
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public class BankAudit {
    /**
     * 这个类主要的原因存在是：从链上获取到一条交易信息之后，在本地零知识验证之后，只保存我们需要审计的六个字段到allTransaction.txt文件，在文件中保存的是json格式，需要转换成类，这个时候
     * 原始的bank类就不能使用，因为原始的bank类中的字段太多，与json字符串不符合，所以新建此类，用于从json到类的转换。
     */
    private BigInteger com;
    private BigInteger bitCom;
    private int isFrom;
    private BigInteger doubleCom;
    private BigInteger tripleCom;
    private BigInteger quadraCom;

    public BankAudit() {}

    public BankAudit(BigInteger com, BigInteger bitCom, int isFrom, BigInteger doubleCom, BigInteger tripleCom, BigInteger quadraCom) {
        this.com = com;
        this.bitCom = bitCom;
        this.isFrom = isFrom;
        this.doubleCom = doubleCom;
        this.tripleCom = tripleCom;
        this.quadraCom = quadraCom;
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

    public int getIsFrom() {
        return isFrom;
    }

    public void setIsFrom(int isFrom) {
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

    @Override
    public String toString() {
        return "BankAudit{" +
                "com=" + com +
                ", bitCom=" + bitCom +
                ", isFrom=" + isFrom +
                ", doubleCom=" + doubleCom +
                ", tripleCom=" + tripleCom +
                ", quadraCom=" + quadraCom +
                '}';
    }
}
