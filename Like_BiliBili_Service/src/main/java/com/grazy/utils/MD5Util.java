package com.grazy.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * MD5加密
 * 单向加密算法
 * 特点：加密速度快，不需要秘钥，但是安全性不高，需要搭配随机盐值使用
 *
 */
public class MD5Util {

	public static String sign(String content, String salt, String charset) {
		content = content + salt;
		return DigestUtils.md5Hex(getContentBytes(content, charset));
	}

	public static boolean verify(String content, String sign, String salt, String charset) {
		content = content + salt;
		String mysign = DigestUtils.md5Hex(getContentBytes(content, charset));
		return mysign.equals(sign);
	}

	private static byte[] getContentBytes(String content, String charset) {
		if (!"".equals(charset)) {
			try {
				return content.getBytes(charset);
			} catch (UnsupportedEncodingException var3) {
				throw new RuntimeException("MD5签名过程中出现错误,指定的编码集错误");
			}
		} else {
			return content.getBytes();
		}
	}


	/**
	 *  获取文件MD5加密字符串（尽管修改了文件名和文件后缀，只要不修改内容，生成的加密都是一样）
	 *  	MD5对文件加密是对二进制输入流进行加密。在实现MD5加密的过程中，首先需要将文件通过流的方式转化为二进制，然后再计算该文件的MD5值。
	 * @param file 文件
	 * @return 加密字符串
	 * @throws IOException IO异常
	 */
    public static String getFileMD5(MultipartFile file) throws IOException {
		InputStream inputStream = file.getInputStream();
		//获取文件字节数组输出流
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] bytes = new byte[1024];
		int realLen;
		while((realLen = inputStream.read(bytes)) > 0){
			//写入
			byteArrayOutputStream.write(bytes,0,realLen);
		}
		inputStream.close();
		//执行加密
		return DigestUtils.md5Hex(byteArrayOutputStream.toByteArray());
    }
}