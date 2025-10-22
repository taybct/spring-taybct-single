package io.github.taybct.single.modules.system;

import io.github.taybct.api.system.domain.SysDictType;
import io.github.taybct.module.system.controller.impl.SysDictTypeControllerRegister;
import io.github.taybct.module.system.service.ISysDictTypeService;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统字典类型相关接口
 *
 * @author xijieyin <br> 2022/8/5 21:28
 * @see SysDictType
 * @see ISysDictTypeService
 * @since 1.0.0
 */
@RestController
public class SysDictTypeController extends SysDictTypeControllerRegister {
}
