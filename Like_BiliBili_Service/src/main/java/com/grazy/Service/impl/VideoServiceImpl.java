package com.grazy.Service.impl;

import com.grazy.Exception.CustomException;
import com.grazy.Service.UserCoinsService;
import com.grazy.Service.UserService;
import com.grazy.Service.VideoService;
import com.grazy.domain.*;
import com.grazy.mapper.VideoMapper;
import com.grazy.utils.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: grazy
 * @Date: 2023/9/12 16:47
 * @Description:
 */

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private UserCoinsService userCoinsService;

    @Autowired
    private UserService userService;

    private static final Long DEFAULT_COLLECTION_GROUP = 0L;

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


    @Override
    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String path) {
        try{
            fastDFSUtil.viewVideoOnlineBySlices(request, response, path);
        }catch (Exception e){
            //e.printStackTrace();
        }
    }


    /**
     * 判断视频是否存在
     * @param videoId 视频id
     */
    public void judgmentVideoExist(Long videoId){
        if(videoId == null){
            throw new CustomException("参数异常！");
        }
        if(videoMapper.selectVideoByVideoId(videoId) == null){
            throw new CustomException("非法视频！");
        }
    }


    @Override
    public void addVideoLikes(Long currentUserId, Long videoId) {
        //判断视频是否存在
        this.judgmentVideoExist(videoId);
        //判断用户是否已经点赞过
        if(videoMapper.selectVideoLike(currentUserId,videoId) != null){
            throw new CustomException("您已点赞过了！");
        }
        //添加数据
        videoMapper.insertVideoLike(currentUserId,videoId,new Date());
    }


    @Override
    public void unlikeVideo(Long currentUserId, Long videoId) {
        //判断视频是否存在
        this.judgmentVideoExist(videoId);
        //判断用户是否点赞
        if(videoMapper.selectVideoLike(currentUserId,videoId) == null){
            throw new CustomException("您当前未点赞该视频！");
        }
        //删除数据
        videoMapper.deleteVideoLike(currentUserId,videoId);
    }


    @Override
    public Map<String, Object> getVideoLikeNumber(Long currentUserId, Long videoId) {
        //判断视频是否存在
        this.judgmentVideoExist(videoId);
        Integer count = videoMapper.selectVideoLikeCountByVideoId(videoId);
        //用户是否点赞当前视频
        VideoLike videoLike = videoMapper.selectVideoLike(currentUserId,videoId);
        Boolean isLike = videoLike != null;
        return new HashMap<String,Object>(){{ put("count",count); put("like",isLike);}};
    }


    @Override
    public void addVideoCollection(VideoCollection videoCollection) {
        Long groupId = videoCollection.getGroupId();
        Long userId = videoCollection.getUserId();
        Long videoId = videoCollection.getVideoId();
        if(userId == null){
            throw new CustomException("非法参数!");
        }
        //判断是否有分组id 没有添加到默认分组中
        if(groupId == null){
            //根据关注分组类型获取分组id
            groupId = videoMapper.selectCollectionGroupIdByType(DEFAULT_COLLECTION_GROUP);
            videoCollection.setGroupId(groupId);
        }
        //判断视频是否存在
        this.judgmentVideoExist(videoCollection.getVideoId());
        if(videoMapper.selectVideoCollectionByUserIdAndVideoId(userId,videoId) != null){
            throw new CustomException("您已收藏该内容！");
        }
        videoCollection.setCreateTime(new Date());
        //删除原先的数据（如此写法可以使 更新和添加 使用同一个方法）
        videoMapper.deleteVideoCollection(videoCollection);
        videoMapper.insertVideoCollection(videoCollection);
    }


    @Override
    public void unfollowVideoCollection(VideoCollection videoCollection) {
        this.judgmentVideoExist(videoCollection.getVideoId());
        if(videoMapper.selectVideoCollectionByUserIdAndVideoId(videoCollection.getUserId(), videoCollection.getVideoId()) == null){
            throw new CustomException("您未收藏该内容！");
        }
        videoMapper.deleteVideoCollection(videoCollection);
    }


    @Override
    public Map<String, Object> getVideoCollectionNumber(VideoCollection videoCollection) {
        Long videoId = videoCollection.getVideoId();
        this.judgmentVideoExist(videoId);
        Integer count = videoMapper.selectVideoCollectionCountByVideoId(videoId);
        //判断用户是否收藏了视频
        Boolean isCollection = videoMapper.selectVideoCollectionByUserIdAndVideoId(videoCollection.getUserId(),videoId) != null;
        return new HashMap<String,Object>(){{
            put("count",count);
            put("isCollection",isCollection);
        }};
    }


    @Override
    @Transactional
    public void addVideoCoins(Long currentUserId, VideoCoin videoCoin) {
        Integer amount = videoCoin.getAmount();
        Long videoId = videoCoin.getVideoId();
        //判断视频的合法性
        this.judgmentVideoExist(videoId);
        //判断当前账户是否拥有足够的硬币
        Integer userCoinsAmount = userCoinsService.getUserCoinsAmount(currentUserId);
        userCoinsAmount = userCoinsAmount == null ? 0 : userCoinsAmount;
        if(userCoinsAmount < amount){
            throw new CustomException("您的硬币数量不足！");
        }
        //查询当前用户对该视频之前的投币记录
        VideoCoin dbVideoCoins = videoMapper.selectVideoCoinsByUserIdAndVideoId(currentUserId,videoId);
        if(dbVideoCoins == null){
            //用户首次对该视频投币
            videoCoin.setUserId(currentUserId);
            videoCoin.setCreateTime(new Date());
            videoMapper.insertVideoCoins(videoCoin);
        }else{
            //获取用户投币记录表中已投的金币数额
            Integer videoCoinsAmount = videoMapper.selectVideoCoinsAmount(currentUserId,videoId);
            //更新用户以前的投币记录
            videoCoinsAmount += videoCoin.getAmount();
            videoCoin.setAmount(videoCoinsAmount);
            videoCoin.setUserId(currentUserId);
            videoCoin.setUpdateTime(new Date());
            videoMapper.updateVideoCoins(videoCoin);
        }
        //更新用户账户的硬币数量
        userCoinsService.updateUserCoinsAmount(currentUserId, userCoinsAmount - amount);
    }


    @Override
    public Map<String, Object> getVideoCoinsNumber(VideoCoin videoCoin) {
        Long videoId = videoCoin.getVideoId();
        Long userId = videoCoin.getUserId();
        this.judgmentVideoExist(videoId);
        //获取视频的全部投币数量
        Integer coinsNumber = videoMapper.selectVideoCoinsNumber(videoId);
        //判断用户是否有投币记录
        Boolean havePlayCoins = videoMapper.selectVideoCoinsByUserIdAndVideoId(userId,videoId) != null;
        return new HashMap<String,Object>(){{
            put("count",coinsNumber);
            put("havePlayCoins",havePlayCoins);
        }};
    }


    @Override
    public void addVideoComment(VideoComment videoComment) {
        this.judgmentVideoExist(videoComment.getVideoId());
        videoComment.setCreateTime(new Date());
        videoMapper.insertVideoComment(videoComment);
    }


    @Override
    public PageResult<VideoComment> pageListVideoComment(Integer current, Integer size, Long videoId) {
        this.judgmentVideoExist(videoId);
        //获取一级评论的总数
        Integer total = videoMapper.selectVideoCommentCountByVideoId(videoId);
        List<VideoComment> firstCommentList = null;
        if(total > 0){
            //分页获取一级评论数据列表
            firstCommentList = videoMapper.pageVideoCommentByVideoId((current-1)*size,size,videoId);
            //提取一级评论的评论id
            List<Long> firstCommentIdList = firstCommentList.stream().map(VideoComment::getId).collect(Collectors.toList());
            //根据一级评论id获取对应下的全部二级评论信息
            List<VideoComment> secondCommentList =videoMapper.batchGetVideoCommentsByRootIds(firstCommentIdList);
            //提取一级和二级评论发布者id
            Set<Long> firstCommentUserIdList = firstCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            Set<Long> secondCommentUserIdList = secondCommentList.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            firstCommentUserIdList.addAll(secondCommentUserIdList);
            //批量查询用户基本信息
            List<UserInfo> userInfoList = userService.selectUserInfoBy(firstCommentUserIdList);
            Map<Long,UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo::getUserId, userInfo -> userInfo));
            firstCommentList.forEach(item -> {
                Long id = item.getId();
                List<VideoComment> childList = new ArrayList<>();
                secondCommentList.forEach(second ->{
                    if(id.equals(second.getRootId())){
                        second.setUserInfo(userInfoMap.get(second.getUserId()));
                        second.setReplyUserInfo(userInfoMap.get(second.getReplyUserId()));
                        childList.add(second);
                    }
                });
                item.setChildList(childList);
                item.setUserInfo(userInfoMap.get(item.getUserId()));
            });
        }
        return new PageResult<>(total,firstCommentList);
    }


    @Override
    public Map<String, Object> getVideoDetail(Long videoId) {
        Video dbVideo = videoMapper.selectVideoByVideoId(videoId);
        Long userId = dbVideo.getUserId();
        UserInfo userInfo = userService.getUserDateById(userId).getUserInfo();
        return new HashMap<String,Object>(){{
            put("video",dbVideo);
            put("userInfo",userInfo);
        }};
    }


}
