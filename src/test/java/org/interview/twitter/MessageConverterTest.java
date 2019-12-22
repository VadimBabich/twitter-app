package org.interview.twitter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.stream.Collectors;

/**
 * Testing conversion JSON to a domain object
 *
 * @author Vadim Babich
 */
public class MessageConverterTest {

    MessageConverter messageConverter;
    String json;

    @Before
    public void setUp() throws IOException {
        messageConverter = new MessageConverter();

        try (Reader reader = new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream("response.json"))) {
            json = new BufferedReader(reader).lines().collect(Collectors.joining());
        }
    }


    @Test
    public void apply() {

        Message message = messageConverter.apply(json);

        Assert.assertEquals(1205880427157827585L, message.getId().longValue());
        Assert.assertNotNull(message.getDate());
        Assert.assertEquals("JISTIN BIEBER SINGING BABY IN 2019 IS SUCH A BLESSING", message.getText());

        Author author = message.getAuthor();
        Assert.assertNotNull(author.getDate());
        Assert.assertEquals(723943940739731457L, author.getId().longValue());
        Assert.assertEquals("\u1d04ristiAna", author.getName());
        Assert.assertEquals("cristianamaleno", author.getScreenName());
    }
}