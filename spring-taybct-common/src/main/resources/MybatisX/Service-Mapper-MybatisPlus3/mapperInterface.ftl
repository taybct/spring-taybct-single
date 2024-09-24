package ${mapperInterface.packageName};

import ${tableClass.fullClassName};
<#if tableClass.pkFields??>
    <#list tableClass.pkFields as field><#assign pkName>${field.shortTypeName}</#assign></#list>
</#if>
import io.github.mangocrisp.spring.taybct.tool.core.mybatis.support.SqlPageParams;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
*
*
<pre>
* 针对表【${tableClass.tableName}<#if tableClass.remark?has_content>(${tableClass.remark!})</#if>】的数据库操作Mapper
* </pre>
*
* @author ${author!}
* @since ${.now?string('yyyy-MM-dd HH:mm:ss')}
* @see ${tableClass.fullClassName}
*/
public interface ${mapperInterface.fileName} extends BaseMapper<${tableClass.shortClassName}> {


/**
* 根据条件批量更新
*
* @param bean   需要更新的对象
* @param params 查询条件
* @return 影响的行数
*/
int updateBatchByCondition(@Param("bean") ${tableClass.shortClassName} bean, @Param("params") JSONObject params);

/**
* 查询记录条数
*
* @param params 查询条件
* @return 数量
*/
long total(@Param("params") JSONObject params);

/**
* 分页查询
*
* @param params  查询条件
* @param page 分页条件
* @return 分页数据
*/
List<? extends ${tableClass.shortClassName}> page(@Param("params") JSONObject params, @Param("page") SqlPageParams page);

/**
* 查询单个
*
* @param params  查询条件
* @return 元素标签详情
*/
<E extends ${tableClass.shortClassName}> E detail(@Param("params") JSONObject params);

    }
