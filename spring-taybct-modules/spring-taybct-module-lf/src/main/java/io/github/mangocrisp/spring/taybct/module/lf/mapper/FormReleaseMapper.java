package io.github.mangocrisp.spring.taybct.module.lf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.mangocrisp.spring.taybct.module.lf.domain.FormRelease;
import io.github.mangocrisp.spring.taybct.module.lf.dto.FormReleasePublishDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;

/**
 * @author admin
 * <br>description 针对表【lf_form_release(流程表单发布表)】的数据库操作Mapper
 * @see FormRelease
 * @since 2023-07-21 15:18:29
 */
public interface FormReleaseMapper extends BaseMapper<FormRelease> {

    /**
     * 发布流程图
     *
     * @param dto 数据
     * @return 影响行数
     */
    int publish(@Param("dto") Collection<FormReleasePublishDTO> dto);

}




