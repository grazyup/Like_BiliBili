package com.grazy.mapper;

import com.grazy.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: grazy
 * @Date: 2023/9/12 16:48
 * @Description:
 */

@Mapper
public interface VideoMapper {

    void insertVideo(Video video);

    void batchAddVideoTags(List<VideoTag> videoTagList);

    Integer selectCountByAre(String area);

    List<Video> selectVideosByArea(@Param("startNumber") Integer startNumber, @Param("size") Integer size, @Param("area") String area);

    List<VideoTag> selectVideoTagListByVideoId(Long videoId);

    Video selectVideoByVideoId(Long videoId);

    VideoLike selectVideoLike(@Param("currentUserId") Long currentUserId, @Param("videoId") Long videoId);

    void insertVideoLike(@Param("currentUserId")Long currentUserId, @Param("videoId") Long videoId, @Param("createTime") Date date);

    void deleteVideoLike(@Param("currentUserId") Long currentUserId, @Param("videoId") Long videoId);

    Integer selectVideoLikeCountByVideoId(Long videoId);

    Long selectCollectionGroupIdByType(Long groupType);

    VideoCollection selectVideoCollectionByUserIdAndVideoId(@Param("userId") Long userId, @Param("videoId") Long videoId);

    void insertVideoCollection(VideoCollection videoCollection);

    void deleteVideoCollection(VideoCollection videoCollection);

    Integer selectVideoCollectionCountByVideoId(Long videoId);

    VideoCoin selectVideoCoinsByUserIdAndVideoId(@Param("userId") Long currentUserId, @Param("videoId") Long videoId);

    void insertVideoCoins( VideoCoin videoCoin);

    Integer selectVideoCoinsAmount(@Param("userId") Long currentUserId, @Param("videoId") Long videoId);

    void updateVideoCoins(VideoCoin videoCoin);

    Integer selectVideoCoinsNumber(Long videoId);

    void insertVideoComment(VideoComment videoComment);

    Integer selectVideoCommentCountByVideoId(Long videoId);

    List<VideoComment> pageVideoCommentByVideoId(@Param("current") Integer current, @Param("size") Integer size, @Param("videoId") Long videoId);

    List<VideoComment> batchGetVideoCommentsByRootIds(List<Long> firstCommentIdList);

    VideoView selectVideoViews(Map<String, Object> params);

    void insertVideoViews(VideoView videoView);

    Integer selectVideoViewCounts(Long videoId);
}
