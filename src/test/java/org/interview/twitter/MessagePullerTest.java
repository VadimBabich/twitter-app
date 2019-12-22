package org.interview.twitter;


import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.mockito.Matchers.any;

/**
 * Testing limits of puller (duration and count)
 *
 * @author Vadim Babich
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpRequestFactory.class, HttpRequest.class, HttpResponse.class})
public class MessagePullerTest {

    MessagePuller underTest;

    @Mock
    private HttpRequestFactory httpRequestFactory;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private HttpResponse httpResponse;

    @Before
    public void setUp() throws IOException {

        MockitoAnnotations.initMocks(this);

        StringBuffer buffer = new StringBuffer();
        IntStream.range(0, 100).forEach((item) -> buffer.append(item).append("\n"));

        Mockito.when(httpResponse.getContent())
                .thenReturn(new ByteArrayInputStream(buffer.toString().getBytes()));
        Mockito.when(httpRequest.execute())
                .thenReturn(httpResponse);
        Mockito.when(httpRequestFactory.buildGetRequest(any(GenericUrl.class)))
                .thenAnswer((Answer<HttpRequest>) invocationOnMock -> httpRequest);

        underTest = new MessagePuller(httpRequestFactory, 10, 1);
    }

    @Test
    public void getTweetsByCountLimitTest() {
        AtomicInteger count = new AtomicInteger(0);
        underTest.getTweetsBy("bieber", s -> count.incrementAndGet());

        Assert.assertEquals(10, count.get());
    }

    @Test
    public void getTweetsByDurationLimitTest() {

        AtomicInteger count = new AtomicInteger(0);
        underTest.getTweetsBy("bieber", s -> {
            try {

                Thread.sleep(1_000);
                count.incrementAndGet();
            } catch (InterruptedException ignore) {
            }
        });

        Assert.assertEquals(1, count.get());
    }

    @Test(expected = RuntimeException.class)
    public void getTweetsByExceptionTest() {

        underTest.getTweetsBy("bieber", s -> {
            throw new RuntimeException("test");
        });
    }

}
