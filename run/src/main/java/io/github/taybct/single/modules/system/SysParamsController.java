package io.github.taybct.single.modules.system;

import io.github.taybct.api.system.domain.SysParams;
import io.github.taybct.module.system.controller.impl.SysParamsControllerRegister;
import io.github.taybct.module.system.service.ISysParamsService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统参数相关接口
 *
 * @author xijieyin <br> 2022/8/5 21:31
 * @see SysParams
 * @see ISysParamsService
 * @since 1.0.0
 */
@RestController
public class SysParamsController extends SysParamsControllerRegister {
}
