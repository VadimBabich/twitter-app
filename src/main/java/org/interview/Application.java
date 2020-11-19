package org.interview;

import com.google.api.client.http.HttpRequestFactory;
import org.apache.commons.cli.*;
import org.interview.twitter.*;
import org.interview.twitter.oauth.TwitterAuthenticationException;
import org.interview.twitter.oauth.TwitterAuthenticator;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.lang.System.exit;
import static java.lang.System.out;

/**
 * The main class for the tweets console app.
 *
 * @author Vadim Babich
 */
public class Application {

    private static final String USAGE_STRING = "java -jar twitter-app-{version}.jar [-help]"
            + " [-key] [-secret] [-tag]";

    private static final Options options;

    static {
        options = new Options();

        options.addOption(Option.builder("help")
                .required(false)
                .hasArg(false)
                .desc("print this message")
                .build());

        options.addOption(Option.builder("key")
                .required(true)
                .hasArg(true)
                .desc("A value used by the Consumer to identify itself to the Service Provider.")
                .build()
        );

        options.addOption(Option.builder("secret")
                .required(true)
                .hasArg(true)
                .desc("A secret used by the Consumer to establish ownership of the Consumer Key")
                .build()
        );

        options.addOption(Option.builder("tag")
                .required(true)
                .hasArg(true)
                .desc("Keywords to track. Phrases of keywords are specified by a comma-separated list.")
                .build()
        );

        options.addOption(Option.builder("limit")
                        .required(false)
                        .hasArg(true)
                        .desc("Limit the number of messages retrieved from twitter (100)")
                        .build()
        );

        options.addOption(Option.builder("duration")
                .required(false)
                .hasArg(true)
                .desc("Limit the duration of the extraction of messages from Twitter (30 sec)")
                .build()
        );
    }

    private static HttpRequestFactory requestFactory;
    private static MessagePuller messagePuller;
    private static String tag;


    public static void main(String[] args) {

        try {
            setupShutdownHook(out);

            initTweeter(args);

            run();

            exit(0);
        } catch (Exception e) {
            printUsage(e.getMessage());
        }
    }

    private static void run() {

        Stream.Builder<String> messageStream = Stream.builder();

        out.println("Fetching messages from twitter..");

        out.print(messagePuller.getTweetsBy(tag, messageStream));
        out.println(" items have been pulled out.");
        
        Map<Author, List<Message>> result = messageStream.build()
                .map(new MessageConverter())
                .collect(MessageCollectors.groupingByAuthorAndSortByDateAscending());

        PrintHelper.prettyPrint(out, result);
    }

    private static void initTweeter(String... args) {
        out.println("initializing..");

        CommandLine commandLine;
        try {
            commandLine = new DefaultParser().parse(options, args);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getMessage());
        }

        if (commandLine.hasOption("help")) {
            printUsage();
            exit(0);
        }

        setUpRequestFactory(commandLine);

        setUpMessagePuller(commandLine);

        tag = commandLine.getOptionValue("tag");

    }


    private static void setUpRequestFactory(CommandLine commandLine) {
        TwitterAuthenticator authenticator = new TwitterAuthenticator(System.out
                , commandLine.getOptionValue("key")
                , commandLine.getOptionValue("secret"));
        try {
            requestFactory = authenticator.getAuthorizedHttpRequestFactory();
            out.println("connected to Twitter");
        }catch (TwitterAuthenticationException e){
            throw new RuntimeException("Unable to connect to Twitter API", e.getCause());
        }
    }

    private static void setUpMessagePuller(CommandLine commandLine){
        messagePuller = new MessagePuller(requestFactory
            , Integer.parseInt(commandLine.getOptionValue("limit", "100"))
            , Integer.parseInt(commandLine.getOptionValue("duration", "30"))
        );
    }


    private static void printUsage(String message) {
        out.println();
        out.println(message);
        printUsage();
    }

    private static void printUsage() {
        out.println();

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(120, USAGE_STRING, "", options, "");
        out.println();
    }

    private static void setupShutdownHook(PrintStream printStream) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Thread.sleep(400);
            } catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
            printStream.println("\nShutting down ...");
        }));
    }
}
