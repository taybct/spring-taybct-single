package io.github.taybct.single.modules.lf.controller;

import io.github.taybct.module.lf.controller.impl.ProcessControllerRegister;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XiJieYin <br> 2023/7/25 16:36
 */
@RestController
@RequestMapping("/lf/v1/process")
public class ProcessController extends ProcessControllerRegister {
}
