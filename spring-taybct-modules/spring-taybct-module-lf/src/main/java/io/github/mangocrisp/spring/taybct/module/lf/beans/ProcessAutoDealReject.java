package io.github.mangocrisp.spring.taybct.module.lf.beans;

import io.github.mangocrisp.spring.taybct.module.lf.api.ProcessAutoDealHandler;
import io.github.mangocrisp.spring.taybct.module.lf.constants.TodoListStatus;
import io.github.mangocrisp.spring.taybct.module.lf.domain.*;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Process;
import io.github.mangocrisp.spring.taybct.tool.core.bean.ISecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * <pre>
 * 驳回 beans
 * </pre>
 *
 * @author XiJieYin
 * @since 2025/9/12 17:56
 */
@AutoConfiguration("flowProcessAutoDealReject")
@Slf4j
@RequiredArgsConstructor
public class ProcessAutoDealReject implements ProcessAutoDealHandler {



    @Override
    public boolean apply(History history, Process process, Edges edges, Nodes nodes) {
        Todo e = new Todo();
        e.setNodeId(nodes.getId());
//        e.setUserId(loginUser.getUserId());
        // 设置当前用户处理已办
        e.setStatus(TodoListStatus.DONE);
        e.setProcessId(process.getId());
        e.setType(process.getType());
        e.setDesignId(process.getDesignId());
//        todoService.save(e);
        return false;
    }
}
