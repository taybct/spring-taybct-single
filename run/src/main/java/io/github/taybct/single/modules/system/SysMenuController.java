package io.github.taybct.single.modules.system;

import io.github.taybct.api.system.domain.SysMenu;
import io.github.taybct.module.system.controller.impl.SysMenuControllerRegister;
import io.github.taybct.module.system.service.ISysMenuService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统菜单相关接口
 *
 * @author xijieyin <br> 2022/8/5 21:03
 * @see SysMenu
 * @see ISysMenuService
 * @since 1.0.0
 */
@RestController
public class SysMenuController extends SysMenuControllerRegister {
}
