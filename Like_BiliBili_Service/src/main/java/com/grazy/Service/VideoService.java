package com.grazy.Service;

import com.grazy.domain.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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


    /**
     * 分片（拉进度条）在线观看视频（以二进制流的形式在response中将视频输出） --- 与下载文件视频类似
     * @param path 视频的相对路径
     */
    void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String path);


    /**
     *  点赞视频
     * @param currentUserId 当前用户id
     * @param videoId 视频id
     */
    void addVideoLikes(Long currentUserId, Long videoId);


    /**
     * 取消点赞
     * @param currentUserId 当前用户
     * @param videoId 视频id
     */
    void unlikeVideo(Long currentUserId, Long videoId);


    /**
     * 获取视频的点赞数量
     * @param videoId 视频id
     * @return 响应点赞数据
     */
    Map<String, Object> getVideoLikeNumber(Long currentUserId, Long videoId);


    /**
     * 收藏视频
     * @param videoCollection 视频对象
     */
    void addVideoCollection(VideoCollection videoCollection);


    /**
     * 取消收藏
     */
    void unfollowVideoCollection(VideoCollection videoCollection);


    /**
     * 获取视频收藏量
     * @return 收藏详情数据
     */
    Map<String, Object> getVideoCollectionNumber(VideoCollection videoCollection);


    /**
     * 视频投币
     * @param currentUserId 当前用户id
     * @param videoCoin 投币记录对象
     */
    void addVideoCoins(Long currentUserId, VideoCoin videoCoin);


    /**
     * 获取视频的全部投币量
     * @param videoCoin 投币记录对象
     * @return 视频投币的详情数据
     */
    Map<String, Object> getVideoCoinsNumber(VideoCoin videoCoin);


    /**
     *  发布评论
     * @param videoComment 评论对象
     */
    void addVideoComment(VideoComment videoComment);


    /**
     * 分页获取评论
     * @param current 当前页
     * @param size 一页显示的数据条数
     * @param videoId 视频id
     * @return 分页数据
     */
    PageResult<VideoComment> pageListVideoComment(Integer current, Integer size, Long videoId);


    /**
     * 获取视频详情
     * @param videoId 视频id
     * @return 视频详情信息
     */
    Map<String, Object> getVideoDetail(Long videoId);
}
