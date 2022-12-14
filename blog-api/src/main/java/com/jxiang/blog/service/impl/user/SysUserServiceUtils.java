package com.jxiang.blog.service.impl.user;

import com.jxiang.common.pojo.SysUser;
import com.jxiang.common.vo.AuthorVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceUtils {

  AuthorVo copyAuthor(SysUser sysUser) {
    AuthorVo authorVo = new AuthorVo();
    authorVo.setId(String.valueOf(sysUser.getId()));
    // copy properties of sysUser to sysUserVo
    BeanUtils.copyProperties(sysUser, authorVo);
    return authorVo;
  }
}
