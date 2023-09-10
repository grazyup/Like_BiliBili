package com.grazy.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.grazy.Exception.CustomException;

import java.util.Calendar;
import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/8/30 15:40
 * @Description:
 */

public class TokenUtil {

    //签发者
    private static final String ISSUER = "Grazy";


    /**
     * 生成允许访问token
     * @param userId 用户id
     * @return token
     * @throws Exception 异常
     */
    public static String generateAccessToken(Long userId) throws Exception {
        //RSA加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        //生成日历对象
        Calendar calendar = Calendar.getInstance();
        //设置时间
        calendar.setTime(new Date());
        //设置token过期时间
        calendar.add(Calendar.SECOND,300);
        //生成JWT
        return JWT.create().withKeyId(String.valueOf(userId))
                //系统的签发者
                .withIssuer(ISSUER)
                //设置过期时间
                .withExpiresAt(calendar.getTime())
                //生成签名加密
                .sign(algorithm);
    }

    /**
     * 生成刷新token
     * @param userId 用户id
     * @return token
     * @throws Exception 异常
     */
    public static String generateRefreshToken(Long userId) throws Exception {
        //RSA加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        //生成日历对象
        Calendar calendar = Calendar.getInstance();
        //设置时间
        calendar.setTime(new Date());
        //设置token过期时间
//        calendar.add(Calendar.DAY_OF_MONTH,7);
        calendar.add(Calendar.SECOND,20);
        //生成JWT
        return JWT.create().withKeyId(String.valueOf(userId))
                //系统的签发者
                .withIssuer(ISSUER)
                //设置过期时间
                .withExpiresAt(calendar.getTime())
                //生成签名加密
                .sign(algorithm);
    }


    /**
     * 解析验证token
     * @param token token值
     * @return 用户id
     */
    public static Long verifyToken(String token){
        try {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            //验证对象
            JWTVerifier verifier = JWT.require(algorithm).build();
            //解密后的jwt
            DecodedJWT decodedJWT = verifier.verify(token);
            return Long.valueOf(decodedJWT.getKeyId());
        } catch (TokenExpiredException e){
            throw new CustomException("555","Token已过期!");
        } catch (Exception e){
            throw new CustomException("非法Token！");
        }
    }


    /**
     * 解析验证RefreshToken是否过期
     * @param refreshToken 刷新token
     * @return 用户id
     */
    public static Boolean verifyRefreshToken(String refreshToken) throws Exception {
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            //验证对象
            JWTVerifier verifier = JWT.require(algorithm).build();
            //解密后的jwt
            DecodedJWT decodedJWT = verifier.verify(refreshToken);
            return true;
    }

}
