package com.grazy.api;

import com.grazy.Service.ElasticsearchService;
import com.grazy.domain.ResultResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: grazy
 * @Date: 2023/9/23 15:54
 * @Description: 系统全局api
 */

@RestController
public class SystemApi {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @GetMapping("/contents")
    public ResultResponse<List<Map<String, Object>>> getContents(@RequestParam String keyword,
                                                                 @RequestParam Integer pageNo,
                                                                 @RequestParam Integer pageSize) throws IOException {
        List<Map<String, Object>> list = elasticsearchService.getContents(keyword,pageNo,pageSize);
        return ResultResponse.success("搜索成功！",list);
    }
}
