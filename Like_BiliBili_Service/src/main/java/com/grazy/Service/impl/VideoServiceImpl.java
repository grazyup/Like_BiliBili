package com.grazy.Service.impl;

import com.grazy.Exception.CustomException;
import com.grazy.Service.VideoService;
import com.grazy.domain.PageResult;
import com.grazy.domain.Video;
import com.grazy.domain.VideoTag;
import com.grazy.mapper.VideoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/12 16:47
 * @Description:
 */

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Override
    @Transactional
    public void addVideos(Video video) {
        video.setCreateTime(new Date());
        //插入数据库视频表
        videoMapper.insertVideo(video);
        Long videoId = video.getId();
        //获取视频标签关联列表
        List<VideoTag> videoTagList = video.getVideoTagList();
        videoTagList.forEach(item -> {
            item.setVideoId(videoId);
            item.setCreateTime(new Date());
        });
        //新增数据到关联表中
        videoMapper.batchAddVideoTags(videoTagList);
    }


    @Override
    public PageResult<Video> pageListVideos(Integer current, Integer size, String area) {
        if(current == null || size == null){
            throw new CustomException("参数异常！");
        }
        //计算SQL中开始位置
        Integer startNumber = (current - 1) * size;
        //查询数据库该条件下的数据总数
       Integer total = videoMapper.selectCountByAre(area);
       List<Video> records = new ArrayList<>();
       if(total > 0){
           records = videoMapper.selectVideosByArea(startNumber,size,area);
           records.forEach(itm -> {
               //获取该视频对应的标签关系数据,并对属性赋值
               itm.setVideoTagList(videoMapper.selectVideoTagListByVideoId(itm.getId()));
           });
       }
       return new PageResult<>(total,records);
    }


}
