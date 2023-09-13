package com.grazy.mapper;

import com.grazy.domain.Video;
import com.grazy.domain.VideoTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
}
