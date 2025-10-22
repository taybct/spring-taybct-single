package io.github.taybct.single.modules.lf.controller;

import io.github.taybct.module.lf.controller.impl.NodesControllerRegister;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author XiJieYin <br> 2023/7/25 16:36
 */
@RestController
@RequestMapping("/lf/v1/nodes")
public class NodesController extends NodesControllerRegister {
}
