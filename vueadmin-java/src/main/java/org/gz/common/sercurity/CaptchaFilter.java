package org.gz.common.sercurity;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.gz.common.exception.CaptchaException;
import org.gz.common.lang.Const;
import org.gz.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CaptchaFilter extends OncePerRequestFilter {
    private final String loginUrl = "/login";
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    LoginFailureHandler loginFailureHandler;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String url = request.getRequestURI();
        if (loginUrl.equals(url) && request.getMethod().equals("POST")) {
            log.info("获取到login链接，正在校验验证码 -- " + url);
            try {
                validate(request);
            } catch (CaptchaException e) {
                log.info(e.getMessage());
                // 交给登录失败处理器处理
                loginFailureHandler.onAuthenticationFailure(request, response, e);
            }
        }
        filterChain.doFilter(request, response);
    }
    private void validate(HttpServletRequest request) {
        String code = request.getParameter("code");
        String token = request.getParameter("token");
        if (StringUtils.isBlank(code) || StringUtils.isBlank(token)) {
            throw new CaptchaException("验证码不能为空");
        }
        if(!code.equals(redisUtil.hget(Const.CAPTCHA_KEY, token))) {
            throw new CaptchaException("验证码不正确");
        }
        // 一次性使用
        redisUtil.hdel(Const.CAPTCHA_KEY, token);
    }
}
