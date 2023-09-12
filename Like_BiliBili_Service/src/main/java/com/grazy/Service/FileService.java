package com.grazy.Service;

import org.springframework.web.multipart.MultipartFile;

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
}
