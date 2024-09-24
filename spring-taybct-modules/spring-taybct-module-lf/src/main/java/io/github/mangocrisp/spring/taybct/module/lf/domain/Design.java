package io.github.mangocrisp.spring.taybct.module.lf.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.github.mangocrisp.spring.taybct.module.lf.constants.ProcessType;
import io.github.mangocrisp.spring.taybct.tool.core.annotation.TableFieldDefault;
import io.github.mangocrisp.spring.taybct.tool.core.annotation.TableFieldJSON;
import io.github.mangocrisp.spring.taybct.tool.core.bean.DeleteLogicEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 流程图设计
 * lf_design
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "lf_design")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "流程图设计")
public class Design extends DeleteLogicEntity<Long, Long> {

    @TableField(exist = false)
    private static final long serialVersionUID = 2340545827439654755L;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 状态(0未发布，1已经发布)
     */
    @Schema(description = "状态(0未发布，1已经发布)")
    @TableFieldDefault("0")
    private Byte status;

    /**
     * 备注说明
     */
    @Schema(description = "备注说明")
    private String description;

    /**
     * 数据
     */
    @Schema(description = "数据")
    @TableFieldJSON
    private Object data;

    /**
     * 流程类型（字典项 lf_process_type）
     */
    @Schema(description = "流程类型（字典项 lf_process_type）")
    @TableFieldDefault(ProcessType.NORMAL)
    private String type;

}