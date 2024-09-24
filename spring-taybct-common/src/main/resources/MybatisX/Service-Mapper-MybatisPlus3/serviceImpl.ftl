package ${baseInfo.packageName};

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${tableClass.fullClassName};
import ${serviceInterface.packageName}.${serviceInterface.fileName};
import ${mapperInterface.packageName}.${mapperInterface.fileName};
<#if baseService??&&baseService!="">
    import ${baseService};
    <#list baseService?split(".") as simpleName>
        <#if !simpleName_has_next>
            <#assign serviceSimpleName>${simpleName}</#assign>
        </#if>
    </#list>
</#if>
import org.springframework.stereotype.Service;
import io.github.mangocrisp.spring.taybct.tool.core.mybatis.support.SqlPageParams;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import io.github.mangocrisp.spring.taybct.tool.core.bean.ModelConvertible;
import io.github.mangocrisp.spring.taybct.tool.core.bean.UpdateModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
*
*
<pre>
* 针对表【${tableClass.tableName}<#if tableClass.remark?has_content>(${tableClass.remark!})</#if>】的数据库操作Service实现
* </pre>
*
* @author ${author!}
* @since ${.now?string('yyyy-MM-dd HH:mm:ss')}
* @see ${tableClass.fullClassName}
*/
@AutoConfiguration
@Service
public class ${baseInfo.fileName} extends ServiceImpl<${mapperInterface.fileName}, ${tableClass.shortClassName}>
implements ${serviceInterface.fileName}{


@Override
@Transactional(rollbackFor = Throwable.class)
public
<UM extends
    ModelConvertible<? extends ${tableClass.shortClassName}>, QM extends ModelConvertible<? extends ${tableClass.shortClassName}>> boolean update(UpdateModel<${tableClass.shortClassName}, UM, QM> model) {
model.getUpdateList().forEach(condition-> getBaseMapper().updateBatchByCondition(condition.getBean(), condition.getParams()));
return true;
}

@Override
public
<E extends ${tableClass.shortClassName}> IPage
    <E> page(JSONObject params, SqlPageParams sqlPageParams) {
        IPage
        <E> page = sqlPageParams.genPage();
            long total = getBaseMapper().total(params);
            page.setTotal(total);
            if (total > 0) {
            List<? extends ${tableClass.shortClassName}> list = getBaseMapper().page(params, sqlPageParams);
            page.setRecords(list.stream().map(e -> (E) e).toList());
            }
            return page;
            }

            @Override
            public
            <E extends ${tableClass.shortClassName}> List
                <E> list(JSONObject params, SqlPageParams sqlPageParams){
                    return getBaseMapper().page(params, sqlPageParams).stream().map(e -> (E) e).toList();
                    }

                    @Override
                    public
                    <E extends ${tableClass.shortClassName}> E detail(JSONObject params) {
                        return getBaseMapper().detail(params);
                        }

                        }
