package io.github.taybct.module.od.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.github.taybct.module.od.domain.OnlineDocPermit;
import io.github.taybct.module.od.mapper.OnlineDocPermitMapper;
import io.github.taybct.module.od.service.IOnlineDocPermitService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Service;

/**
 * <pre>
 * 在线文档操作权限 Service 实现
 * </pre>
 *
 * @author XiJieYin
 * @since 2025/9/20 03:26
 */
@AutoConfiguration
@Service
public class OnlineDocPermitServiceImpl extends ServiceImpl<OnlineDocPermitMapper, OnlineDocPermit> implements IOnlineDocPermitService {
}
