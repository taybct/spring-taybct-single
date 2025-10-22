package io.github.taybct.module.system.controller.impl;

import io.github.taybct.module.system.controller.ISysNoticeController;
import io.github.taybct.module.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 消息通知相关接口
 *
 * @author xijieyin <br> 2022/10/10 15:32
 * @since 1.0.5
 */
public class SysNoticeControllerRegister implements ISysNoticeController {

    @Autowired(required = false)
    protected ISysNoticeService sysNoticeService;

    @Override
    public ISysNoticeService getBaseService() {
        return sysNoticeService;
    }
}
