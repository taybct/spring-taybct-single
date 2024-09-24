package io.github.mangocrisp.spring.taybct.module.lf.beans;

import io.github.mangocrisp.spring.taybct.module.lf.api.ProcessAutoDealHandler;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Edges;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Nodes;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Process;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * 数据有效性检查
 *
 * @author XiJieYin <br> 2023/7/17 14:03
 */
@AutoConfiguration("processDataCheck")
@Slf4j
public class ProcessDataCheck implements ProcessAutoDealHandler {

    @Override
    public boolean apply(Process process, Edges edges, Nodes nodes) {
        log.info("数据有效性检查");
        return true;
    }

}
