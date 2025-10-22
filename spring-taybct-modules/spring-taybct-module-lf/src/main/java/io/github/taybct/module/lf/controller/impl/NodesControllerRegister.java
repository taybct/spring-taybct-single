package io.github.taybct.module.lf.controller.impl;

import io.github.taybct.module.lf.controller.INodesController;
import io.github.taybct.module.lf.domain.Nodes;
import io.github.taybct.module.lf.service.INodesService;
import io.github.taybct.tool.core.annotation.WebLog;
import io.github.taybct.tool.core.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author XiJieYin <br> 2023/7/25 16:36
 */
public class NodesControllerRegister implements INodesController {

    @Autowired(required = false)
    protected INodesService nodesService;

    public INodesService getNodesService() {
        return nodesService;
    }

    @WebLog
    @Override
    public R<Nodes> detail(@PathVariable String id) {
        return R.data(getNodesService().getById(id));
    }


}
