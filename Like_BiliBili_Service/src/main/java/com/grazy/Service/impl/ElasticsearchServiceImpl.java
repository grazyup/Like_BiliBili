package com.grazy.Service.impl;

import com.grazy.Service.ElasticsearchService;
import com.grazy.domain.Video;
import com.grazy.mapper.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: grazy
 * @Date: 2023/9/23 0:35
 * @Description:
 */

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Autowired
    private VideoRepository videoRepository;


    @Override
    public void addVideo(Video video){
        videoRepository.save(video);
    }


    @Override
    public Video getVideo(String keyWord) {
        return videoRepository.findByTitleLike(keyWord);
    }

}
