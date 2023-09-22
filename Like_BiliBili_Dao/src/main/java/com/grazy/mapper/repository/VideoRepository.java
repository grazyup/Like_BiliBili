package com.grazy.mapper.repository;

import com.grazy.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @Author: grazy
 * @Date: 2023/9/23 0:29
 * @Description: es中视频数据的管理
 */


/**
 *  继承ElasticsearchRepository与mybatis-pule一样，
 *      可以使用SpringData中elasticsearch内部封装的方法，可以直接调用现成的,
 *          使用与mapper大致一样
 */
public interface VideoRepository extends ElasticsearchRepository<Video,Long> {

    //使用SpringData的命名规范，其会自动解析方法名，执行es中的数据操作
    Video findByTitleLike(String keyword);
}
