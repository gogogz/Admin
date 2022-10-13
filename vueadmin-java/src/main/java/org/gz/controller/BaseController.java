package org.gz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.gz.entity.SysRoleMenu;
import org.gz.service.*;
import org.gz.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

@Controller
public class BaseController {
    @Autowired
    HttpServletRequest req;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysRoleService sysRoleService;
    @Autowired
    SysMenuService sysMenuService;
    @Autowired
    SysRoleMenuService sysRoleMenuService;
    @Autowired
    SysUserRoleService sysUserRoleService;

    public Page getPage(){
        int current = ServletRequestUtils.getIntParameter(req,"current",1);
        int size = ServletRequestUtils.getIntParameter(req,"size",10);
        return new Page(current,size);
    }
}
