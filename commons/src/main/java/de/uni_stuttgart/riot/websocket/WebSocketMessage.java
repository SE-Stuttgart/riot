package de.uni_stuttgart.riot.websocket;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Base class for web socket messages. All messages that are sent over web socket connections handled by {@link BaseEndpoint} must inherit
 * from this class. All these messages should be in this package to ensure that the entire web socket protocol "definition" is in one place
 * and to keep the Jackson type info structure complete.
 * 
 * @author Philipp Keck
 */
@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = NotificationMessage.class, name = "NotificationMessage"), //

})
public abstract class WebSocketMessage {

    /**
     * Allow sub-classes in same package, only.
     */
    WebSocketMessage() {
    }

    /**
     * This class uses Jackson to decode and encode JSON messages.
     */
    public static class Coder implements Decoder.TextStream<WebSocketMessage>, Encoder.TextStream<WebSocketMessage> {

        /**
         * The Jackson ObjectMapper used for all instances.
         */
        private static final ObjectMapper MAPPER = new ObjectMapper();

        @Override
        public void init(EndpointConfig config) {
        }

        @Override
        public void destroy() {
        }

        @Override
        public void encode(WebSocketMessage object, Writer writer) throws EncodeException, IOException {
            MAPPER.writeValue(writer, object);
        }

        @Override
        public WebSocketMessage decode(Reader reader) throws DecodeException, IOException {
            return MAPPER.readValue(reader, WebSocketMessage.class);
        }
    }

}
