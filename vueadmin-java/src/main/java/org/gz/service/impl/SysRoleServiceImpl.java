package org.gz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.gz.entity.SysRole;
import org.gz.mapper.SysRoleMapper;
import org.gz.service.SysRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Override
    public List<SysRole> listRolesByUserId(Long id) {
        List<SysRole> sysRoleList = this.list(new QueryWrapper<SysRole>().inSql("id","select role_id from sys_user_role where user_id= " + id));

        return sysRoleList;
    }
}
