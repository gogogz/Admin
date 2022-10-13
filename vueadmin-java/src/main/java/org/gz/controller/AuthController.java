package org.gz.controller;

import cn.hutool.core.map.MapUtil;
import com.google.code.kaptcha.Producer;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.gz.common.lang.Const;
import org.gz.common.lang.Result;
import org.gz.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.UUID;

@RestController
@Slf4j
public class AuthController extends BaseController{
    @Autowired
    Producer producer;
    @GetMapping("/captcha")
    public Result captcha() throws IOException {
        String code = producer.createText();
        String key = UUID.randomUUID().toString();
        BufferedImage image = producer.createImage(code);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        BASE64Encoder encoder = new BASE64Encoder();
        String str = "data:image/jpeg;base64,";
        String base64Img = str + encoder.encode(outputStream.toByteArray());

        // 存储到redis中
        redisUtil.hset(Const.CAPTCHA_KEY, key, code, 120);
        log.info("验证码 -- {} - {}", key, code);
        return Result.succ(
                MapUtil.builder()
                        .put("token", key)
                        .put("base64Img", base64Img)
                        .build()
        );
    }
    @GetMapping("/sys/userinfo")
    public Result getUserInfo(Principal principal){
        SysUser sysUser = sysUserService.getByUsername(principal.getName());
        return  Result.succ(MapUtil.builder()
                .put("id",sysUser.getId())
                .put("username",sysUser.getUsername())
                .put("avatar",sysUser.getAvatar())
                .map());
    }
}
