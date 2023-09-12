package com.grazy.api;

import com.grazy.Service.FileService;
import com.grazy.domain.ResultResponse;
import com.grazy.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: grazy
 * @Date: 2023/9/11 16:03
 * @Description: 文件上传api
 */

@RestController
public class FileApi {


    @Autowired
    private FastDFSUtil fastDFSUtil;


    @Autowired
    private FileService fileService;


    /**
     * 分片断点续传
     * @param slice 分片文件
     * @param fileMD5 MD5加密的完整文件名
     * @param sliceNo 第几片
     * @param totalSliceNo 分片总数量
     * @return 文件上传地址
     * @throws Exception 异常
     */
    @PutMapping("/file-slices")
    public ResultResponse<String> uploadFileBySlice(MultipartFile slice, String fileMD5, Integer sliceNo, Integer totalSliceNo) throws Exception{
        String filePath = fileService.uploadFileBySlice(slice,fileMD5,sliceNo,totalSliceNo);
        return ResultResponse.success("上传完毕!",filePath);
    }


    /**
     * 测试文件分片功能
     * @param file 文件
     * @throws Exception 异常
     */
    @PostMapping("/test-slice")
    public void testSlice(MultipartFile file) throws Exception {
        fastDFSUtil.convertFileToSlices(file);
    }
}
