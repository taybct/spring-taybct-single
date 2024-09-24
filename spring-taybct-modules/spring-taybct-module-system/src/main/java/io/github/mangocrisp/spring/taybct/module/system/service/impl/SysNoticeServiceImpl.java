package io.github.mangocrisp.spring.taybct.module.system.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.github.mangocrisp.spring.taybct.api.system.domain.SysNotice;
import io.github.mangocrisp.spring.taybct.api.system.domain.SysNoticeUser;
import io.github.mangocrisp.spring.taybct.api.system.dto.SysNoticeUserDTO;
import io.github.mangocrisp.spring.taybct.api.system.mapper.SysNoticeMapper;
import io.github.mangocrisp.spring.taybct.api.system.vo.SysNoticeVO;
import io.github.mangocrisp.spring.taybct.common.constants.SysDictConstants;
import io.github.mangocrisp.spring.taybct.common.dict.SysDict;
import io.github.mangocrisp.spring.taybct.module.system.service.ISysDictService;
import io.github.mangocrisp.spring.taybct.module.system.service.ISysNoticeService;
import io.github.mangocrisp.spring.taybct.module.system.service.ISysNoticeUserService;
import io.github.mangocrisp.spring.taybct.tool.core.bean.ILoginUser;
import io.github.mangocrisp.spring.taybct.tool.core.bean.service.BaseServiceImpl;
import io.github.mangocrisp.spring.taybct.tool.core.util.MyBatisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 针对表【sys_notice(消息通知)】的数据库操作Service实现
 *
 * @author xijieyin <br> 2022/10/10 15:46
 * @since 1.0.5
 */
@Transactional(rollbackFor = Exception.class)
public class SysNoticeServiceImpl extends BaseServiceImpl<SysNoticeMapper, SysNotice>
        implements ISysNoticeService {

    @Autowired(required = false)
    protected ISysDictService sysDictService;

    @Autowired(required = false)
    protected ISysNoticeUserService sysNoticeUserService;

    @Override
    public IPage<SysNoticeVO> userNoticesPage(Map<String, Object> sqlQueryParams) {
        IPage<SysNoticeVO> page = MyBatisUtil.genPage(sqlQueryParams);
        SysNotice dto = JSONObject.parseObject(JSONObject.toJSONString(sqlQueryParams), SysNotice.class);
        getNotices(dto, sqlQueryParams, page.getCurrent(), page.getSize(), (total, list) -> {
            page.setTotal(total);
            page.setRecords(list);
        });
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addRelatedNotices(SysNotice notice, Collection<SysNoticeUserDTO> noticeUsers) {
        return !customizeSave(notice) ||
                (noticeUsers == null || noticeUsers.size() <= 0) ||
                sysNoticeUserService.saveBatch(noticeUsers.stream().map(nu -> {
                            SysNoticeUser sysNoticeUser = new SysNoticeUser(notice.getId(), nu.getRelatedId(), nu.getNoticeType(), nu.getStatus());
                            MyBatisUtil.setInsertDefaultValue(Collections.singletonList(sysNoticeUser));
                            return sysNoticeUser;
                        })
                        .collect(Collectors.toSet()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserNotices(int status, Collection<Long> noticeIds) {
        ILoginUser loginUser = securityUtil.getLoginUser();
        Set<SysNoticeUser> collect = noticeIds.stream().map(noticeId ->
                        new SysNoticeUser(noticeId, loginUser.getUserId(), SysDict.NoticeType.USER.getKey(), (byte) status))
                .collect(Collectors.toSet());
        // 把已经关联的删掉先，然后再批量保存
        collect.forEach(notice ->
                sysNoticeUserService.remove(Wrappers.<SysNoticeUser>lambdaQuery().eq(SysNoticeUser::getNoticeId, notice.getNoticeId())
                        .eq(SysNoticeUser::getRelatedId, notice.getRelatedId())
                        .eq(SysNoticeUser::getNoticeType, notice.getNoticeType())));
        return sysNoticeUserService.saveBatch(collect);
    }

    /**
     * 获取通知消息
     *
     * @param dto            {@literal 请求参数}
     * @param sqlQueryParams {@literal sql 查询参数}
     * @param current        页码
     * @param size           分布大小
     * @param result         返回结果
     * @author xijieyin <br> 2022/10/10 18:23
     * @since 1.0.5
     */
    private void getNotices(SysNotice dto, Map<String, Object> sqlQueryParams, Long current, Long size, BiConsumer<Long, List<SysNoticeVO>> result) {
        String pageOrder = MyBatisUtil.getPageOrder(sqlQueryParams);
        JSONObject params = JSONObject.parseObject(JSONObject.toJSONString(dto));
        params.remove("expansion");
        List<SysNoticeVO> list = Collections.emptyList();
        Map<String, String> relatedCondition = new HashMap<>();
        sysDictService.cache(SysDictConstants.NOTICE_TYPE).forEach(dict -> {
            Optional.ofNullable(sqlQueryParams.get(dict.getDictVal()))
                    .map(Object::toString).ifPresent(value ->
                            relatedCondition.put(dict.getDictKey(), value));
        });

        long count = baseMapper.countQuery(relatedCondition, MyBatisUtil.humpToUnderline(params));
        if (count > 0) {
            list = baseMapper.listQuery(relatedCondition
                    , MyBatisUtil.humpToUnderline(params)
                    , Optional.ofNullable(current).map(c -> (c - 1) * size).orElse(null)
                    , size
                    , pageOrder);
        }
        result.accept(count, list);

    }

}




