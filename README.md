
# Twitter message collector #

This development test is used as part of selection for Java developers. You are requested to develop a simple application that covers all the requirements listed below. To have an indication of the criteria that will be used to judge your submission, all the following are considered as metrics of good development:

+ Correctness of the implementation
+ Decent test coverage
+ Code cleanliness
+ Efficiency of the solution
+ Careful choice of tools and data formats
+ Use of production-ready approaches

You are free to choose any library as long as it can run on the JVM.

## Task ##

We would like you to write code that will cover the functionality explained below and provide us with the source, instructions to build and run the appliocation  as well as a sample output of an execution:

+ Connect to the [Twitter Streaming API](https://dev.twitter.com/streaming/overview)
    * Use the following values:
        + Consumer Key: `RLSrphihyR4G2UxvA0XBkLAdl`
        + Consumer Secret: `FTz2KcP1y3pcLw0XXMX5Jy3GTobqUweITIFy4QefullmpPnKm4`
    * The app name will be `java-exercise`
    * You will need to login with Twitter
+ Filter messages that track on "bieber"
+ Retrieve the incoming messages for 30 seconds or up to 100 messages, whichever comes first
+ Your application should return the messages grouped by user (users sorted chronologically, ascending)
+ The messages per user should also be sorted chronologically, ascending
+ For each message, we will need the following:
    * The message ID
    * The creation date of the message as epoch value
    * The text of the message
    * The author of the message
+ For each author, we will need the following:
    * The user ID
    * The creation date of the user as epoch value
    * The name of the user
    * The screen name of the user
+ All the above infomation is provided in either SDTOUT or a log file
+ You are free to choose the output format, provided that it makes it easy to parse and process by a machine

### __Bonus points for:__ ###

+ Keep track of messages per second statistics across multiple runs of the application
+ The application can run as a Docker container

## Provided functionality ##

The present project in itself is a [Maven project](http://maven.apache.org/) that contains one class that provides you with a `com.google.api.client.http.HttpRequestFactory` that is authorised to execute calls to the Twitter API in the scope of a specific user.
You will need to provide your _Consumer Key_ and _Consumer Secret_ and follow through the OAuth process (get temporary token, retrieve access URL, authorise application, enter PIN for authenticated token).
With the resulting factory you are able to generate and execute all necessary requests.
If you want to, you can also disregard the provided classes or Maven configuration and create your own application from scratch.

## Delivery ##

You are assigned to you own private repository. Please use your own branch and do not commit on master.
When the assignment is finished, please create a pull request on the master of this repository, and your contact person will be notified automatically. 

## Getting Start

This section contains instructions on running localy.

### Building and running Application
1. Clone this repository.
1. Compile the Application.

    `mvn clean install -DskipTests`
    
1. Start the Application.

    `java -jar target/twitter-app-1.0.0-SNAPSHOT.jar [-key] [-secret] [-tag]`
  
    usage available arguments:
      ```  
    usage: java -jar twitter-app-{version}.jar [-help] [-key] [-secret] [-tag]
     -duration <arg>   Limit the duration of the extraction of messages from Twitter (30 sec)
     -help             print this message
     -key <arg>        A value used by the Consumer to identify itself to the Service Provider.
     -limit <arg>      Limit the number of messages retrieved from twitter (100)
     -secret <arg>     A secret used by the Consumer to establish ownership of the Consumer Key
     -tag <arg>        Keywords to track. Phrases of keywords are specified by a comma-separated list.
    ```
### Building and running with Docker
1. Clone this repository.
1. Compile and create a Docker image, which has name ${env.USER}/widget-app.
 
    `mvn clean install -DskipTests dockerfile:build`
   
   ```
  
    bash-3.2$ docker images -a
    REPOSITORY                  TAG                 IMAGE ID            CREATED             SIZE
    ${env.USER}/twitter-app       latest              6dcc5420aa2d        24 minutes ago      110MB   
   ```
1. Start the Application.

    `docker run -i --name=twitter-app ${env.USER}/twitter-app -key RLSrphihyR4G2UxvA0XBkLAdl -secret FTz2KcP1y3pcLw0XXMX5Jy3GTobqUweITIFy4QefullmpPnKm4 -tag bieber`

   example 
    ```
   bash-3.2$ docker run -i --name=twitter-app ${env.USER}/twitter-app -key RLSrphihyR4G2UxvA0XBkLAdl -secret FTz2KcP1y3pcLw0XXMX5Jy3GTobqUweITIFy4QefullmpPnKm4 -tag bieber
   initializing..
   Aquired temporary token...
   
   Go to the following link in your browser:
   https://api.twitter.com/oauth/authorize?oauth_token=BRIh-QAAAAAAt7ElAAABbwoEW98
   
   Please enter the retrieved PIN:
   4693263
   
   Authorization was successful
   connected to Twitter
   Fetching messages from twitter..
   84 items have been pulled out.
   
    author:trishuson id:30650188 created:12.04.2009 13:36:44
   	15.12.2019 14:44:38 text:RT @maIonesus: JUSTIN BIEBER SINGING BABY IN 2019.. I DID NOT KNOW HOW MUCH I NEEDED THIS UNTIL NOW OMFGGGG https://t.co/YK8RZBozmH
   
    author:KassidyStevens_ id:231101169 created:27.12.2010 16:06:39
   	15.12.2019 14:44:52 text:RT @maIonesus: JUSTIN BIEBER SINGING BABY IN 2019.. I DID NOT KNOW HOW MUCH I NEEDED THIS UNTIL NOW OMFGGGG https://t.co/YK8RZBozmH
   
   ...
   
    author:merarisimei1 id:1195017483075366912 created:14.11.2019 16:36:22
   	15.12.2019 14:44:46 text:RT @ibieberauhlIs: JISTIN BIEBER SINGING BABY IN 2019 IS SUCH A BLESSING https://t.co/QWHiBWjCnS
   Shutting down..
   ```
