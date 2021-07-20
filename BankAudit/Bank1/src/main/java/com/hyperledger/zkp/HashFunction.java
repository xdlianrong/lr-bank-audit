package com.hyperledger.zkp;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: lxq
 * \* Date: 2021/2/25
 * \* Time: 18:04
 * \* To change this template use File | Settings | File Templates.
 * \* Description:  主要实现在非交互式零知识证明中的哈希函数
 * \
 */
public class HashFunction {
    /**
     * 利用java原生的摘要实现SHA256加密
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256StrJava(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
//            encodeStr = byte2Hex(messageDigest.digest());
            encodeStr = byte2Dec(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodeStr;
    }
    /**
     * 将byte转为16进制
     * @param bytes
     * @return
     */
    /**
    private static String byte2Hex(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i=0;i<bytes.length;i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length()==1){
                //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }
     */
    /**
     * 将byte转为十进制
     * @param bytes
     * @return string
     */
    private static String byte2Dec(byte[] bytes){
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        String str = binary(bytes, 10);
        return str;
    }

    private static String binary(byte[] bytes, int radix){
        return new BigInteger(1,bytes).toString(radix);//这里的1代表正数
    }

}
