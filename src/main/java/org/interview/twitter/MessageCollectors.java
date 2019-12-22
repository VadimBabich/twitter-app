package org.interview.twitter;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MessageCollectors {

    /**
     * Collect messages grouped by author and sorted in chronological order.
     *
     * @return collector
     */
    public static Collector<Message, ?, Map<Author, List<Message>>> groupingByAuthorAndSortByDateAscending() {

        Collector<Message, List<Message>, List<Message>> sortAndCollectMessageForAuthor
                = messagesSortBy(Comparators.messageByDateAscending());

        Supplier<Map<Author, List<Message>>> sortedMapSupplier = () ->
                new TreeMap<>(Comparators.authorByDateAscending());

        return Collectors.groupingBy(Message::getAuthor
                , sortedMapSupplier
                , sortAndCollectMessageForAuthor);
    }

    static public Collector<Message, List<Message>, List<Message>> messagesSortBy(Comparator<Message> messageComparator) {

        assert null != messageComparator;

        return new Collector<Message, List<Message>, List<Message>>() {

            @Override
            public Supplier<List<Message>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<Message>, Message> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<Message>> combiner() {
                return (left, right) -> {
                    left.addAll(right);
                    return left;
                };
            }

            @Override
            public Function<List<Message>, List<Message>> finisher() {
                return strings -> {
                    strings.sort(messageComparator);
                    return strings;
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };

    }


    public static class Comparators {

        /**
         * Message date comparator in chronological order
         * @return comparator
         */
        public static Comparator<Message> messageByDateAscending() {

            return Comparator
                    .comparing(Message::getDate)
                    .thenComparingLong(Message::getId);
        }

        /**
         * Author date comparator in chronological order
         * @return comparator
         */
        public static Comparator<Author> authorByDateAscending() {

            return Comparator
                    .comparing(Author::getDate)
                    .thenComparingLong(Author::getId);
        }
    }
}
