package com.grazy.Service.impl;

import com.grazy.Exception.CustomException;
import com.grazy.Service.UserCoinsService;
import com.grazy.Service.UserService;
import com.grazy.Service.VideoService;
import com.grazy.domain.*;
import com.grazy.mapper.VideoMapper;
import com.grazy.utils.FastDFSUtil;
import com.grazy.utils.IpUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
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


    @Override
    public void addVideoViews(VideoView videoView, HttpServletRequest request) {
        Long userId = videoView.getUserId();
        Long videoId = videoView.getVideoId();
        //生成clientId
        String agent = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        //区分游客的规则： 操作系统 + 浏览器 + Ip  --> clientId + Ip
        String clientId = String.valueOf(userAgent.getId());
        String ip = IpUtil.getIP(request);
        Map<String, Object> params = new HashMap<>();
        if(userId == null){
            //当前为游客模式
            params.put("clientId",clientId);
            params.put("ip",ip);
        }else{
            //当前为登录模式
            params.put("userId",userId);
        }
        Date now = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //添加今日时间
        params.put("today",simpleDateFormat.format(now));
        params.put("videoId", videoId);
        //查询今日观看记录是否已存在
        VideoView dbVideoViews = videoMapper.selectVideoViews(params);
        if(dbVideoViews == null){
            videoView.setIp(ip);
            videoView.setClientId(clientId);
            videoView.setCreateTime(new Date());
            //添加数据
            videoMapper.insertVideoViews(videoView);
        }
    }


    @Override
    public Integer getVideoViewCounts(Long videoId) {
        return videoMapper.selectVideoViewCounts(videoId);
    }


    @Override
    public List<Video> recommend(Long currentUserId) throws TasteException {
        //获取全部用户的偏好数据
        List<UserPreference> userPreferenceList = videoMapper.selectAllUserPreference();
        //创建数据模型
        DataModel dataModel = this.createDataModel(userPreferenceList);
        //获取用户相似程度
        UncenteredCosineSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        //获取用户邻居（参数 : 2 是想要获取用户邻居的数量）
        NearestNUserNeighborhood userNeighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
        //构建推荐器
        GenericUserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
        //推荐视频（参数: 5 表示给用户推荐的商品数量）
        List<RecommendedItem> recommendedItems = recommender.recommend(currentUserId, 5);
        //获取的是算法所推荐的视频的videoId集合
        List<Long> itemIds = recommendedItems.stream().map(RecommendedItem::getItemID).collect(Collectors.toList());
        //返回根据推荐的视频查询到的视频数据
        return videoMapper.batchGetVideosByIds(itemIds);
    }


    @Override
    public List<Video> recommendByItem(Long userId, Long itemId, int howMany) throws TasteException {
        List<UserPreference> list = videoMapper.selectAllUserPreference();
        //创建数据模型
        DataModel dataModel = this.createDataModel(list);
        //获取内容相似程度
        ItemSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        GenericItemBasedRecommender genericItemBasedRecommender = new GenericItemBasedRecommender(dataModel, similarity);
        // 物品推荐相拟度，计算两个物品同时出现的次数，次数越多任务的相拟度越高
        List<Long> itemIds = genericItemBasedRecommender.recommendedBecause(userId, itemId, howMany)
                .stream()
                .map(RecommendedItem::getItemID)
                .collect(Collectors.toList());
        //推荐视频
        return videoMapper.batchGetVideosByIds(itemIds);
    }


    /**
     * 创建数据模型
     * @param userPreferenceList 用户偏好列表
     * @return 数据模型
     */
    private DataModel createDataModel(List<UserPreference> userPreferenceList) {
        FastByIDMap<PreferenceArray> fastByIdMap = new FastByIDMap<>();
        //List转换为map,UserId作为键值对的key,value是每个用户对应的全部视频操作评分
        Map<Long, List<UserPreference>> preferenceMap = userPreferenceList.stream().collect(Collectors.groupingBy(UserPreference::getUserId));
        //提取出全部的value存储到Set视图中
        Collection<List<UserPreference>> list = preferenceMap.values();
        for(List<UserPreference> userPreferences: list){
            GenericPreference[] array = new GenericPreference[userPreferences.size()];
            for(int i = 0; i < userPreferences.size(); i++){
                UserPreference userPreference = userPreferences.get(i);
                GenericPreference item = new GenericPreference(userPreference.getUserId(), userPreference.getVideoId(), userPreference.getValue());
                array[i] = item;
            }
            fastByIdMap.put(array[0].getUserID(),new GenericUserPreferenceArray(Arrays.asList(array)));
        }
        return new GenericDataModel(fastByIdMap);
    }

}
