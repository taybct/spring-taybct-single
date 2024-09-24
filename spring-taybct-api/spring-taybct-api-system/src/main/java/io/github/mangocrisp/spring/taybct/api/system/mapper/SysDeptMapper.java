package io.github.mangocrisp.spring.taybct.api.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.mangocrisp.spring.taybct.api.system.domain.SysDept;
import io.github.mangocrisp.spring.taybct.api.system.dto.SysDeptQueryDTO;
import io.github.mangocrisp.spring.taybct.api.system.vo.SysDeptTreeVO;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 针对表【sys_dept(部门)】的数据库操作Mapper
 *
 * @author admin
 * 2023-06-08 14:00:20
 * @see SysDept
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {
    /**
     * 查询部门树
     *
     * @param dto         查询参数
     * @param operator    操作用户 id
     * @param authorities 用户权限
     * @param isRoot      是否是 ROOT 角色
     * @return 部门树
     */
    LinkedHashSet<SysDeptTreeVO> tree(@Param("dto") SysDeptQueryDTO dto
            , @Param("operator") Serializable operator
            , @Param("authorities") Set<String> authorities
            , @Param("isRoot") Integer isRoot);

    /**
     * 查询分页
     *
     * @param page        分页参数
     * @param dto         查询参数
     * @param authorities 用户权限
     * @param isRoot      是否是 ROOT 角色
     * @param pageOrder   排序字段
     * @return 分页查询的列表
     */
    IPage<SysDept> deptFilterPage(IPage<?> page
            , @Param("pageOrder") String pageOrder
            , @Param("dto") SysDeptQueryDTO dto
            , @Param("operator") Serializable operator
            , @Param("authorities") Set<String> authorities
            , @Param("isRoot") Integer isRoot);

}
