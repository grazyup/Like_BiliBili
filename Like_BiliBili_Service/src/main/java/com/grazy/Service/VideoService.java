package com.grazy.Service;

import com.grazy.domain.PageResult;
import com.grazy.domain.Video;

/**
 * @Author: grazy
 * @Date: 2023/9/12 16:47
 * @Description: 视频投稿服务层
 */

public interface VideoService {

    /**
     * 视频投稿
     * @param video 视频对象
     */
    void addVideos(Video video);


    /**
     * 首页瀑布流获取视频列表（分页查询）
     * @param area 分区
     * @return 分页数据
     */
    PageResult<Video> pageListVideos(Integer current, Integer size, String area);
}
