package io.github.mangocrisp.spring.taybct.module.system.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.mangocrisp.spring.taybct.api.system.domain.SysDept;
import io.github.mangocrisp.spring.taybct.api.system.dto.SysDeptQueryDTO;
import io.github.mangocrisp.spring.taybct.api.system.mapper.SysDeptMapper;
import io.github.mangocrisp.spring.taybct.api.system.vo.SysDeptTreeVO;
import io.github.mangocrisp.spring.taybct.module.system.service.ISysDeptService;
import io.github.mangocrisp.spring.taybct.tool.core.bean.ILoginUser;
import io.github.mangocrisp.spring.taybct.tool.core.bean.service.BaseServiceImpl;
import io.github.mangocrisp.spring.taybct.tool.core.request.SqlQueryParams;
import io.github.mangocrisp.spring.taybct.tool.core.util.MyBatisUtil;

import java.util.LinkedHashSet;

/**
 * 针对表【sys_dept(部门)】的数据库操作Service实现
 *
 * @author admin 2023-06-08 14:00:20
 */
public class SysDeptServiceImpl extends BaseServiceImpl<SysDeptMapper, SysDept>
        implements ISysDeptService {

    @Override
    public LinkedHashSet<SysDeptTreeVO> tree(SysDeptQueryDTO dto) {
        ILoginUser loginUser = securityUtil.getLoginUser();
        return getBaseMapper().tree(dto
                , loginUser.getUserId()
                , loginUser.checkAuthorities()
                , loginUser.checkRoot());
    }

    @Override
    public IPage<? extends SysDept> page(SysDeptQueryDTO dto, IPage<?> page, SqlQueryParams pageParams) {
        ILoginUser loginUser = securityUtil.getLoginUser();
        // 排序字段
        String pageOrder = MyBatisUtil.getPageOrder(pageParams);
        return getBaseMapper().deptFilterPage(page
                , pageOrder
                , dto
                , loginUser.getUserId()
                , loginUser.checkAuthorities()
                , loginUser.checkRoot());
    }

}
