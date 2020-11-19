package org.interview.twitter;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Testing for grouped by author and sorted in chronological order collectors.
 *
 * @author Vadim Babich
 */
class MessageCollectorsTest {


    @ParameterizedTest
    @MethodSource("getMessageList")
    @DisplayName("Messages should be grouping and sorting by author in chronological order.")
    void groupingByAuthorAndSortByDateAscending(Stream<Message> messageStream) {

        Map<Author, List<Message>> value = messageStream
                .collect(MessageCollectors.groupingByAuthorAndSortByDateAscending());

        Iterator<Author> authors = value.keySet().iterator();

        Author author = authors.next();
        assertThat(author.getId(), equalTo(1L));

        Iterator<Message> messages = value.get(author).iterator();
        assertThat(messages.next().getId(), equalTo(1L));
        assertThat(messages.next().getId(), equalTo(2L));

        author = authors.next();
        assertThat(author.getId(), equalTo(2L));

        messages = value.get(author).iterator();
        assertThat(messages.next().getId(), equalTo(3L));
    }


    static Stream<Stream<Message>> getMessageList() {
        Author firstAuthor = new Author(1L, new Date(1L), "", "");
        Author secondAuthor = new Author(2L, new Date(2L), "", "");

        return Stream.of(
                Stream.of(new Message(1L, new Date(1L), "", firstAuthor)
                        , new Message(2L, new Date(2L), "", firstAuthor)
                        , new Message(3L, new Date(3L), "", secondAuthor))
        );
    }
}