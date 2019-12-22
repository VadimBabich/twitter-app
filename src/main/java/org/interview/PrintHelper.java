package org.interview;

import org.interview.twitter.Author;
import org.interview.twitter.Message;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Allows you to print tweets in PrintStream
 *
 * @author Vadim Babich
 */
public class PrintHelper {

    public static void prettyPrint(PrintStream printStream, Map<Author, List<Message>> messages) {


        messages.forEach((author, mess) -> {

            printAuthor(printStream, author);

            mess.forEach(printMessage(printStream));

            printStream.print("\n");
        });
    }

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    static void printAuthor(PrintStream printStream, Author author) {

            printStream.print("\n author:");
            printStream.print(author.getScreenName());
            printStream.print(" id:");
            printStream.print(author.getId());

            printStream.print(" created:");
            printStream.print(dateFormat.format(author.getDate()));
    }

    static Consumer<Message> printMessage(PrintStream printStream) {

        return (message) -> {
            printStream.print("\n\t");
            printStream.print(dateFormat.format(message.getDate()));

            printStream.print(" text:");
            printStream.print(message.getText().replace("\n", ""));
        };
    }
}
