package io.github.mangocrisp.spring.taybct.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.mangocrisp.spring.taybct.api.system.domain.SysDept;
import io.github.mangocrisp.spring.taybct.api.system.dto.SysDeptQueryDTO;
import io.github.mangocrisp.spring.taybct.api.system.vo.SysDeptTreeVO;
import io.github.mangocrisp.spring.taybct.tool.core.bean.service.IBaseService;
import io.github.mangocrisp.spring.taybct.tool.core.request.SqlQueryParams;

import java.util.LinkedHashSet;

/**
 * 针对表【sys_dept(部门)】的数据库操作Service
 *
 * @author admin
 * 2023-06-08 14:00:20
 */
public interface ISysDeptService extends IBaseService<SysDept> {

    /**
     * 查询树
     *
     * @param dto 查询参数
     * @return 树结构
     */
    LinkedHashSet<SysDeptTreeVO> tree(SysDeptQueryDTO dto);

    /**
     * 分页查询
     *
     * @param dto        查询参数
     * @param page       分页参数
     * @param pageParams 分页参数
     * @return 分页结果
     */
    IPage<? extends SysDept> page(SysDeptQueryDTO dto, IPage<?> page, SqlQueryParams pageParams);

}
