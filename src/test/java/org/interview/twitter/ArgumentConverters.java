package org.interview.twitter;

import static java.time.temporal.ChronoField.OFFSET_SECONDS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.stream.Collectors;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

/**
 * ArgumentConverter extensions
 *
 * @author Vadim Babich
 */
public class ArgumentConverters {

    /**
     * localDateTime is converted to java.util.Date relative to the systemDefault timezone.
     * @return java.util.Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        long offsetSeconds = ZoneOffset.systemDefault()
                .getRules()
                .getOffset(localDateTime)
                .getLong(OFFSET_SECONDS);

        return Date.from(ZonedDateTime.of(localDateTime.plusSeconds(offsetSeconds)
                , ZoneId.systemDefault()).toInstant());
    }

    /**
     * Reading a resource by name and returning it in string format.
     */
    public static class ResourceNameToStringArgumentConverter implements ArgumentConverter {

        @Override
        public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
            String resourceName = checkArgument(source);

            try (Reader reader = new InputStreamReader(getInputStream(resourceName, context))) {
                return new BufferedReader(reader).lines().collect(Collectors.joining());
            } catch (IOException exception) {
                throw new ArgumentConversionException(String.format("Resource file {%s} cannot be read",
                        resourceName), exception);
            }
        }

        InputStream getInputStream(String resourceName, ParameterContext context) throws ArgumentConversionException{
            Class<?> testClass = context.getDeclaringExecutable().getDeclaringClass();

            InputStream inputStream = testClass.getClassLoader().getResourceAsStream(resourceName);
            if(null == inputStream){
                throw new ArgumentConversionException(String.format("Resource {%s} could not be found",
                        resourceName));
            }

            return inputStream;
        }

        String checkArgument(Object source) throws ArgumentConversionException {
            if (source == null) {
                throw new ArgumentConversionException("Cannot convert null source object");
            }

            if (!source.getClass().equals(String.class)) {
                throw new ArgumentConversionException(
                        "Cannot convert source object because it's not a string"
                );
            }

            return (String) source;
        }
    }
}
