package io.github.mangocrisp.spring.taybct.module.lf.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * 历史记录操作者
 *
 * @author XiJieYin <br> 2023/7/19 12:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "历史记录操作者")
@Builder
public class HistoryOperator implements Serializable {
    private static final long serialVersionUID = -647879019573889339L;
    /**
     * 流程发起人 id
     */
    @Schema(description = "流程发起人 id")
    private Long userId;
    /**
     * 发起部门
     */
    @Schema(description = "发起部门")
    private Long deptId;
    /**
     * 岗位
     */
    @Schema(description = "岗位")
    private Long postId;
}
