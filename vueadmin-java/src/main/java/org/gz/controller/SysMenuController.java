package org.gz.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.gz.common.dto.SysMenuDto;
import org.gz.common.lang.Const;
import org.gz.common.lang.Result;
import org.gz.entity.SysMenu;
import org.gz.entity.SysRoleMenu;
import org.gz.entity.SysUser;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
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
@RequestMapping("/sys/menu/")
public class SysMenuController extends BaseController {
    @GetMapping("/nav")
    public Result nav(Principal principal){
        String username = principal.getName();
        SysUser sysUser = sysUserService.getByUsername(username);
        String authorityInfo = sysUserService.getUserAuthorityInfo(sysUser.getId());
        String[] authorityInfoArr = StringUtils.tokenizeToStringArray(authorityInfo,",");
        List<SysMenuDto> navs = sysMenuService.getCurrenUserNavs();
        return Result.succ(MapUtil.builder()
                .put("authoritys",authorityInfoArr)
                .put("nav",navs)
                .map());

    }
    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long id){
        SysMenu sysMenu = sysMenuService.getById(id);
        return Result.succ(sysMenu);
    }
    @GetMapping("list")
    public Result list(){
        List<SysMenu> sysMenus = sysMenuService.tree();
        return Result.succ(sysMenus);
    }
    @PostMapping("/save")
    public Result save(@Validated @RequestBody SysMenu sysMenu){
        sysMenu.setCreated(LocalDateTime.now());
        sysMenu.setStatu(Const.STATUS_ON);
        sysMenuService.save(sysMenu);
        return Result.succ(sysMenu);
    }
    @PostMapping("/update")
    public Result update(@Validated @RequestBody SysMenu sysMenu){
        sysMenu.setUpdated(LocalDateTime.now());
        sysMenuService.updateById(sysMenu);
        sysUserService.clearUserAuthorityInfoByMenuId(sysMenu.getId());
        return Result.succ(sysMenu);
    }
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable("id") Long id){
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id",id));
        if(count > 0){
            return Result.fail("请先删除子菜单");
        }
        sysUserService.clearUserAuthorityInfoByMenuId(id);
        sysMenuService.removeById(id);
        // 同步删除
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id", id));
        return Result.succ("");
    }
}
