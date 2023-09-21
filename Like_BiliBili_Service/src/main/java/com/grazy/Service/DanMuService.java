package com.grazy.Service;

import com.grazy.domain.DanMu;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/20 15:32
 * @Description: 弹幕服务层
 */

public interface DanMuService {

    /**
     * 添加弹幕到数据库
     * @param danMu 弹幕对象
     */
    void addDanMu(DanMu danMu);


    /**
     * 异步添加弹幕到数据库
     * @param danMu 弹幕对象
     */
    void asyncAddDanMu(DanMu danMu);

    /**
     * 添加弹幕到Redis缓存
     * @param danMu 弹幕对象
     */
    void addDanMuToRedis(DanMu danMu);


    /**
     * 筛选创建时间段上的弹幕数据
     * @param videoId 视频id
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 弹幕数据
     */
    List<DanMu> getDanMUData(Long videoId, String startTime, String endTime);
}
