package org.interview.twitter;

import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.interview.twitter.ArgumentConverters.toDate;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;
import org.interview.twitter.ArgumentConverters.ResourceNameToStringArgumentConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.JavaTimeConversionPattern;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Testing conversion JSON to a domain object
 *
 * @author Vadim Babich
 */
class MessageConverterTest {

    private MessageConverter underTest;

    @BeforeEach
    void setUp() {
        underTest = new MessageConverter();
    }

    @DisplayName("JSON string should be converting to domain objects.")
    @ParameterizedTest(name = "{index} => json={0}, userDate={1}, messageDate={2}")
    @CsvSource({"response.json, Sat Apr 23 18:37:40 +0000 2016, Sat Dec 14 16:01:12 +0000 2019"})
    void
    jsonShouldBeConvertedToDomainObjects_apply(@ConvertWith(ResourceNameToStringArgumentConverter.class) String json
            , @JavaTimeConversionPattern("EEE MMM dd HH:mm:ss Z yyyy") LocalDateTime authorDate
            , @JavaTimeConversionPattern("EEE MMM dd HH:mm:ss Z yyyy") LocalDateTime messageDate) {

        Message message = underTest.apply(json);

        Author expectedAuthor = new Author(723943940739731457L
                , toDate(authorDate)
                , "\u1d04ristiAna"
                , "cristianamaleno");

        Message expectedMessage = new Message(1205880427157827585L
                , toDate(messageDate)
                , "JISTIN BIEBER SINGING BABY IN 2019 IS SUCH A BLESSING"
                , expectedAuthor);

        assertThat(message, samePropertyValuesAs(expectedMessage));
        assertThat(message.author, samePropertyValuesAs(expectedAuthor));
    }

}