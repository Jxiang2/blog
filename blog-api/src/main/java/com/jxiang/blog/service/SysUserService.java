package com.jxiang.blog.service;

import com.jxiang.common.pojo.SysUser;
import com.jxiang.common.vo.AuthorVo;
import com.jxiang.common.vo.SysUserVo;
import com.jxiang.common.vo.result.Result;
import org.springframework.stereotype.Service;

@Service
public interface SysUserService {

  /**
   * retrieve an author vo by a given id
   *
   * @param id sysUser's id
   * @return single sysUser
   */
  AuthorVo findAuthorVoById(Long id);

  /**
   * retrieve a user based on its username and password, and reset last login time to current
   *
   * @param account  user account
   * @param password user password
   * @return Result
   */
  SysUser findUserForLogin(String account, String password);

  /**
   * retrieve user's authUserVo according to token
   *
   * @param token jwt token
   * @return Result
   */
  Result findCurrentLoginUserVoByToken(String token);

  /**
   * find sysUser according to account (unique)
   *
   * @param account user account
   * @return Result
   */
  SysUser findUserByAccount(String account);

  /**
   * save sysUser to db
   *
   * @param sysUser new created sysUser object
   */
  void save(SysUser sysUser);

}
