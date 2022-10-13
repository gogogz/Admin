package org.gz.common.sercurity;

import org.gz.entity.SysUser;
import org.gz.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    SysUserService sysUserService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getByUsername(username);
        if(sysUser == null){
            throw new UsernameNotFoundException("用户找不到啦！");
        }
        return new AccountUser(sysUser.getId(), sysUser.getUsername(), sysUser.getPassword(),getUserAuthority(sysUser.getId()));
    }

    public List<GrantedAuthority> getUserAuthority(Long userId) {
        // 通过内置的工具类，把权限字符串封装成GrantedAuthority列表
        return  AuthorityUtils.commaSeparatedStringToAuthorityList(
                sysUserService.getUserAuthorityInfo(userId)
        );
    }
}
