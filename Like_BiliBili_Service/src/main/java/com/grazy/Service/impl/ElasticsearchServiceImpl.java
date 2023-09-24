package com.grazy.Service.impl;

import com.grazy.Service.ElasticsearchService;
import com.grazy.domain.UserInfo;
import com.grazy.domain.Video;
import com.grazy.mapper.repository.UserInfoRepository;
import com.grazy.mapper.repository.VideoRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: grazy
 * @Date: 2023/9/23 0:35
 * @Description:
 */

@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Resource
    private RestHighLevelClient restHighLevelClient;


    @Override
    public List<Map<String, Object>> getContents(String keyword, Integer pageNo, Integer pageSize) throws IOException {
        //搜索存储在ES中的索引名字
        String[] indices ={"videos","user-infos"};
        //创建查询请求
        SearchRequest searchRequest = new SearchRequest(indices);
        //查询资源构建器对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(pageNo - 1);
        searchSourceBuilder.size(pageSize);
        //设置需要查询的属性字段
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "nick", "description");
        searchSourceBuilder.query(matchQueryBuilder);
        //设置请求超时终止时间
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //高亮提示
        String[] array = {"title", "nick", "description"};
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for(String key: array){
            //将需要高亮的提示字段添加到高亮构建器中，如此执行Elasticsearch查询时，这些字段（'title'，'nick'，'description'）的匹配部分将被高亮显示
            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
        }
        highlightBuilder.requireFieldMatch(false); //如果需要多个字段进行高亮，要设置为false
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        //将高亮构建器配置到查询资源构建器中
        searchSourceBuilder.highlighter(highlightBuilder);
        //将查询资源构建器配置到查询请求中
        searchRequest.source(searchSourceBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String,Object>> arrayList = new ArrayList<>();
        //返回每一个索引对应的JSON数据对象
        for(SearchHit hit: searchResponse.getHits()){
            //处理高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //获取array中元素作为key的键值对
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            for(String key: array){
                //根据需要搜索的属性字段获取键值对中的数据
                HighlightField field = highlightFields.get(key);
                if(field != null){
                    Text[] fragments = field.fragments();
                    String str = Arrays.toString(fragments);
                    str = str.substring(1, str.length() - 1);
                    sourceMap.put(key,str);
                }
            }
            arrayList.add(sourceMap);
        }
        return arrayList;
    }


    @Override
    public void addVideo(Video video){
        videoRepository.save(video);
    }


    @Override
    public void addUserInfo(UserInfo userInfo){
        userInfoRepository.save(userInfo);
    }


    @Override
    public Video getVideo(String keyWord) {
        return videoRepository.findByTitleLike(keyWord);
    }


    @Override
    public void deleteAllVideos(){
        videoRepository.deleteAll();
    }


    @Override
    public void deleteAllUserInfo(){
        videoRepository.deleteAll();
    }

}
