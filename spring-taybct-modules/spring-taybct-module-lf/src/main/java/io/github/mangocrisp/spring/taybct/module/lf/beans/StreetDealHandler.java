package io.github.mangocrisp.spring.taybct.module.lf.beans;

import cn.hutool.core.convert.Convert;
import cn.hutool.extra.expression.engine.spel.SpELEngine;
import io.github.mangocrisp.spring.taybct.module.lf.api.ProcessAutoDealHandler;
import io.github.mangocrisp.spring.taybct.module.lf.constants.TodoListStatus;
import io.github.mangocrisp.spring.taybct.module.lf.constants.TodoStatus;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Edges;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Nodes;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Process;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Todo;
import io.github.mangocrisp.spring.taybct.module.lf.service.IEdgesService;
import io.github.mangocrisp.spring.taybct.module.lf.service.INodesService;
import io.github.mangocrisp.spring.taybct.module.lf.service.ITodoService;
import io.github.mangocrisp.spring.taybct.module.lf.util.ProcessUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * 街道用户处理，用于指定下一个节点，该有哪些用户去处理问题
 *
 * @author XiJieYin <br> 2023/7/17 15:09
 */
@AutoConfiguration("streetDealHandler")
@Slf4j
@RequiredArgsConstructor
public class StreetDealHandler implements ProcessAutoDealHandler {

    final IEdgesService edgesService;
    final INodesService nodesService;
    final ITodoService todoService;

    @Override
    public boolean apply(Process process, Edges edges, Nodes nodes) {
        Map<String, Object> processFormData = ProcessUtil.getProcessFormData(process);
        // 街道处理人
        Object streetPerson = new SpELEngine().eval("#node_c0cf40b2731a49f7af07e1bbb41f59f3_streetPerson", processFormData, new LinkedHashSet<>());
        Todo e = new Todo();
        // 因为用户节点和系统节点都是只能连出一条线的，所以这里可以直接拿第一个线就是了
        List<Edges> edgesList = edgesService.selectBySourceId(nodes.getId());
        // 再根据这条线的目标节点id查询到的节点就是下一个节点
        Nodes nextNodes = nodesService.getById(edgesList.get(0).getTargetNodeId());
        e.setNodeId(nextNodes.getId());
        e.setUserId(Convert.toLong(streetPerson));
        e.setStatus(TodoListStatus.TODO);
        e.setTodoStatus(TodoStatus.TODO);
        e.setProcessId(process.getId());
        e.setType(process.getType());
        e.setDesignId(process.getDesignId());
        return todoService.save(e);
    }
}
