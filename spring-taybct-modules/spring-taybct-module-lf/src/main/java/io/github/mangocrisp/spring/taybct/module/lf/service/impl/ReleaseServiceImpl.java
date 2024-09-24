package io.github.mangocrisp.spring.taybct.module.lf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.mangocrisp.spring.taybct.module.lf.domain.Release;
import io.github.mangocrisp.spring.taybct.module.lf.dto.ReleasePublishDTO;
import io.github.mangocrisp.spring.taybct.module.lf.dto.ReleaseQueryDTO;
import io.github.mangocrisp.spring.taybct.module.lf.enums.DesignPermissionsType;
import io.github.mangocrisp.spring.taybct.module.lf.mapper.ReleaseMapper;
import io.github.mangocrisp.spring.taybct.module.lf.mapper.ReleasePermissionsMapper;
import io.github.mangocrisp.spring.taybct.module.lf.service.IDesignPermissionsService;
import io.github.mangocrisp.spring.taybct.module.lf.service.IReleaseService;
import io.github.mangocrisp.spring.taybct.tool.core.bean.service.BaseServiceImpl;
import io.github.mangocrisp.spring.taybct.tool.core.exception.def.BaseException;
import io.github.mangocrisp.spring.taybct.tool.core.request.SqlQueryParams;
import io.github.mangocrisp.spring.taybct.tool.core.util.MyBatisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * @author admin
 * <br>description 针对表【lf_release(流程发布表)】的数据库操作Service实现
 * @since 2023-07-03 11:31:36
 */
public class ReleaseServiceImpl extends BaseServiceImpl<ReleaseMapper, Release>
        implements IReleaseService {

    @Autowired(required = false)
    protected IDesignPermissionsService designPermissionsService;

    @Autowired(required = false)
    protected ReleasePermissionsMapper releasePermissionsMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publish(ReleasePublishDTO dto) {
        if (!designPermissionsService.checkPermission(Collections.singleton(dto.getDesignId()), new DesignPermissionsType[]{
                DesignPermissionsType.PUBLISH
        })) {
            throw new BaseException("没有发布权限！");
        }
        boolean b = getBaseMapper().publish(Collections.singleton(dto)) > 0;
        if (b) {
            // 发布完，要把权限从设计图复制一份到发行
            if (!(releasePermissionsMapper.publishPermission(IdWorker.getId(), dto.getId(), dto.getDesignId()) > 0)) {
                throw new BaseException("权限保存失败！");
            }
        }
        return b;
    }

    @Override
    public IPage<? extends Release> publishList(ReleaseQueryDTO dto, SqlQueryParams sqlQueryParams) {
        Release release = BeanUtil.copyProperties(dto, Release.class);
        LambdaQueryWrapper<Release> releaseLambdaQueryWrapper = Wrappers.lambdaQuery(release);
        // 这里只查询基础字段，数据字段太长了，需要用查看详情去查询
        releaseLambdaQueryWrapper
                .select(Release::getId
                        , Release::getDescription
                        , Release::getType
                        , Release::getName
                        , Release::getStatus
                        , Release::getVersion
                        , Release::getDesignId
                        , Release::getCreateTime
                        , Release::getCreateUser
                        , Release::getUpdateTime
                        , Release::getUpdateUser);
        releaseLambdaQueryWrapper
                .exists(String.format("select 1 from lf_release_permissions lrp where lrp.release_id = lf_release.id and (user_id = '%s' or user_id is null)"
                        , securityUtil.getLoginUser().getUserId()));
        // 如果是要显示最新的版本，这里接个查询最新版本的条件
        if (dto.isShowNewVersion()) {
            releaseLambdaQueryWrapper.apply("version = (select max(version) from lf_release lri where lri.design_id = lf_release.design_id)");
        }
        // 开始时间范围查询
        if (dto.getCreateTimeBegin() != null) {
            releaseLambdaQueryWrapper.ge(Release::getCreateTime, dto.getCreateTimeBegin());
        }
        if (dto.getCreateTimeEnd() != null) {
            releaseLambdaQueryWrapper.le(Release::getCreateTime, dto.getCreateTimeEnd());
        }
        return getBaseMapper().selectPage(MyBatisUtil.genPage(sqlQueryParams), releaseLambdaQueryWrapper);
    }

    @Override
    public Release detail(Long id) {
        return getBaseMapper().selectByPrimaryKey(id);
    }

}




