package com.grazy.Service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.grazy.Service.DanMuService;
import com.grazy.domain.DanMu;
import com.grazy.mapper.DanMuMapper;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/20 15:33
 * @Description:
 */

@Service
public class DanMuServiceImpl implements DanMuService {

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private DanMuMapper danMuMapper;

    private static final String DANMU_KEY = "dm-video-";


    @Override
    public void addDanMu(DanMu danMu) {
        danMuMapper.insertDanMu(danMu);
    }


    @Override
    @Async
    public void asyncAddDanMu(DanMu danMu) {
        danMuMapper.insertDanMu(danMu);
    }


    @Override
    public void addDanMuToRedis(DanMu danMu) {
        String key = DANMU_KEY + danMu.getVideoId();
        String value = redisTemplate.opsForValue().get(key);
        List<DanMu> danMuList = new ArrayList<>();
        if(!StringUtils.isNullOrEmpty(value)){
            danMuList = JSONArray.parseArray(value, DanMu.class);
        }
        danMuList.add(danMu);
        redisTemplate.opsForValue().set(key,JSONObject.toJSONString(danMuList));
    }


    @Override
    public List<DanMu> getDanMUData(Long videoId, String startTime, String endTime) {
        return danMuMapper.selectDanMUDataByStartTimeAndEndTime(videoId,startTime,endTime);
    }
}
