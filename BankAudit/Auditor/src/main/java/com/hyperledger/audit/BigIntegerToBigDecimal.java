package com.hyperledger.audit;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2020/12/18
 * \* Time: 11:37
 * \* To change this template use File | Settings | File Templates.
 * \* Description:  在进行平均值，方差计算时，将BigInteger类型转换为BigDecimal
 * \
 */
public interface BigIntegerToBigDecimal {
    /**
     * 在我们计算方差、平均值、HHI、偏度以及峰度的时候，会用到大整数的除法，但是BigInteger是整数之间的出发，相除也是保留整数部分，
     * 所以我们需要从文件中读取的时候，直接把从文件中读取到的类型就是BigDecimal类型。但是我们在验证账本同态信息和银行提交上来的信息之后，
     * 得到的数据类型是BigInteger类型，但是不能直接一步从BigInteger转换为BigDecimal,需要先把BigInteger先转换为double，然后把double
     * 转换为BigDecimal类型，之后就可以进行除法运算，并且保留小数点后位数。
     */
    static BigDecimal transferBigIntegertoBigDecimal(BigInteger bi){
        //首先把传进来的BigInteger转换成double
        double temp = bi.doubleValue();
        //然后把double类型转换成BigDecimal类型
        BigDecimal bd = BigDecimal.valueOf(temp);

        //返回
        return bd;
    }
}
