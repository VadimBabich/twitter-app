package org.interview.twitter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Objects;

import java.util.Date;

/**
 * Class to hold properties associated with a tweet message.
 *
 * @author Vadim Babich
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("created_at")
    private Date date;

    @JsonProperty("text")
    private String text;

    @JsonProperty("user")
    Author author;

    @JsonCreator
    public Message(@JsonProperty("id") Long id
            , @JsonProperty("created_at") Date date
            , @JsonProperty("text") String text
            , @JsonProperty("user") Author author) {
        assert null != id;

        this.id = id;
        this.date = date;
        this.text = text;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public Author getAuthor() {
        return author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("id", id)
                .add("date", date)
                .add("text", text)
                .add("author", author)
                .toString();
    }
}
