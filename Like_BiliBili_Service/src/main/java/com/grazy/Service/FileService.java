package com.grazy.Service;

import com.grazy.domain.MyFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: grazy
 * @Date: 2023/9/11 16:10
 * @Description: 文件服务层
 */

public interface FileService {

    /**
     * 分片断点续传
     * @param slice 分片文件
     * @param fileMD5 MD5加密的完整文件名
     * @param sliceNo 第几片
     * @param totalSliceNo 分片总数量
     * @return 文件上传地址
     * @throws Exception 异常
     */
    String uploadFileBySlice(MultipartFile slice, String fileMD5, Integer sliceNo, Integer totalSliceNo) throws Exception;


    /**
     *  获取文件MD5加密字符串
     * @param file 文件
     * @return 加密字符串
     */
    String getFileMD5(MultipartFile file) throws IOException;


    /**
     * 根据MD5获取文件
     * @param fileMd5 文件的MD5加密
     * @return 文件
     */
    public MyFile getFileByMd5(String fileMd5);
}
