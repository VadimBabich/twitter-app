package org.interview.twitter;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
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

        assert limit > 0;
        assert maxPollingDurationSec > 0;

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

            LineNumberReader reader = new LineNumberReader(new InputStreamReader(response.getContent()));

            pull(tweetConsumer, reader);

            return reader.getLineNumber();

        } catch (Exception e) {
            throw new RuntimeException("Error retrieving messages from twitter:", e.getCause());
        } finally {

            if (null != response) {
                try {
                    response.disconnect();
                } catch (IOException ignore) {
                }
            }
        }
    }

    private void pull(Consumer<String> tweetsConsumer, LineNumberReader reader) {

        AtomicReference<Exception> exceptionHolder = new AtomicReference<>();

        Future<?> handler = executor.submit(() -> {
            try {
                for (; reader.getLineNumber() < limit; ) {

                    String line;
                    if (null == (line = reader.readLine())) {
                        continue;
                    }

                    tweetsConsumer.accept(line);
                }
            } catch (Exception e) {
                exceptionHolder.set(e);
            }
        });

        try {
            handler.get(maxPollingDuration, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException ignore) {
        }

        if (null != exceptionHolder.get()) {
            throw new RuntimeException(exceptionHolder.get());
        }
    }

    private static GenericUrl getGenericUrl(String tag) {
        String requestLine = String
                .format("https://stream.twitter.com/1.1/statuses/filter.json?track=%s", tag);

        return new GenericUrl(requestLine);
    }

}
