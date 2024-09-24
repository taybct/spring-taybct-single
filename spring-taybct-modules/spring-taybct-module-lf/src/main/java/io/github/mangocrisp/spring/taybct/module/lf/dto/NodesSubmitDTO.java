package io.github.mangocrisp.spring.taybct.module.lf.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Edges;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Nodes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 流程节点提交 DTO
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "lf_nodes")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "流程节点提交 DTO")
public class NodesSubmitDTO extends Nodes {

    @TableField(exist = false)
    private static final long serialVersionUID = -7736973053195778901L;

    /**
     * 待办 id
     */
    @Schema(description = "待办 id")
    private Long todoId;
    /**
     * 哪个节点连接的当前节点
     */
    @Schema(description = "哪个线连接的当前节点")
    private Edges edges;
    /**
     * 上一个节点的 id
     */
    @Schema(description = "上一个节点的 id")
    private String lastNodesId;
    /**
     * 处理的部门
     */
    @Schema(description = "处理的部门")
    private Long deptId;
    /**
     * 处理的岗位
     */
    @Schema(description = "处理的岗位")
    private Long postId;

}
