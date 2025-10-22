package io.github.taybct.single.modules.system;

import io.github.taybct.api.system.domain.SysDict;
import io.github.taybct.module.system.controller.impl.SysDictControllerRegister;
import io.github.taybct.module.system.service.ISysDictService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统字典相关接口
 *
 * @author xijieyin <br> 2022/8/5 21:26
 * @see SysDict
 * @see ISysDictService
 * @since 1.0.0
 */
@RestController
public class SysDictController extends SysDictControllerRegister {
}
