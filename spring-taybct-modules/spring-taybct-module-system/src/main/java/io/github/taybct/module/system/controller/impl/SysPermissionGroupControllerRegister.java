package io.github.taybct.module.system.controller.impl;

import io.github.taybct.api.system.domain.SysPermissionGroup;
import io.github.taybct.module.system.controller.ISysPermissionGroupController;
import io.github.taybct.module.system.service.ISysPermissionGroupService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 权限分组接口
 *
 * @author xijieyin <br> 2022/9/23 9:35
 * @see SysPermissionGroup
 * @see ISysPermissionGroupService
 * @since 1.0.4
 */
public class SysPermissionGroupControllerRegister implements ISysPermissionGroupController {

    @Autowired(required = false)
    protected ISysPermissionGroupService sysPermissionGroupService;

    @Override
    public ISysPermissionGroupService getBaseService() {
        return sysPermissionGroupService;
    }
}
