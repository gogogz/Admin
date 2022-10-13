package org.gz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.gz.entity.SysMenu;
import org.gz.entity.SysRole;
import org.gz.entity.SysUser;
import org.gz.mapper.SysUserMapper;
import org.gz.service.SysMenuService;
import org.gz.service.SysRoleService;
import org.gz.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.gz.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 我的公众号：GuoZhou
 * @since 2022-10-04
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysRoleService sysRoleService;

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    SysMenuService sysMenuService;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new QueryWrapper<SysUser>().eq("username",username));
    }

    @Override
    public String getUserAuthorityInfo(Long userId) {
        String authority = "";
        SysUser sysUser = sysUserMapper.selectById(userId);
        if(redisUtil.hasKey("GrantedAuthority:"+sysUser.getUsername())){
            authority = (String) redisUtil.get("GrantedAuthority:"+sysUser.getUsername());
        }else{
            List<SysRole> roles = sysRoleService.list(new QueryWrapper<SysRole>()
                    .inSql("id","select role_id from sys_user_role where user_id = " + userId));
            if(roles.size() > 0){
                String roleCodes = roles.stream().map(r -> "ROLE_"+r.getCode()).collect(Collectors.joining(","));
                authority = roleCodes.concat(",");
            }

            List<Long> menuIds = sysUserMapper.getNavMenuIds(userId);
            System.out.println("MenusId: "+menuIds.get(0));
            if(menuIds.size() > 0){
                List<SysMenu> menus = sysMenuService.listByIds(menuIds);
                String menuPerms = menus.stream().map(m -> m.getPerms()).collect(Collectors.joining(","));
                authority = authority.concat(menuPerms);
            }
            redisUtil.set("GrantedAuthority:"+sysUser.getUsername(),authority);
        }
        System.out.println("getUserAuthorityInfo:"+authority);
        return authority;
    }

    @Override
    public void clearUserAuthorityInfo(String username) {
        redisUtil.del("GrantedAuthority:"+username);
    }

    @Override
    public void clearUserAuthorityInfoByRoleId(Long roleId) {
        List<SysUser> sysUsers = this.list(new QueryWrapper<SysUser>()
                .inSql("id","select user_id from sys_user_role where role_id =" + roleId));
        sysUsers.forEach(user -> {
            this.clearUserAuthorityInfo(user.getUsername());
        });
    }

    @Override
    public void clearUserAuthorityInfoByMenuId(Long menuId) {
        List<SysUser> sysUsers = sysUserMapper.listByMenuId(menuId);
        sysUsers.forEach(u -> {
            this.clearUserAuthorityInfo(u.getUsername());
        });
    }
}
