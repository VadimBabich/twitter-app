package org.interview.twitter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.api.client.util.Objects;

import java.util.Date;

/**
 * Class to hold properties associated with an author of tweet message.
 *
 * @author Vadim Babich
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Author {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("created_at")
    private Date date;

    @JsonProperty("name")
    private String name;

    @JsonProperty("screen_name")
    private String screenName;

    @JsonCreator
    public Author(@JsonProperty("id") Long id
            , @JsonProperty("created_at") Date date
            , @JsonProperty("name") String name
            , @JsonProperty("screen_name") String screenName) {
        assert null != id;

        this.id = id;
        this.date = date;
        this.name = name;
        this.screenName = screenName;
    }

    public Long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return id.equals(author.id);
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
                .add("name", name)
                .add("screenName", screenName)
                .toString();
    }
}
