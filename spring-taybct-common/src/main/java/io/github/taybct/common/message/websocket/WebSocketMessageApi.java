package io.github.taybct.common.message.websocket;

import io.github.taybct.tool.core.websocket.support.WSR;

/**
 * <pre>
 * websocket 消息 api
 * </pre>
 *
 * @author XiJieYin
 * @since 2025/10/31 17:08
 */
public interface WebSocketMessageApi {

    /**
     * 发送当前用户消息
     *
     * @param message 消息内容
     */
    boolean sendCurrentUser(String message);

    /**
     * 发送简单消息
     *
     * @param message 消息内容
     */
    boolean send(WSR<?> message);

    /**
     * 发送所有用户消息
     *
     * @param message 消息内容
     */
    boolean sendAll(WSR<?> message);
}
