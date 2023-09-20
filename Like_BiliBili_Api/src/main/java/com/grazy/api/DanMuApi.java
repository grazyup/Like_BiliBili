package com.grazy.api;

import com.grazy.Service.DanMuService;
import com.grazy.domain.DanMu;
import com.grazy.domain.ResultResponse;
import com.grazy.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DanMuApi {

    @Autowired
    private DanMuService danMuService;

    @Autowired
    private UserSupport userSupport;

    @GetMapping("/danmus")
    public ResultResponse<List<DanMu>> getDanMus(@RequestParam Long videoId, String startTime, String endTime) throws Exception {
        List<DanMu> list;
        try{
            //判断当前是游客模式还是用户登录模式
            userSupport.getCurrentUserId();
            //若是用户登录模式，则允许用户进行时间段筛选
            list = danMuService.getDanMUData(videoId, startTime, endTime);
        }catch (Exception ignored){
            //若为游客模式，则不允许用户进行时间段筛选
            list = danMuService.getDanMUData(videoId, null, null);
        }
        return ResultResponse.success("获取成功！",list);
    }

}
