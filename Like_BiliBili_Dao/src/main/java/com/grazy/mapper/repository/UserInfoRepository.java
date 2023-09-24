package com.grazy.mapper.repository;

import com.grazy.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: grazy
 * @Date: 2023/9/23 2:13
 * @Description: es中用户基本信息数据管理
 */
public interface UserInfoRepository  extends ElasticsearchRepository<UserInfo,Long> {
}
