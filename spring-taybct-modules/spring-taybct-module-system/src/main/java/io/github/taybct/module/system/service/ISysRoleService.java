package io.github.taybct.module.system.service;

import io.github.taybct.api.system.domain.SysRole;
import io.github.taybct.tool.core.bean.service.IBaseService;

import java.util.List;

/**
 * 角色表操作的 service
 *
 * @author xijieyin <br> 2022/8/5 22:09
 * @see SysRole
 * @since 1.0.0
 */
public interface ISysRoleService extends IBaseService<SysRole> {
    /**
     * 取角色列表（过滤出只有当前登录角色拥有的）
     *
     * @param dto {@literal 请求参数}
     * @return 过滤后的角色列表
     */
    List<SysRole> listFilterRole(SysRole dto);
}
