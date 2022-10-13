package org.gz.service.impl;

import cn.hutool.json.JSONUtil;
import org.gz.common.dto.SysMenuDto;
import org.gz.entity.SysMenu;
import org.gz.entity.SysUser;
import org.gz.mapper.SysMenuMapper;
import org.gz.mapper.SysUserMapper;
import org.gz.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.gz.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 我的公众号：GuoZhou
 * @since 2022-10-04
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Autowired
    SysUserService sysUserService;
    @Autowired
    SysUserMapper sysUserMapper;
    @Autowired
    SysMenuService sysMenuService;
    @Override
    public List<SysMenuDto> getCurrenUserNavs() {
        String useranme = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("getCurrenUserNavs username：" + useranme);
        SysUser sysUser = sysUserService.getByUsername(useranme);
        List<Long> menuIds = sysUserMapper.getNavMenuIds(sysUser.getId());
        System.out.println("getCurrenUserNavs menuIds:"+menuIds.get(0));
        List<SysMenu> menus = buildTreeMenu(this.listByIds(menuIds));
        System.out.println("getCurrenUserNavs menus:"+this.listByIds(menuIds));
        return convert(menus);
    }

    private List<SysMenuDto> convert(List<SysMenu> menuTree) {
        List<SysMenuDto> menuDtos = new ArrayList<>();

        menuTree.forEach(m -> {
            SysMenuDto dto = new SysMenuDto();

            dto.setId(m.getId());
            dto.setName(m.getPerms());
            dto.setTitle(m.getName());
            dto.setComponent(m.getComponent());
            dto.setPath(m.getPath());

            if (m.getChildren().size() > 0) {

                // 子节点调用当前方法进行再次转换
                dto.setChildren(convert(m.getChildren()));
            }

            menuDtos.add(dto);
        });

        return menuDtos;
    }

    private List<SysMenu> buildTreeMenu(List<SysMenu> menus) {
        List<SysMenu> finalMenus = new ArrayList<>();
        System.out.println("build: "+menus);
        // 先各自寻找到各自的孩子
        for (SysMenu menu : menus) {

            for (SysMenu e : menus) {
                if (menu.getId() == e.getParentId()) {
                    menu.getChildren().add(e);
                }
            }

            // 提取出父节点
            if (menu.getParentId() == 0L) {
                finalMenus.add(menu);
            }
        }

        System.out.println(JSONUtil.toJsonStr(finalMenus));
        return finalMenus;
    }

    @Override
    public List<SysMenu> tree() {
        List<SysMenu> sysMenus = sysMenuService.list();
        return buildTreeMenu(sysMenus);
    }
}
