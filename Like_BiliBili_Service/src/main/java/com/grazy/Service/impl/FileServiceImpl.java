package com.grazy.Service.impl;

import com.grazy.Service.FileService;
import com.grazy.domain.MyFile;
import com.grazy.mapper.FileMapper;
import com.grazy.utils.FastDFSUtil;
import com.grazy.utils.MD5Util;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

/**
 * @Author: grazy
 * @Date: 2023/9/11 16:10
 * @Description:
 */

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private FileMapper fileMapper;


    @Override
    public String uploadFileBySlice(MultipartFile slice, String fileMD5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        //根据fileMD5从数据库中查询文件是否已上传过
        MyFile file = fileMapper.selectFileByFileMD5(fileMD5);
        if(file != null) {
            return file.getUrl();
        }
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);
        if(!StringUtils.isNullOrEmpty(url)){
            //分片文件全部上传完毕
            MyFile newFileData = new MyFile(null,url,fastDFSUtil.getFileType(slice),fileMD5,new Date());
            //文件数据加入数据库
            fileMapper.insertFileData(newFileData);
        }
        return url;
    }



    @Override
    public String getFileMD5(MultipartFile file) throws IOException {
        return MD5Util.getFileMD5(file);
    }
}
