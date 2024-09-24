package io.github.mangocrisp.spring.taybct.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.github.mangocrisp.spring.taybct.api.system.domain.SysNotice;
import io.github.mangocrisp.spring.taybct.api.system.dto.SysNoticeUserDTO;
import io.github.mangocrisp.spring.taybct.api.system.vo.SysNoticeVO;
import io.github.mangocrisp.spring.taybct.tool.core.bean.service.IBaseService;

import java.util.Collection;
import java.util.Map;

/**
 * 针对表【sys_notice(消息通知)】的数据库操作Service
 *
 * @author xijieyin <br> 2022/10/10 15:46
 * @since 1.0.5
 */
public interface ISysNoticeService extends IBaseService<SysNotice> {

    /**
     * 用户消息分页
     *
     * @param sqlQueryParams sql 查询参数
     * @return {@code IPage<SysNoticeVO>}
     * @author xijieyin <br> 2022/10/10 16:49
     * @since 1.0.5
     */
    IPage<SysNoticeVO> userNoticesPage(Map<String, Object> sqlQueryParams);

    /**
     * 添加消息通知
     *
     * @param notice      消息通知
     * @param noticeUsers 消息通知附加的通知对象关系，如果不指定就是通知公告，所有人可见
     * @return boolean
     * @author xijieyin <br> 2022/10/11 14:03
     * @since 1.0.5
     */
    boolean addRelatedNotices(SysNotice notice, Collection<SysNoticeUserDTO> noticeUsers);

    /**
     * 批量更新用户消息
     *
     * @param status    [状态(0不可见 1 已读 2待办)]不能为空
     * @param noticeIds 消息 id
     * @return boolean
     * @author xijieyin <br> 2022/10/11 10:32
     * @since 1.0.5
     */
    boolean updateUserNotices(int status, Collection<Long> noticeIds);

}
