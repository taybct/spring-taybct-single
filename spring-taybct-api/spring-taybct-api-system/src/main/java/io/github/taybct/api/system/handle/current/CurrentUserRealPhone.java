package io.github.taybct.api.system.handle.current;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.taybct.api.system.domain.SysUser;
import io.github.taybct.api.system.mapper.SysUserMapper;
import io.github.taybct.tool.core.bean.ILoginUser;
import io.github.taybct.tool.core.bean.ISecurityUtil;
import io.github.taybct.tool.core.config.TableFieldDefaultHandler;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 * 当前登录用户的手机号
 * </pre>
 *
 * @author xijieyin
 * @since 2025/9/20 00:27
 */
@RequiredArgsConstructor
public class CurrentUserRealPhone implements TableFieldDefaultHandler<String> {


    final SysUserMapper sysUserMapper;

    final ISecurityUtil securityUtil;

    @Override
    public String get(Object entity) {
        ILoginUser loginUser = securityUtil.getLoginUser();
        SysUser sysUser = sysUserMapper.selectOne(Wrappers.<SysUser>lambdaQuery()
                .select(SysUser::getId, SysUser::getPhone)
                .eq(SysUser::getId, loginUser.getUserId()));
        return sysUser.getPhone();
    }

}
