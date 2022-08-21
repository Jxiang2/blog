package com.jxiang.blog.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jxiang.blog.dao.SysUserMapper;
import com.jxiang.blog.pojo.SysUser;
import com.jxiang.blog.services.AuthService;
import com.jxiang.blog.services.SysUserService;
import com.jxiang.blog.vo.SysUserVo;
import com.jxiang.blog.vo.results.ErrorCode;
import com.jxiang.blog.vo.results.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    AuthService authService;

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickname("unknown");
        }
        return sysUser;
    }

    @Override
    public SysUser findAuthUserForLogin(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper
            .eq(SysUser::getAccount, account)
            .eq(SysUser::getPassword, password)
            .select(SysUser::getId, SysUser::getAccount, SysUser::getAvatar, SysUser::getNickname)
            .last("LIMIT 1");

        SysUser sysUser = sysUserMapper.selectOne(queryWrapper);

        if (sysUser != null) {
            sysUser.setLastLogin(System.currentTimeMillis());
            sysUserMapper.updateById(sysUser);
            return sysUser;
        }

        return null;
    }

    @Override
    public Result findUserByToken(String token) {


        SysUser sysUser = authService.checkToken(token);

        if (sysUser == null) {
            return Result.failure(ErrorCode.TOKEN_INVALID.getCode(), ErrorCode.TOKEN_INVALID.getMsg());
        }

        SysUserVo sysUserVo = new SysUserVo();
        BeanUtils.copyProperties(sysUser, sysUserVo);

        return Result.success(sysUserVo);
    }

    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SysUser::getAccount, account).last("LIMIT 1");

        return sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public void save(SysUser sysUser) {
        // id is auto-generated with 雪花算法
        sysUserMapper.insert(sysUser);
    }

    @Override
    public SysUserVo getSysUserVoById(Long sysUserId) {
        SysUser sysUser = sysUserMapper.selectById(sysUserId);
        if (sysUser == null) {
            // generate a template for all anonymous users
            sysUser = new SysUser();
            sysUser.setAccount("anonymous user");
            sysUser.setAvatar("unknown.png");
            sysUser.setNickname("unknown");
        }

        SysUserVo sysUserVo = new SysUserVo();
        BeanUtils.copyProperties(sysUser, sysUserVo); // copy field values of sysUser to sysUserVo if match

        return sysUserVo;
    }

}
