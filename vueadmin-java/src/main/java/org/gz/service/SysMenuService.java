package org.gz.service;

import org.gz.common.dto.SysMenuDto;
import org.gz.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 我的公众号：GuoZhou
 * @since 2022-10-04
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenuDto> getCurrenUserNavs();

    List<SysMenu> tree();
}
