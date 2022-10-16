package com.jxiang.blog.service.impl.article;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jxiang.blog.dao.mapper.ArticleBodyMapper;
import com.jxiang.blog.dao.mapper.ArticleMapper;
import com.jxiang.blog.dao.mapper.ArticleTagMapper;
import com.jxiang.blog.pojo.Article;
import com.jxiang.blog.pojo.ArticleBody;
import com.jxiang.blog.pojo.ArticleTag;
import com.jxiang.blog.pojo.SysUser;
import com.jxiang.blog.service.ArticleService;
import com.jxiang.blog.service.CommentService;
import com.jxiang.blog.service.thread.ThreadService;
import com.jxiang.blog.util.statics.SysUserThreadLocal;
import com.jxiang.blog.vo.ArticleVo;
import com.jxiang.blog.vo.TagVo;
import com.jxiang.blog.vo.params.ArticleBodyParam;
import com.jxiang.blog.vo.params.ArticleParam;
import com.jxiang.blog.vo.params.ArticleUpdateParam;
import com.jxiang.blog.vo.params.LimitParam;
import com.jxiang.blog.vo.params.PageParams;
import com.jxiang.blog.vo.results.ErrorCode;
import com.jxiang.blog.vo.results.Result;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {

  @Autowired
  private ArticleMapper articleMapper;
  @Autowired
  private ArticleBodyMapper articleBodyMapper;
  @Autowired
  private CommentService commentService;
  @Autowired
  private ThreadService threadService;
  @Autowired
  private ArticleTagMapper articleTagMapper;
  @Autowired
  private ArticleServiceUtils articleServiceUtils;

  @Override
  public Result listArticles(PageParams pageParams) {
    Page<Article> page = new Page<>(pageParams.getPage(),
        pageParams.getPageSize());
    LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

    Long categoryId = Long.valueOf(pageParams.getCategoryId());
    if (categoryId != null) {
      queryWrapper.eq(Article::getCategoryId, categoryId);
    }

    Long tagId = Long.valueOf(pageParams.getTagId());
    if (tagId != null) {
      // article_id 1 -- * tag_id
      LambdaQueryWrapper<ArticleTag> articleTagLambdaQueryWrapper = new LambdaQueryWrapper<>();
      articleTagLambdaQueryWrapper.eq(ArticleTag::getTagId, tagId);
      List<ArticleTag> articleTagList = articleTagMapper.selectList(
          articleTagLambdaQueryWrapper);

      List<Long> articleIdList = new ArrayList<>();
      articleTagList.forEach(
          articleTag -> articleIdList.add(articleTag.getArticleId()));

      if (articleIdList.size() > 0) {
        queryWrapper.in(Article::getId, articleIdList);
      }
    }

    queryWrapper // order by create_date DESC & weight DESC
        .orderByDesc(Article::getCreateDate)
        .orderByDesc(Article::getWeight);

    final Page<Article> articlePage = articleMapper.selectPage(page,
        queryWrapper);

    final List<ArticleVo> articleVoList = articleServiceUtils.copyList(
        articlePage.getRecords(),
        true,
        true
    );

    return articleVoList.size() != 0
        ? Result.success(articleVoList)
        : Result.success("No article yet");
  }

  @Override
  public Result listHotArticles(LimitParam limitParam) {
    LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

    queryWrapper // select id, title, view_counts from ms_article order by view_counts desc limit {limit}
        .orderByDesc(Article::getViewCounts)
        .select(Article::getId, Article::getTitle, Article::getViewCounts)
        .last("LIMIT " + limitParam.getLimit());

    final List<Article> articles = articleMapper.selectList(queryWrapper);

    final List<ArticleVo> articleVoList = articleServiceUtils.copyList(
        articles, false, false
    );

    return articleVoList.size() != 0
        ? Result.success(articleVoList)
        : Result.success("No article Yet");
  }

  @Override
  public Result listNewArticles(LimitParam limitParam) {
    LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

    queryWrapper // select id, title, createDate from ms_article order by create_date desc limit {limit}
        .orderByDesc(Article::getCreateDate)
        .select(Article::getId, Article::getTitle, Article::getCreateDate)
        .last("LIMIT " + limitParam.getLimit());

    final List<Article> articles = articleMapper.selectList(queryWrapper);

    final List<ArticleVo> articleVoList = articleServiceUtils.copyList(
        articles, false, false
    );

    return articleVoList.size() != 0
        ? Result.success(articleVoList)
        : Result.success("No article Yet");
  }

  @Override
  public Result listArchiveSummary() {
    return Result.success(articleMapper.listArchiveSummary());
  }

  @Override
  public Result findArticleById(Long articleId) {
    Article article = articleMapper.selectById(articleId);

    if (article == null) {
      return Result.failure(ErrorCode.NOT_FOUND.getCode(),
          ErrorCode.NOT_FOUND.getMsg());
    }

    ArticleVo articleVo = articleServiceUtils.copy(
        article, true, true, true, true
    );

    // use thread pool to process the add review count operation, isolated from the main program thread
    threadService.updateArticleViewCount(articleMapper, article);

    return Result.success(articleVo);
  }

  @Override
  public Result createArticle(ArticleParam articleParam) {
    SysUser sysUser = SysUserThreadLocal.get();

    // article basic fields
    Article article = new Article();
    article.setAuthorId(sysUser.getId());
    article.setWeight(Article.Article_Common);
    article.setViewCounts(0);
    article.setTitle(articleParam.getTitle());
    article.setSummary(articleParam.getSummary());
    article.setCommentCounts(0);
    article.setCreateDate(System.currentTimeMillis());

    // article category
    article.setCategoryId(Long.valueOf(articleParam.getCategory().getId()));

    articleMapper.insert(article);

    // article tags
    List<TagVo> tagVos = articleParam.getTags();
    if (tagVos != null) {
      for (TagVo tagVo : tagVos) {
        ArticleTag articleTag = new ArticleTag();
        articleTag.setTagId(Long.valueOf(tagVo.getId()));
        articleTag.setArticleId(article.getId());
        articleTagMapper.insert(articleTag);
      }
    }

    // article body
    ArticleBody articleBody = new ArticleBody();
    articleBody.setArticleId(article.getId());
    articleBody.setContent(articleParam.getBody().getContent());
    articleBody.setContentHtml(articleParam.getBody().getContentHtml());
    articleBodyMapper.insert(articleBody);
    article.setBodyId(articleBody.getId());

    // update article
    articleMapper.updateById(article);

    return Result.success(article);
  }

  @Override
  public Result deleteArticleById(Long articleId) {
    Article articleToDelete = articleMapper.selectById(articleId);

    if (articleToDelete == null) {
      return Result.failure(ErrorCode.NOT_FOUND.getCode(),
          ErrorCode.NOT_FOUND.getMsg());
    }

    if (!articleToDelete.getAuthorId()
        .equals(SysUserThreadLocal.get().getId())) {
      return Result.failure(ErrorCode.NO_LOGIN.getCode(),
          ErrorCode.NO_LOGIN.getMsg());
    }

    articleMapper.deleteById(articleToDelete);

    // delete article's comments
    boolean deleted = commentService.deleteArticleComments(articleId);

    if (deleted) {
      return Result.success(articleToDelete.getId());
    }

    return Result.failure(ErrorCode.SYSTEM_ERROR.getCode(),
        ErrorCode.SYSTEM_ERROR.getMsg());
  }

  @Override
  public Result updateArticleBodyById(Long articleId,
      ArticleBodyParam articleBody) {
    LambdaQueryWrapper<ArticleBody> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ArticleBody::getArticleId, articleId).last("LIMIT " + 1);
    ArticleBody articleBodyToUpdate = articleBodyMapper.selectOne(queryWrapper);
    Article articleToUpdate = articleMapper.selectById(articleId);

    if (articleToUpdate == null) {
      return Result.failure(ErrorCode.NOT_FOUND.getCode(),
          ErrorCode.NOT_FOUND.getMsg());
    }

    if (!articleToUpdate.getAuthorId()
        .equals(SysUserThreadLocal.get().getId())) {
      return Result.failure(ErrorCode.NO_LOGIN.getCode(),
          ErrorCode.NO_LOGIN.getMsg());
    }

    if (articleBodyToUpdate == null) {
      return Result.failure(ErrorCode.NOT_FOUND.getCode(),
          ErrorCode.NOT_FOUND.getMsg());
    }

    String content = articleBody.getContent();
    String contentHtml = articleBody.getContentHtml();

    // not update if the content has not changed
    if (content.equals(articleBodyToUpdate.getContent()) &&
        contentHtml.equals(articleBodyToUpdate.getContentHtml())) {
      return Result.failure(ErrorCode.PARAMS_ERROR.getCode(),
          ErrorCode.PARAMS_ERROR.getMsg());
    }

    articleBodyToUpdate.setContent(content);
    articleBodyToUpdate.setContentHtml(contentHtml);

    int success = articleBodyMapper.updateById(articleBodyToUpdate);

    return success == 1
        ? Result.success("Success")
        : Result.failure(ErrorCode.SYSTEM_ERROR.getCode(),
            ErrorCode.SYSTEM_ERROR.getMsg());
  }

  @Override
  public Result updateArticleById(Long articleId,
      ArticleUpdateParam articleUpdateParam) {
    Article articleToUpdate = articleMapper.selectById(articleId);

    if (articleToUpdate == null) {
      return Result.failure(ErrorCode.NOT_FOUND.getCode(),
          ErrorCode.NOT_FOUND.getMsg());
    }

    if (!articleToUpdate.getAuthorId()
        .equals(SysUserThreadLocal.get().getId())) {
      return Result.failure(ErrorCode.NO_LOGIN.getCode(),
          ErrorCode.NO_LOGIN.getMsg());
    }

    String title = articleUpdateParam.getTitle();
    String summary = articleUpdateParam.getSummary();

    if (title == null && summary == null) {
      return Result.failure(ErrorCode.PARAMS_ERROR.getCode(),
          ErrorCode.PARAMS_ERROR.getMsg());
    }

    if (title != null) {
      if (!title.equals("") && !title.equals(articleToUpdate.getTitle())) {
        articleToUpdate.setTitle(title);
      } else {
        return Result.failure(ErrorCode.PARAMS_ERROR.getCode(),
            ErrorCode.PARAMS_ERROR.getMsg());
      }
    }

    if (summary != null) {
      if (!summary.equals("") && !summary.equals(
          articleToUpdate.getSummary())) {
        articleToUpdate.setSummary(summary);
      } else {
        return Result.failure(ErrorCode.PARAMS_ERROR.getCode(),
            ErrorCode.PARAMS_ERROR.getMsg());
      }
    }

    int success = articleMapper.updateById(articleToUpdate);

    Map<String, String> res = new HashMap<>();
    res.put("title", articleToUpdate.getTitle());
    res.put("summary", articleToUpdate.getSummary());

    return success == 1
        ? Result.success(res)
        : Result.failure(ErrorCode.SYSTEM_ERROR.getCode(),
            ErrorCode.SYSTEM_ERROR.getMsg());
  }

}