package io.github.mangocrisp.spring.taybct.module.lf.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程列表查询 VO 对象
 *
 * @author XiJieYin <br> 2023/7/17 10:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "流程列表查询 VO 对象")
public class ProcessListVO implements Serializable {

    private static final long serialVersionUID = -2494815245297446809L;

    /**
     * 待办 id
     */
    @Schema(description = "待办 id")
    private Long todoId;
    /**
     * 流程发布 id（可以知道当前流程是基于什么版本的设计在运行的）
     */
    @Schema(description = "流程发布 id（可以知道当前流程是基于什么版本的设计在运行的）")
    private Long releaseId;
    /**
     * 运行流程 id
     */
    @Schema(description = "运行流程 id")
    private Long processId;
    /**
     * 流程标题
     */
    @Schema(description = "流程标题")
    private String title;
    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String createUser;
    /**
     * 创建人姓名
     */
    @Schema(description = "创建人姓名")
    private String createUserName;
    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    /**
     * 所属流程图 id
     */
    @Schema(description = "流程图 id（可以知道当前流程是基于什么原始设计运行的）")
    private Long designId;
    /**
     * 所属流程名称
     */
    @Schema(description = "所属流程名称")
    private String designName;
    /**
     * 当前节点 id
     */
    @Schema(description = "当前节点 id")
    private String nodeId;
    /**
     * 当前节点标题
     */
    @Schema(description = "当前节点标题")
    private String nodeText;

}
