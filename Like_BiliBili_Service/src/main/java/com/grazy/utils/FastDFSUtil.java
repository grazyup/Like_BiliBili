package com.grazy.utils;

import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.grazy.Exception.CustomException;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: grazy
 * @Date: 2023/9/10 15:46
 * @Description:  fastDFS文件存储管理系统工具类
 */

public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Resource
    private RedisTemplate<String,String > redisTemplate;

    private static final String DEFAULT_GROUP = "group1";

    private static final String PATH_KEY = "path-key";

    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key";

    private static final String UPLOADED_NO_KEY = "uploaded-no-key";

    private static final int SLICE_SIZE = 1024 * 1024 * 2;


    public String getFileType(MultipartFile file){
        if(file == null){
            throw new CustomException("非法文件");
        }
        String originalFilename = file.getOriginalFilename();
       return originalFilename.substring(originalFilename.indexOf("."));
    }


    /**
     * 上传普通（一般）文件
     * @return 文件路径
     */
    public String uploadCommonFile(MultipartFile file) throws Exception {
        //文件属性集合
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = this.getFileType(file);
        //上传,返回文件的路径相关信息
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }


    /**
     * 上传可以断点续传的文件
     *     一般是前端将文件分片处理，调用该方法上传第一个分片
     *
     * @param file 第一个文件分片
     * @return 第一个分片上传的地址
     * @throws Exception 异常
     */
    public String uploadAppenderFile(MultipartFile file) throws Exception{
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }


    /**
     *  修改/更新 大型文件的分片
     *      在上传第一个分片并拿到地址后，调用该方法将剩余的分片逐步拼接到第一个分片之后，最终实现文件的上传
     *      （这两个方法一般联合使用）
     * @param file 分片文件
     * @param filePath 第一个文件的路径
     * @param offSet 偏移量
     * @throws Exception 异常
     */
    public void modifyFile(MultipartFile file, String filePath, Long offSet) throws Exception{
        appendFileStorageClient.modifyFile(DEFAULT_GROUP,filePath,
                file.getInputStream(),file.getSize(),offSet);
    }


    /**
     * 大型文件分片/断点续传存储
     * @param file 分配文件
     * @param fileMd5 文件MD5信息
     * @param sliceNo 第几片
     * @param totalSliceNo 总片数
     * @return 存储相对路径
     * @throws Exception 异常
     */
    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception{
        if(file == null || sliceNo == null || totalSliceNo == null){
            throw new CustomException("参数异常！");
        }
        String pathKey = PATH_KEY + fileMd5;   //第一个分片的上传路径(也是一整个文件的最终上传路径)
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;   //文件已上传的大小
        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;  //已上传的分片的片数
        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize = 0L;
        if(!StringUtils.isNullOrEmpty(uploadedSizeStr)){
            //之前已经上传过一部分，将Redis中存储已上传的分片大小赋值给临时参数
            uploadedSize = Long.valueOf(uploadedSizeStr);
        }
        if(sliceNo == 1){  //上传第一片
            String firstFilePath = this.uploadAppenderFile(file);
            if(StringUtils.isNullOrEmpty(firstFilePath)){
                throw new CustomException("上传失败！");
            }
            redisTemplate.opsForValue().set(pathKey,firstFilePath);
            redisTemplate.opsForValue().set(uploadedNoKey,"1");
        }else{
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if(StringUtils.isNullOrEmpty(filePath)){
                throw new CustomException("上传失败！");
            }
            this.modifyFile(file,filePath,uploadedSize);
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey,String.valueOf(uploadedSize));
        //如果文件全部上传完成，清空Redis中的文件上传缓存数据
        Integer uploadedNo = Integer.valueOf(redisTemplate.opsForValue().get(uploadedNoKey));
        String resultPath = redisTemplate.opsForValue().get(pathKey);
        if(uploadedNo.equals(totalSliceNo)){
            //上传完毕
            redisTemplate.delete(Arrays.asList(pathKey,uploadedNoKey,uploadedSizeKey));
        }
        return resultPath;
    }


    /**
     * 文件分片(一般分片由前端操作)
     * @param multipartFile 文件
     * @throws Exception 异常
     */
    public void convertFileToSlices(MultipartFile multipartFile) throws Exception{
        String fileType = this.getFileType(multipartFile);
        File file = this.multipartFileToFile(multipartFile);
        //文件的大小
        long fileSize = file.length();
        int count = 1;
        for(int i = 0; i < fileSize; i += SLICE_SIZE){
            //RandomAccessFile是读取文件中某个区间的二进制流
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            //设置读取区间的开头偏移量
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
            //每次默认读取大小为bytes（有可能在末尾剩余大小不够默认值，所以需要返回真实读取到的文件大小）
            int realReadSize = randomAccessFile.read(bytes);
            String localPath = "D:/java_SpringBoot/Project源码资料/fastDFS_TempFile/" + count + "." + fileType;
            File sliceFile = new File(localPath);
            //创建一个输出流
            FileOutputStream fos = new FileOutputStream(sliceFile);
            fos.write(bytes,0,realReadSize);
            fos.close();
            randomAccessFile.close();
            count++;
        }
    }


    /**
     * 将MultipartFile类型转换为java中的File类型
     * @param multipartFile 文件
     * @return java类型的file
     */
    private File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] fileName = originalFilename.split("\\.");
        File tempFile = File.createTempFile(fileName[0], "." + fileName[1]);
        //将multipartFile中的内容传输到tempFile中
        multipartFile.transferTo(tempFile);
        return tempFile;
    }


    /**
     * 删除
     */
    public void deleteFile(String filePath){
        fastFileStorageClient.deleteFile(filePath);
    }

}
