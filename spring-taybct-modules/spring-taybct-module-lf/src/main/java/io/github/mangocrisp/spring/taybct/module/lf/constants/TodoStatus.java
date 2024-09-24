package io.github.mangocrisp.spring.taybct.module.lf.constants;

/**
 * 待办状态
 *
 * @author XiJieYin <br> 2023/7/17 10:10
 */
public interface TodoStatus {

    /**
     * 待办
     */
    byte TODO = 1;
    /**
     * 待阅
     */
    byte TO_BE_READ = 2;
    /**
     * 被退回
     */
    byte BOUNCED = 3;
    /**
     * 未读
     */
    byte UNREAD = 4;
    /**
     * 反馈
     */
    byte FEEDBACK = 5;

}
