package com.socialuni.social.sdk.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.socialuni.social.utils.JsonUtils;
import org.springframework.web.socket.TextMessage;

public class WebsocketUtil {
    public <T> TextMessage toMessage(T object) {
        try {
            return new TextMessage(JsonUtils.objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
