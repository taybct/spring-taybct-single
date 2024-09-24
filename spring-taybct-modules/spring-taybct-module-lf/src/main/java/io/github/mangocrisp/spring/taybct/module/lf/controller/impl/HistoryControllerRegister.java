package io.github.mangocrisp.spring.taybct.module.lf.controller.impl;

import io.github.mangocrisp.spring.taybct.module.lf.controller.IHistoryController;
import io.github.mangocrisp.spring.taybct.module.lf.dto.HistoryListQueryDTO;
import io.github.mangocrisp.spring.taybct.module.lf.service.IHistoryService;
import io.github.mangocrisp.spring.taybct.module.lf.vo.HistoryListVO;
import io.github.mangocrisp.spring.taybct.tool.core.result.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author XiJieYin <br> 2023/7/25 16:36
 */
public class HistoryControllerRegister implements IHistoryController {
    @Autowired(required = false)
    protected IHistoryService historyService;

    public IHistoryService getHistoryService() {
        return historyService;
    }

    @Override
    public R<List<HistoryListVO>> historyList(@RequestBody HistoryListQueryDTO dto) {
        return R.data(getHistoryService().historyList(dto));
    }

}
