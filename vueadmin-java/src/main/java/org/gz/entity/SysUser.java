package org.gz.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * <p>
 *
 * </p>
 *
 * @author 我的公众号：GuoZhou
 * @since 2022-10-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUser extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户名不能为空")
    private String username;

    private String password;

    private String avatar;

    @NotBlank(message = "邮箱不能为空")
    private String email;

    private String city;

    private LocalDateTime lastLogin;

    @TableField(exist = false)
    private List<SysRole> sysRoles = new ArrayList<>();

}
