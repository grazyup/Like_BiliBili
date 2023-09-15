package com.grazy.utils;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.grazy.Exception.CustomException;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @Author: grazy
 * @Date: 2023/9/10 15:46
 * @Description:  fastDFS文件存储管理系统工具类
 */

@Component
public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Resource
    private RedisTemplate<String,String > redisTemplate;

    @Value("${fdfs.http.storage-add}")
    private String httpStorageAdd;

    private static final String DEFAULT_GROUP = "group1";

    private static final String PATH_KEY = "path-key";

    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key";

    private static final String UPLOADED_NO_KEY = "uploaded-no-key";

    private static final int SLICE_SIZE = 1024 * 1024 * 10;


    public String getFileType(MultipartFile file){
        if(file == null){
            throw new CustomException("非法文件");
        }
        String originalFilename = file.getOriginalFilename();
       return originalFilename.substring(originalFilename.indexOf(".") + 1);
    }


    /**
     * 上传普通（一般）文件
     * @return 文件路径
     */
    public String uploadCommonFile(MultipartFile file) throws Exception {
        //文件属性集合
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = this.getFileType(file);
        InputStream inputStream = file.getInputStream();
        //上传,返回文件的路径相关信息
        StorePath storePath = fastFileStorageClient.uploadFile(inputStream, file.getSize(), fileType, metaDataSet);
        inputStream.close();
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
        InputStream inputStream = file.getInputStream();
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP,inputStream, file.getSize(), fileType);
        inputStream.close();
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
        InputStream inputStream = file.getInputStream();
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, inputStream, file.getSize(), offSet);
        inputStream.close();
        file.getInputStream().close();
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
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
        }else{
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if(StringUtils.isNullOrEmpty(filePath)){
                throw new CustomException("上传失败！");
            }
            this.modifyFile(file,filePath,uploadedSize);
            //需要将redis的value序列化后才能实现String转为Integer自增 (GenericToStringSerializer、StringRedisSerializer将字符串的值直接转为字节数组，所以保存到redis中是数字，所以可以进行加1)
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey,String.valueOf(uploadedSize));
        //如果文件全部上传完成，清空Redis中的文件上传缓存数据
        Integer uploadedNo = Integer.valueOf(redisTemplate.opsForValue().get(uploadedNoKey));
        String resultPath = null;
        if(uploadedNo.equals(totalSliceNo)){
            resultPath =  redisTemplate.opsForValue().get(pathKey);
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
        //删除临时文件
        file.delete();
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


    /**
     * 分片（分段/拉进度条）视频在线观看
     * @param request 请求
     * @param response 响应
     * @param path 视频相对路径
     * @throws Exception 异常
     */
    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String path) throws Exception{
        //视频http全路径
        String url = httpStorageAdd + path;
        //获取文件的详情信息
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        Long totalFileSize = fileInfo.getFileSize();

        //创建一个新请求头内的参数集合
        Map<String,Object> headers = new HashMap<>();
        //获取Request请求中的全部请求头名称
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            //将请求头中的数据添加到新建的headers中
            headers.put(header,request.getHeader(header));
        }

        /*************************************以下是分段（拖动进度条）的核心代码**********************************************/

        //一个资源单次请求的范围
        String rangeStr = request.getHeader("Range");
        if(StringUtils.isNullOrEmpty(rangeStr)){
            rangeStr = "bytes=0-" + (totalFileSize - 1);
        }
        String[] RangeArray = rangeStr.split("bytes=|-");
        Long begin = Long.valueOf(0);
        if(RangeArray.length >= 2){
            begin = Long.parseLong(RangeArray[1]);
        }
        Long end = totalFileSize - 1;
        if(RangeArray.length >= 3){
            end = Long.parseLong(RangeArray[2]);
        }
        //计算出这次请求范围长度
        Long len = (end - begin) + 1;
        String ContentRange = "bytes " + begin + "-" + end + "/" + totalFileSize;
        response.setHeader("Accept-Ranges","bytes");
        response.setContentLength(Math.toIntExact(len));
        response.setHeader("Content-Range",ContentRange);
        response.setHeader("Content-Type","video/mp4");

        /*************************************以上是分段（拖动进度条）的核心代码**********************************************/

        //分片在线观看的响应状态码是 206，所以响应头中的响应码需要也要改成206（如果没有上面分段的核心的代码，只需要将状态码改成200即可）
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        //调用http工具类进行访问视频
        HttpUtil.get(url,headers,response);
    }

}
