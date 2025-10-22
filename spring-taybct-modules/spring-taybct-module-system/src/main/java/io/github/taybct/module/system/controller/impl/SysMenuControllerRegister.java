package io.github.taybct.module.system.controller.impl;

import io.github.taybct.api.system.domain.SysMenu;
import io.github.taybct.module.system.controller.ISysMenuController;
import io.github.taybct.module.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 系统菜单相关接口
 *
 * @author xijieyin <br> 2022/8/5 21:03
 * @see SysMenu
 * @see ISysMenuService
 * @since 1.0.0
 */
public class SysMenuControllerRegister implements ISysMenuController {

    @Autowired(required = false)
    protected ISysMenuService sysMenuService;

    @Override
    public ISysMenuService getBaseService() {
        return sysMenuService;
    }
}
