package org.interview.twitter;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.function.Function;

/**
 * Convert JSON string to message object
 *
 * @author Vadim Babich
 */
public class MessageConverter implements Function<String, Message> {

    private final ObjectMapper objectMapper;

    public MessageConverter() {
        this.objectMapper = new ObjectMapper()
                .setDateFormat(new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy"));

    }

    @Override
    public Message apply(String tweetLine) {
        try {
            return objectMapper.readValue(tweetLine, Message.class);
        } catch (IOException e) {
            throw new RuntimeException("Error when parsing the message: ", e.getCause());
        }
    }
}
