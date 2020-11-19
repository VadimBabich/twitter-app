package org.interview.twitter;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UncheckedIOException;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Fetching messages from twitter
 *
 * @author Vadim Babich
 */
public class MessagePuller {

    private final HttpRequestFactory requestFactory;
    private final int limit;
    private final int maxPollingDuration;

    private final ExecutorService executor = Executors.newFixedThreadPool(1);


    public MessagePuller(HttpRequestFactory requestFactory, int limit, int maxPollingDurationSec) {

        if(limit <= 0 || 0 >= maxPollingDurationSec) {
            throw new IllegalArgumentException(String.format("limit {%d} and maxPollingDurationSec {%d} "
                    + "must be greater than 0", limit, maxPollingDurationSec));
        }

        this.requestFactory = requestFactory;
        this.limit = limit;
        this.maxPollingDuration = maxPollingDurationSec;
    }

    public int getTweetsBy(String tag, Consumer<String> tweetConsumer) {

        GenericUrl url = getGenericUrl(tag);
        tweetConsumer = null == tweetConsumer ? System.out::println : tweetConsumer;

        HttpResponse response = null;
        try {
            HttpRequest request = requestFactory.buildGetRequest(url);

            response = request.execute();

            try(LineNumberReader reader = new LineNumberReader(new InputStreamReader(response.getContent()))) {

                pull(tweetConsumer, reader);

                return reader.getLineNumber();
            }

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ExecutionException e){
            throw new MessageRetrievingException("Error retrieving messages from twitter:", e.getCause());
        } finally {

            if (null != response) {
                try {
                    response.disconnect();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private void pull(Consumer<String> tweetsConsumer, LineNumberReader reader) throws ExecutionException {

        Future<?> handler = executor.submit(() -> {
            try {
                while (reader.getLineNumber() < limit) {

                    String line;
                    if (null == (line = reader.readLine())) {
                        continue;
                    }

                    tweetsConsumer.accept(line);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });

        try {
            handler.get(maxPollingDuration, TimeUnit.SECONDS);
        } catch (InterruptedException e ) {
            Thread.currentThread().interrupt();
            throw new CancellationException(e.getMessage());
        } catch (TimeoutException ignore) {
        }

    }

    private static GenericUrl getGenericUrl(String tag) {
        String requestLine = String
                .format("https://stream.twitter.com/1.1/statuses/filter.json?track=%s", tag);

        return new GenericUrl(requestLine);
    }

    public static class MessageRetrievingException extends RuntimeException{

        public MessageRetrievingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
