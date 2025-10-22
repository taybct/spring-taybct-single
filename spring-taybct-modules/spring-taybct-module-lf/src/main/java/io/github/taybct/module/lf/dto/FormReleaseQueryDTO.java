package io.github.taybct.module.lf.dto;

import com.baomidou.mybatisplus.annotation.SqlCondition;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.github.taybct.tool.core.constant.DateConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表单发布查询 DTO
 *
 * @author XiJieYin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Schema(description = "表单发布查询 DTO")
public class FormReleaseQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7610420451077111257L;

    /**
     * 主键
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 创建人
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createUser;

    /**
     * 创建时间开始
     */
    @DateTimeFormat(pattern = DateConstants.format.YYYY_MM_DD_HH_mm_ss)
    @JsonFormat(pattern = DateConstants.format.YYYY_MM_DD_HH_mm_ss)
    private LocalDateTime createTimeBegin;

    /**
     * 创建时间结束
     */
    @DateTimeFormat(pattern = DateConstants.format.YYYY_MM_DD_HH_mm_ss)
    @JsonFormat(pattern = DateConstants.format.YYYY_MM_DD_HH_mm_ss)
    private LocalDateTime createTimeEnd;

    /**
     * 表单 id
     */
    @Schema(description = "表单 id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long formId;
    /**
     * 发布名称
     */
    @Size(max = 64, message = "编码长度不能超过64")
    @Schema(description = "发布名称")
    @Length(max = 64, message = "编码长度不能超过64")
    private String name;
    /**
     * 备注说明
     */
    @Size(max = 255, message = "编码长度不能超过255")
    @Schema(description = "备注说明")
    @Length(max = 255, message = "编码长度不能超过255")
    @TableField(condition = SqlCondition.LIKE)
    private String description;
    /**
     * 状态(0 关闭 1 打开)
     */
    @Schema(description = "状态(0 关闭 1 打开)")
    private Byte status;
    /**
     * 版本号
     */
    @Schema(description = "版本号")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long version;
    /**
     * 流程类型（字典项 lf_process_type）
     */
    @Size(max = 100, message = "编码长度不能超过100")
    @Schema(description = "流程类型（字典项 lf_process_type）")
    @Length(max = 100, message = "编码长度不能超过100")
    private String type;

    /**
     * 显示最新版本
     */
    @Schema(description = "显示最新版本是否只显示最新版本")
    public boolean showNewVersion;

}
