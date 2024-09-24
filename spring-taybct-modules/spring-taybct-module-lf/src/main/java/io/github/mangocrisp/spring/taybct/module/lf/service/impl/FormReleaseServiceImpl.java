package io.github.mangocrisp.spring.taybct.module.lf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.mangocrisp.spring.taybct.module.lf.domain.FormRelease;
import io.github.mangocrisp.spring.taybct.module.lf.dto.FormReleasePublishDTO;
import io.github.mangocrisp.spring.taybct.module.lf.dto.FormReleaseQueryDTO;
import io.github.mangocrisp.spring.taybct.module.lf.mapper.FormReleaseMapper;
import io.github.mangocrisp.spring.taybct.module.lf.service.IFormReleaseService;
import io.github.mangocrisp.spring.taybct.tool.core.bean.service.BaseServiceImpl;
import io.github.mangocrisp.spring.taybct.tool.core.request.SqlQueryParams;
import io.github.mangocrisp.spring.taybct.tool.core.util.MyBatisUtil;

import java.util.Collections;

/**
 * @author admin
 * <br>description 针对表【lf_form_release(流程表单发布表)】的数据库操作Service实现
 * @since 2023-07-21 15:18:29
 */
public class FormReleaseServiceImpl extends BaseServiceImpl<FormReleaseMapper, FormRelease>
        implements IFormReleaseService {

    @Override
    public boolean publish(FormReleasePublishDTO dto) {
        return getBaseMapper().publish(Collections.singleton(dto)) > 0;
    }

    @Override
    public IPage<? extends FormRelease> publishList(FormReleaseQueryDTO dto, SqlQueryParams sqlQueryParams) {
        FormRelease release = BeanUtil.copyProperties(dto, FormRelease.class);
        LambdaQueryWrapper<FormRelease> releaseLambdaQueryWrapper = Wrappers.lambdaQuery(release);
        // 这里只查询基础字段，数据字段太长了，需要用查看详情去查询
        releaseLambdaQueryWrapper
                .select(FormRelease::getId
                        , FormRelease::getDescription
                        , FormRelease::getType
                        , FormRelease::getPath
                        , FormRelease::getName
                        , FormRelease::getStatus
                        , FormRelease::getVersion
                        , FormRelease::getFormId
                        , FormRelease::getCreateTime
                        , FormRelease::getCreateUser
                        , FormRelease::getUpdateTime
                        , FormRelease::getUpdateUser);
        // 如果是要显示最新的版本，这里接个查询最新版本的条件
        if (dto.isShowNewVersion()) {
            releaseLambdaQueryWrapper.apply("version = (select max(version) from lf_form_release lfr where lfr.form_id = lf_form_release.form_id)");
        }
        // 开始时间范围查询
        if (dto.getCreateTimeBegin() != null) {
            releaseLambdaQueryWrapper.ge(FormRelease::getCreateTime, dto.getCreateTimeBegin());
        }
        if (dto.getCreateTimeEnd() != null) {
            releaseLambdaQueryWrapper.le(FormRelease::getCreateTime, dto.getCreateTimeEnd());
        }
        return getBaseMapper().selectPage(MyBatisUtil.genPage(sqlQueryParams), releaseLambdaQueryWrapper);
    }
}




