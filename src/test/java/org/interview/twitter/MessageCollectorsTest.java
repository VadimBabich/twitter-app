package org.interview.twitter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Testing for grouped by author and sorted in chronological order collectors.
 *
 * @author Vadim Babich
 */
public class MessageCollectorsTest {


    private List<Message> messageList;

    @Before
    public void setUp(){
        Author firstAuthor = new Author(1L, new Date(1L), "", "");
        Author secondAuthor = new Author(2L, new Date(2L), "", "");

        messageList = Arrays.asList(new Message(1L, new Date(1L), "", firstAuthor)
            , new Message(2L, new Date(2L), "", firstAuthor)
            , new Message(3L, new Date(3L), "", secondAuthor));
    }

    @Test
    public void groupingByAuthorAndSortByDateAscending() {

        Map<Author, List<Message>> value = messageList.stream()
                .collect(MessageCollectors.groupingByAuthorAndSortByDateAscending());

        Iterator<Author> authors = value.keySet().iterator();

        Author author = authors.next();

        Assert.assertEquals(1L, author.getId().longValue());
        Assert.assertEquals(1L, value.get(author).get(0).getId().longValue());
        Assert.assertEquals(2L, value.get(author).get(1).getId().longValue());

        author = authors.next();
        Assert.assertEquals(2L, author.getId().longValue());
        Assert.assertEquals(3L, value.get(author).get(0).getId().longValue());
    }
}