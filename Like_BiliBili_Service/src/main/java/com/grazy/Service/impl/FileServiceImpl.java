package com.grazy.Service.impl;

import com.grazy.Service.FileService;
import com.grazy.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: grazy
 * @Date: 2023/9/11 16:10
 * @Description:
 */

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Override
    public String uploadFileBySlice(MultipartFile slice, String fileMD5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        return fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);
    }
}
