package com.grazy.mapper;

import com.grazy.domain.MyFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: grazy
 * @Date: 2023/9/12 15:48
 * @Description:
 */

@Mapper
public interface FileMapper {

    /**
     * 查询文件上传情况
     * @param fileMD5 文件MD5加密字符串
     * @return 自定义已上传文件对象
     */
    MyFile selectFileByFileMD5(String fileMD5);


    /**
     * 新增已上传文件信息
     * @param newFileData 新文件信息
     */
    void insertFileData(MyFile newFileData);


    MyFile getFileByMD5(String fileMd5);
}
