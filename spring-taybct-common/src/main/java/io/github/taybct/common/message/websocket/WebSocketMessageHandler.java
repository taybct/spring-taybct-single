package io.github.taybct.common.message.websocket;

import com.alibaba.fastjson2.JSONObject;
import io.github.taybct.common.constants.TypeConstant;
import io.github.taybct.tool.core.message.IMessageSendHandler;
import io.github.taybct.tool.core.message.MessageType;
import io.github.taybct.tool.core.websocket.support.WSR;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

/**
 * <pre>
 * websocket 消息处理器
 * </pre>
 *
 * @author XiJieYin
 * @since 2025/11/1 10:25
 */
@Slf4j
@RequiredArgsConstructor
public class WebSocketMessageHandler implements IMessageSendHandler {

    final WebSocketMessageApi webSocketMessageApi;

    @Override
    @NonNull
    public MessageType getMessageType() {
        return TypeConstant.WEBSOCKET_MESSAGE;
    }

//    @Override
//    public boolean sendOriginal(Serializable data) {
//        if (data instanceof WSR<?> message) {
//            webSocketMessageApi.send(message);
//        }
//        return true;
//    }

    @Override
    public boolean send(String message) {
        try {
            webSocketMessageApi.send(JSONObject.parseObject(message, WSR.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
