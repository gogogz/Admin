package org.gz.service;

import org.gz.entity.SysRole;
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
public interface SysRoleService extends IService<SysRole> {

    List<SysRole> listRolesByUserId(Long id);
}
