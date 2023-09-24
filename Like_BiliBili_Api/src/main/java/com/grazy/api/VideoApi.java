package com.grazy.api;

import com.grazy.Service.ElasticsearchService;
import com.grazy.Service.VideoService;
import com.grazy.domain.*;
import com.grazy.support.UserSupport;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ElasticsearchService elasticsearchService;


    /**
     * 视频投稿
     * @param video 视频对象
     * @return 响应结果
     */
    @PostMapping("/videos")
    public ResultResponse<String> addVideos(@RequestBody Video video){
        video.setUserId(userSupport.getCurrentUserId());
        videoService.addVideos(video);
        //在es中添加视频信息（注意：要在添加进数据库之后才能添加到es中，因为需要对象id作为标识）
        elasticsearchService.addVideo(video);
        return ResultResponse.success("投稿成功！");
    }


    /**
     * 首页瀑布流获取视频列表（分页查询）
     * @param area 分区
     */
    @GetMapping("/videos")
    public ResultResponse<PageResult<Video>> pageListVideos(Integer size,  Integer no, String area){
        PageResult<Video> result = videoService.pageListVideos(no,size,area);
        return ResultResponse.success("获取成功！",result);
    }


    /**
     * 分片（拖动进度条）在线观看视频（以二进制流的形式在response中将视频输出） --- 与下载文件视频类似
     * @param url 视频的相对路径
     */
    @GetMapping("/video-slices")
    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url){
        videoService.viewVideoOnlineBySlices(request,response,url);
    }


    /**
     * 点赞视频
     * @param videoId 视频id
     * @return 响应结果
     */
    @PostMapping("/video-likes")
    public ResultResponse<String> addVideoLikes(@RequestParam Long videoId){
        videoService.addVideoLikes(userSupport.getCurrentUserId(),videoId);
        return ResultResponse.success("点赞成功！");
    }


    /**
     * 取消点赞
     * @param videoId 视频id
     * @return 响应结果
     */
    @DeleteMapping("/video-likes")
    public ResultResponse<String> unlikeVideo(@RequestParam Long videoId){
        videoService.unlikeVideo(userSupport.getCurrentUserId(),videoId);
        return ResultResponse.success("点赞取消成功！");
    }


    /**
     * 获取视频的点赞数量
     * @param videoId 视频id
     * @return 响应点赞数据
     */
    @GetMapping("/video-likes")
    public ResultResponse<Map<String,Object>> getVideoLikeNumber(@RequestParam Long videoId){
        Long currentUserId = null;
        try {
            currentUserId = userSupport.getCurrentUserId();
        }catch (Exception ignore){
            //忽略异常
        }
        Map<String,Object> videoLikeMap = videoService.getVideoLikeNumber(currentUserId,videoId);
        return ResultResponse.success("获取成功！",videoLikeMap);
    }


    /**
     * 收藏视频
     * @param videoCollection 视频收藏对象
     * @return 响应结果
     */
    @PostMapping("/video-collections")
    public ResultResponse<String> addVideoCollection(@RequestBody VideoCollection videoCollection){
        videoCollection.setUserId(userSupport.getCurrentUserId());
        videoService.addVideoCollection(videoCollection);
        return ResultResponse.success("收藏成功！");
    }


    /**
     * 取消收藏
     * @param videoCollection 视频收藏对象
     * @return 响应结果
     */
    @DeleteMapping("/video-collections")
    public ResultResponse<String> unfollowVideo(@RequestBody VideoCollection videoCollection){
        videoCollection.setUserId(userSupport.getCurrentUserId());
        videoService.unfollowVideoCollection(videoCollection);
        return ResultResponse.success("收藏成功！");
    }


    /**
     * 获取视频收藏的数量
     * @param  videoId 视频收藏对象
     * @return 收藏详情数据
     */
    @GetMapping("/video-collections")
    public ResultResponse<Map<String,Object>> getVideoCollectionNumber(@RequestParam Long videoId){
        Long userId = null;
        try{
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignore){
            //忽略异常
        }
        VideoCollection videoCollection = new VideoCollection();
        videoCollection.setVideoId(videoId);
        videoCollection.setUserId(userId);
        Map<String,Object> videoCollectionNumberMap = videoService.getVideoCollectionNumber(videoCollection);
        return ResultResponse.success("获取成功！",videoCollectionNumberMap);
    }


    /**
     * 视频投币
     * @param videoCoin 视频投币记录对象
     * @return 响应结果
     */
    @PostMapping("/video-coins")
    public ResultResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin){
        Long currentUserId = userSupport.getCurrentUserId();
        videoService.addVideoCoins(currentUserId,videoCoin);
        return ResultResponse.success("投币成功！");
    }


    /**
     * 获取视频的全部投币数
     * @param videoId 视频投币记录对象
     * @return 投币详情数据
     */
    @GetMapping("/video-coins")
    public ResultResponse<Map<String,Object>> getVideoCoinsNumber(@RequestParam Long videoId){
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignore){
        }
        VideoCoin videoCoin = new VideoCoin();
        videoCoin.setUserId(userId);
        videoCoin.setVideoId(videoId);
        Map<String,Object> videoCoinsMap = videoService.getVideoCoinsNumber(videoCoin);
        return ResultResponse.success("获取成功！",videoCoinsMap);
    }


    /**
     *  发布评论
     * @param videoComment 评论对象
     * @return 响应结果
     */
    @PostMapping("/video-comments")
    public ResultResponse<String> addVideoComment(@RequestBody VideoComment videoComment){
        videoComment.setUserId(userSupport.getCurrentUserId());
        videoService.addVideoComment(videoComment);
        return ResultResponse.success("发不成功！");
    }


    /**
     * 分页获取评论
     * @param current 当前页
     * @param size 一页显示的数据条数
     * @param videoId 视频id
     * @return 分页数据
     */
    @GetMapping("/video-comments")
    public ResultResponse<PageResult<VideoComment>> pageListVideoComment(@RequestParam(defaultValue = "1") Integer current,
                                                                         @RequestParam(defaultValue = "10") Integer size,
                                                                         @RequestParam Long videoId){
        PageResult<VideoComment> videoCommentPageResult = videoService.pageListVideoComment(current,size,videoId);
        return ResultResponse.success("获取成功！",videoCommentPageResult);
    }


    /**
     * 获取视频详情
     * @param videoId 视频id
     * @return 视频详情信息
     */
    @GetMapping("/video-details")
    public ResultResponse<Map<String,Object>> getVideoDetail(@RequestParam Long videoId){
        Map<String,Object> videoDetailMap = videoService.getVideoDetail(videoId);
        return ResultResponse.success("获取成功!",videoDetailMap);
    }


    /**
     * 添加播放记录
     * @param videoView 视频播放记录对象
     * @param request 请求参数对象
     * @return 响应结果
     */
    @PostMapping("/video-views")
    public ResultResponse<String> addVideoViews(@RequestBody VideoView videoView, HttpServletRequest request){
        Long userId;
        try{
            videoView.setUserId(userSupport.getCurrentUserId());
            videoService.addVideoViews(videoView,request);
        }catch (Exception e){
            videoService.addVideoViews(videoView,request);
        }
        return ResultResponse.success("添加成功！");
    }


    /**
     * 视频播放量
     * @param videoId 视频id
     * @return 播放量
     */
    @GetMapping("/video-view-counts")
    public ResultResponse<Integer> getVideoViewCounts(@RequestParam Long videoId){
        return ResultResponse.success("获取成功",videoService.getVideoViewCounts(videoId));
    }


    /**
     * 视频内容推荐
     * @return 根据偏好算法推荐的视频数据列表
     * @throws TasteException 异常
     */
    @GetMapping("/recommendations")
    public ResultResponse<List<Video>> recommend() throws TasteException{
        List<Video> recommendVideoList =videoService.recommend(userSupport.getCurrentUserId());
        return ResultResponse.success("推荐成功!",recommendVideoList);
    }


}
