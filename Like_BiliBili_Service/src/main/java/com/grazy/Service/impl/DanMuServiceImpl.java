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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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


    /**
     * 查询策略是优先查redis中的弹幕数据，
     * 如果没有的话查询数据库，然后把查询的数据写入redis当中
     */
    @Override
    public List<DanMu> getDanMUData(Long videoId, String startTime, String endTime) throws Exception {
        String key = DANMU_KEY + videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<DanMu> danMuList = new ArrayList<>();
        if(!StringUtils.isNullOrEmpty(value)){
            danMuList = JSONArray.parseArray(value, DanMu.class);
            if(!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)){  //当前为用户登录模式
                //传入时间格式化
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
                Date startDate = simpleDateFormat.parse(startTime);
                Date endDate = simpleDateFormat.parse(endTime);
                // 对Redis中的弹幕实现时间筛选
                ArrayList<DanMu> suitableTimeDanMuList = new ArrayList<>();
                for(DanMu element: danMuList){
                    if(element.getCreateTime().after(startDate) && element.getCreateTime().before(endDate)){
                        //Redis中符合时间区间内的弹幕
                        suitableTimeDanMuList.add(element);
                    }
                }
                danMuList = suitableTimeDanMuList;
            }
        }else{
            //Redis中不存在该数据,从数据库中查询
            danMuList = danMuMapper.selectDanMUDataByStartTimeAndEndTime(videoId, startTime, endTime);
            //保存到Redis中
            redisTemplate.opsForValue().set(key,JSONObject.toJSONString(danMuList));
        }
        return danMuList;
    }
}
