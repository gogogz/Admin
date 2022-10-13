package org.gz.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.gz.common.dto.PassDto;
import org.gz.common.lang.Const;
import org.gz.common.lang.Result;
import org.gz.entity.SysRole;
import org.gz.entity.SysUser;
import org.gz.entity.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 我的公众号：GuoZhou
 * @since 2022-10-04
 */
@RestController
@RequestMapping("/sys/user/")
public class SysUserController extends BaseController {
    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        SysUser sysUser = sysUserService.getById(id);
        List<SysRole> sysRoleList = sysRoleService.listRolesByUserId(id);
        sysUser.setSysRoles(sysRoleList);
        return Result.succ(sysUser);
    }
    @GetMapping("/list")
    public Result list(String username){
        Page<SysUser> pageData = sysUserService.page(getPage(), new QueryWrapper<SysUser>()
                .like(StrUtil.isNotBlank(username), "username", username));

        pageData.getRecords().forEach(u -> {

            u.setSysRoles(sysRoleService.listRolesByUserId(u.getId()));
        });

        return Result.succ(pageData);
    }

    @PostMapping("/save")
    public Result save(@RequestBody  SysUser sysUser){
        sysUser.setCreated(LocalDateTime.now());
        sysUser.setStatu(Const.STATUS_ON);
        sysUser.setPassword(passwordEncoder.encode(Const.DEFULT_PASSWORD));
        sysUser.setAvatar(Const.DEFULT_AVATAR);
        sysUserService.save(sysUser);
        return Result.succ(sysUser);
    }
    @PostMapping("/update")
    public Result update(@Validated @RequestBody SysUser sysUser) {

        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);
        return Result.succ(sysUser);
    }
    @Transactional
    @PostMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {

        sysUserService.removeByIds(Arrays.asList(ids));
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id", ids));

        return Result.succ("");
    }
    @PostMapping("/role/{userId}")
    public Result role(@PathVariable("userId") Long userId,@RequestBody Long[] roleIds){
        List<SysUserRole> sysUserRoles = new ArrayList<>();
        Arrays.asList(roleIds).forEach(roleId -> {
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(roleId);
            sysUserRoles.add(sysUserRole);
        });
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id",userId));
        sysUserRoleService.saveBatch(sysUserRoles);
        SysUser sysUser = sysUserService.getById(userId);
        sysUserService.clearUserAuthorityInfo(sysUser.getUsername());

        return Result.succ("");
    }
    @PostMapping("/repass")
    public Result repass(@RequestBody Long userId) {

        SysUser sysUser = sysUserService.getById(userId);

        sysUser.setPassword(passwordEncoder.encode(Const.DEFULT_PASSWORD));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);
        return Result.succ("");
    }
    @PostMapping("/updatePass")
    public Result updatePass(@Validated @RequestBody PassDto passDto, Principal principal) {

        SysUser sysUser = sysUserService.getByUsername(principal.getName());

        boolean matches = passwordEncoder.matches(passDto.getCurrentPass(), sysUser.getPassword());
        if (!matches) {
            return Result.fail("旧密码不正确");
        }

        sysUser.setPassword(passwordEncoder.encode(passDto.getPassword()));
        sysUser.setUpdated(LocalDateTime.now());

        sysUserService.updateById(sysUser);
        return Result.succ("");
    }

}
