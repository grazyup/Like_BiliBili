package com.grazy.api;

import com.grazy.Service.VideoService;
import com.grazy.domain.PageResult;
import com.grazy.domain.ResultResponse;
import com.grazy.domain.Video;
import com.grazy.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: grazy
 * @Date: 2023/9/12 16:45
 * @Description: 视频投稿Api
 */

@RestController
public class VideoApi {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserSupport userSupport;


    /**
     * 视频投稿
     * @param video 视频对象
     * @return 响应结果
     */
    @PostMapping("/videos")
    public ResultResponse<String> addVideos(@RequestBody Video video){
        video.setUserId(userSupport.getCurrentUserId());
        videoService.addVideos(video);
        return ResultResponse.success("投稿成功！");
    }


    /**
     * 首页瀑布流获取视频列表（分页查询）
     * @param area 分区
     */
    @GetMapping("/videos")
    public ResultResponse<PageResult<Video>> pageListVideos(@RequestParam(defaultValue = "1") Integer current, @RequestParam(defaultValue = "10") Integer size, String area){
        PageResult<Video> result = videoService.pageListVideos(current,size,area);
        return ResultResponse.success("获取成功！",result);
    }
}
