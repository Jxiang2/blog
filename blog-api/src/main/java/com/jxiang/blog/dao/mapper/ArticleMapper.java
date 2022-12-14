package com.jxiang.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxiang.common.pojo.Article;
import com.jxiang.common.vo.ArchiveVo;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleMapper extends BaseMapper<Article> {

  @Select("" +
      "SELECT FROM_UNIXTIME(create_date/1000, '%y') AS year, "
      + "FROM_UNIXTIME(create_date/1000, '%m') AS month, "
      + "COUNT(*) as count "
      + "FROM ms_article "
      + "GROUP BY year, month;"
  )
  List<ArchiveVo> listArchiveSummary();

  @Select({
      "<script>",
      "select * from ms_article",
      "<where>",
      "deleted=0",
      "<if test = 'queryString != null'> ",
      "and ( match(title, summary) against (#{queryString}) )",
      "</if>",
      "</where>",
      "</script>"
  })
  Page<Article> queryFullText(Page<Article> page, @Param("queryString") String queryString);

}
