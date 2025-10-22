package io.github.taybct.single.modules.system;

import io.github.taybct.api.system.domain.SysUser;
import io.github.taybct.module.system.controller.impl.SysUserControllerRegister;
import io.github.taybct.module.system.service.ISysUserService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统用户相关接口
 *
 * @author xijieyin <br> 2022/8/5 21:32
 * @see SysUser
 * @see ISysUserService
 * @since 1.0.0
 */
@RestController
public class SysUserController extends SysUserControllerRegister {
}
