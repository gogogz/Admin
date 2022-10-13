package org.gz.controller;

import cn.hutool.core.map.MapUtil;
import org.gz.common.lang.Result;
import org.gz.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TesController {
    @Autowired
    SysUserService userService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @GetMapping("/test")
    @PreAuthorize("hasRole('admin')")
    public Object test() {
        Result result = Result.succ(userService.list());
        return result;
    }
    @GetMapping("/test/pass")
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result passEncode(){
        String pass = bCryptPasswordEncoder.encode("111111");

        // 密码验证
        boolean matches = bCryptPasswordEncoder.matches("111111", pass);

        return Result.succ(MapUtil.builder()
                .put("pass", pass)
                .put("marches", matches)
                .build()
        );
    }
}
