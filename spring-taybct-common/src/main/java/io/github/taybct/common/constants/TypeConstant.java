package io.github.taybct.common.constants;

import cn.hutool.core.lang.UUID;
import io.github.taybct.common.message.cheduledlog.ScheduledLogDTO;
import io.github.taybct.common.message.sysfile.FileSendDTO;
import io.github.taybct.common.message.websocket.WebSocketMessageDTO;
import io.github.taybct.tool.core.message.MessageType;

/**
 * <pre>
 * 消息类型
 * </pre>
 *
 * @author XiJieYin
 * @since 2024/9/1 23:08
 */
public interface TypeConstant {

    /**
     * 文件关联消息类型
     */
    MessageType SYS_FILE_LINK = new MessageType(FileSendDTO.class, "SYS_FILE_LINK-", UUID.fastUUID().toString(true), ".json");
    /**
     * 任务调度日志消息
     */
    MessageType SCHEDULED_LOG = new MessageType(ScheduledLogDTO.class, "SCHEDULED_LOG-", UUID.fastUUID().toString(true), ".json");
    /**
     * websocket 消息
     */
    MessageType WEBSOCKET_MESSAGE = new MessageType(WebSocketMessageDTO.class, "WEBSOCKET_MESSAGE-", UUID.fastUUID().toString(true), ".json");
}
