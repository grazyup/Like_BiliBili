package com.grazy.Service;

import com.grazy.domain.Video;

/**
 * @Author: grazy
 * @Date: 2023/9/23 0:35
 * @Description: es服务层
 */

public interface ElasticsearchService {

    /**
     * 添加视频信息到es中
     * @param video 视频对象
     */
    void addVideo(Video video);


    /**
     * 根据视频title模糊查询视频
     * @param keyWord 关键词
     * @return 视频对象
     */
    Video getVideo(String keyWord);
}
