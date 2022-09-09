package com.jxiang.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jxiang.blog.pojo.Tag;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagMapper extends BaseMapper<Tag> {

    @Select("" +
        "SELECT id, avatar, tag_name AS tagName " +
        "FROM ms_tag " +
        "WHERE id IN (SELECT tag_id FROM ms_article_tag WHERE article_id = #{articleId}) "
    )
    List<Tag> findTagsByArticleId(Long articleId);

    @Select("" +
        "SELECT tag_id, COUNT(article_id) AS article_count " +
        "FROM ms_article_tag " +
        "GROUP BY tag_id " +
        "ORDER BY article_count DESC " +
        "LIMIT #{limit} "
    )
    List<Long> findHotTagIds(int limit);

}