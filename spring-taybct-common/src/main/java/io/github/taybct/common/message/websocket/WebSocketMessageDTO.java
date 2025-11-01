package io.github.taybct.common.message.websocket;

import com.alibaba.fastjson2.JSONObject;
import io.github.taybct.tool.core.message.Message;
import io.github.taybct.tool.core.websocket.support.WSR;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <pre>
 * websocket 消息 DTO
 * </pre>
 *
 * @author XiJieYin
 * @since 2025/10/31 17:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessageDTO implements Message {

    private WSR<?> message;

    private boolean realTime = true;

    @Serial
    private static final long serialVersionUID = 884236285493169019L;

    @Override
    public Serializable getOriginalData() {
        return this.message;
    }

    @Override
    public boolean getRealTime() {
        return this.realTime;
    }

    @Override
    public String getPayload() {
        return JSONObject.toJSONString(this.message);
    }
}
