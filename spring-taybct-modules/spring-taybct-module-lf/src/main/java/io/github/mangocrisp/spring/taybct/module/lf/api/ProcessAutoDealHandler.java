package io.github.mangocrisp.spring.taybct.module.lf.api;

import io.github.mangocrisp.spring.taybct.module.lf.domain.Edges;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Nodes;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Process;

/**
 * 流程自动处理类，传流程信息和连线及节点信息，自动处理业务，返回是否处理成功
 *
 * @author XiJieYin <br> 2023/7/13 14:40
 */
public interface ProcessAutoDealHandler {

    /**
     * 处理业务
     *
     * @param process 流程信息
     * @param edges   连线信息
     * @param nodes   连线连到的节点信息
     * @return boolean
     */
    boolean apply(Process process, Edges edges, Nodes nodes);
}
