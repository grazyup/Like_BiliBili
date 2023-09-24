package com.grazy.Service;

import com.grazy.domain.UserInfo;
import com.grazy.domain.Video;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: grazy
 * @Date: 2023/9/23 0:35
 * @Description: es服务层
 */

public interface ElasticsearchService {


    /**
     * es全文搜索获取内容
     * @param keyword 关键词
     * @param pageNo 当前页面码
     * @param pageSize 展示条数
     * @return 数据列表
     * @throws IOException 异常
     */
    List<Map<String, Object>> getContents(String keyword, Integer pageNo, Integer pageSize) throws IOException;


    /**
     * 添加视频信息到es中
     * @param video 视频对象
     */
    void addVideo(Video video);


    /**
     * 添加用户基本信息到es中
     * @param userInfo 用户基本信息
     */
    void addUserInfo(UserInfo userInfo);


    /**
     * 在es中根据视频title模糊查询视频
     * @param keyWord 关键词
     * @return 视频对象
     */
    Video getVideo(String keyWord);


    /**
     * 删除es中全部视频信息数据
     */
    void deleteAllVideos();


    /**
     * 删除es中全部用户信息数据
     */
    void deleteAllUserInfo();
}
