package com.grazy.mapper;

import com.grazy.domain.DanMu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: grazy
 * @Date: 2023/9/20 15:33
 * @Description:
 */

@Mapper
public interface DanMuMapper {

    void insertDanMu(DanMu danMu);

    List<DanMu> selectDanMUDataByStartTimeAndEndTime(@Param("videoId") Long videoId, @Param("startTime") String startTime, @Param("endTime") String endTime);
}
