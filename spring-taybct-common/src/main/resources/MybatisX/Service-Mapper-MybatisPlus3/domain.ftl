package ${domain.packageName};

import jakarta.validation.constraints.NotBlank;
import io.github.mangocrisp.spring.taybct.tool.core.constant.DateConstants;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
<#list tableClass.importList as fieldType>${"\n"}import ${fieldType};</#list>
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Length;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.*;
import java.io.Serial;

/**
*
*
<pre>
* ${tableClass.remark!}
* TableName: ${tableClass.tableName}
* </pre>
*
* @author ${author!}
* @since ${.now?string('yyyy-MM-dd HH:mm:ss')}
*/
@TableName(value = "${tableClass.tableName}")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "${tableClass.remark!}")
public class ${tableClass.shortClassName} implements Serializable {

@TableField(exist = false)
@Serial
private static final long serialVersionUID = 1L;

<#list tableClass.pkFields as field>
    /**
    * ${field.remark!}
    */<#if !field.nullable || field.jdbcType=="VARCHAR">${"\n    "}</#if><#if !field.nullable><#if field.jdbcType=="VARCHAR">@NotBlank(message="[${field.remark!}]不能为空")<#else>@NotNull(message="[${field.remark!}]不能为空")</#if></#if><#if field.jdbcType=="VARCHAR"><#if !field.nullable>${"\n    "}</#if>@Size(max= ${field.columnLength?c},message="编码长度不能超过${field.columnLength?c}")</#if>
    @Schema(description = "${field.remark!}")<#if field.jdbcType=="VARCHAR">${"\n    "}@Length(max= ${field.columnLength?c},message="编码长度不能超过${field.columnLength?c}")</#if>
    @TableId(value = "${field.columnName}")<#if field.jdbcType=="BIGINT">${"\n    "}@JsonSerialize(using = ToStringSerializer.class)</#if>
    private ${field.shortTypeName} ${field.fieldName};
</#list>

<#list tableClass.baseBlobFields as field>
    /**
    * ${field.remark!}
    */<#if !field.nullable || field.jdbcType=="VARCHAR">${"\n    "}</#if><#if !field.nullable><#if field.jdbcType=="VARCHAR">@NotBlank(message="[${field.remark!}]不能为空")<#else>@NotNull(message="[${field.remark!}]不能为空")</#if></#if><#if field.jdbcType=="VARCHAR"><#if !field.nullable>${"\n    "}</#if>@Size(max= ${field.columnLength?c},message="编码长度不能超过${field.columnLength?c}")</#if>
    @Schema(description = "${field.remark!}")<#if field.jdbcType=="VARCHAR">${"\n    "}@Length(max= ${field.columnLength?c},message="编码长度不能超过${field.columnLength?c}")</#if>
    @TableField(value = "${field.columnName}")<#if field.jdbcType=="TIMESTAMP">${"\n    "}@DateTimeFormat(pattern = DateConstants.format.YYYY_MM_DD_HH_mm_ss)${"\n    "}@JsonFormat(pattern = DateConstants.format.YYYY_MM_DD_HH_mm_ss)</#if><#if field.jdbcType=="BIGINT">${"\n    "}@JsonSerialize(using = ToStringSerializer.class)</#if>
    private ${field.shortTypeName} ${field.fieldName};
</#list>

}
