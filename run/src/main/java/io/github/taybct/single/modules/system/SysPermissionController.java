package io.github.taybct.single.modules.system;

import io.github.taybct.api.system.domain.SysPermission;
import io.github.taybct.module.system.controller.impl.SysPermissionControllerRegister;
import io.github.taybct.module.system.service.ISysPermissionService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单权限相关接口
 *
 * @author xijieyin <br> 2022/8/5 21:19
 * @see SysPermission
 * @see ISysPermissionService
 * @since 1.0.0
 */
@RestController
public class SysPermissionController extends SysPermissionControllerRegister {
}
