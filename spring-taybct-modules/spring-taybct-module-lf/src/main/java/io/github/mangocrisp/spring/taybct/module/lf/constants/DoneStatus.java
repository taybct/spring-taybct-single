package io.github.mangocrisp.spring.taybct.module.lf.constants;

/**
 * 已办状态
 *
 * @author XiJieYin <br> 2023/7/17 10:10
 */
public interface DoneStatus {

    /**
     * 未归档
     */
    byte NOT_ARCHIVED = 1;
    /**
     * 已经归档
     */
    byte ARCHIVED = 2;
    /**
     * 待回复
     */
    byte TO_BE_REPLIED = 3;
    /**
     * 未读
     */
    byte UNREAD = 4;
    /**
     * 反馈
     */
    byte FEEDBACK = 5;

}
