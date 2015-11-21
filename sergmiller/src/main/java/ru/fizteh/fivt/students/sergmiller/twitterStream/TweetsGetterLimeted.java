package ru.fizteh.fivt.students.sergmiller.twitterStream;

import twitter4j.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sergmiller on 16.11.15.
 */
public class TweetsGetterLimeted {
    static final String RADIUS_UNIT = "km";
    public static final int MAX_QUANTITY_OF_TRIES = 2;

    /**
     * Mod with print limited quantity of text.
     *
     * @param jCommanderParser is class with query's info
     * @throws TwitterException is kind of exception
     */
    public List<String> getTwitterLimited(
            final JCommanderParser jCommanderParser, final Twitter twitter) {
        int currentQuantityOfTries = 0;
        List<String> allTweets = new ArrayList<>();
        LocationData currentLocation = jCommanderParser.getGeoLocation();
        while (currentQuantityOfTries < MAX_QUANTITY_OF_TRIES) {
            try {
                String joinedQuery = "";
                if (!jCommanderParser.getQuery().isEmpty()) {
                    joinedQuery = String.join(" ", jCommanderParser.getQuery());
                }
                Query query = new Query(joinedQuery);

                if (currentLocation != null) {
                    query.geoCode(currentLocation.getGeoLocation(), currentLocation.getRadius(), RADIUS_UNIT);
                }

                query.setCount(jCommanderParser.getLimit());

                QueryResult request;
                int quantityOfPrintedTweets = 0;
                Boolean flagLimit = false;

                do {
                    request = twitter.search(query);

                    List<Status> tweets = request.getTweets();

//                    System.out.print("************tweets***************\n");
//                    for (int i = 0;i < tweets.size();++i) {
//                    System.out.println(tweets.get(i));
//                     }

//                  write("/Users/sergmiller/Documents/javaproj/JavaGit/fizteh-java-2015/sergmiller"
//                            + "/DoctorWhoInLondonTweets.json", new JSONObject(request).toString());

                    for (Status status : tweets) {
                        if (!jCommanderParser.isHideRetweets()
                                || !status.isRetweet()) {
                            allTweets.add(TweetFormater.formatTweet(status, jCommanderParser));
                            ++quantityOfPrintedTweets;
                            if (quantityOfPrintedTweets == jCommanderParser.getLimit()) {
                                flagLimit = true;
                                break;
                            }
                        }
                    }
                    query = request.nextQuery();
                } while (query != null && !flagLimit);

                if (allTweets.isEmpty()) {

                    StringBuilder emptyAnswerMessage = new StringBuilder();

                    emptyAnswerMessage.append("\nПо запросу ");
                    emptyAnswerMessage.append(String.join(", ", jCommanderParser.getQuery()));

                    String printedNameOfLocation = "";

                    if (currentLocation == null && jCommanderParser.getLocation() != "") {
                        printedNameOfLocation = " для World";
                    } else {
                        if (currentLocation != null) {
                            printedNameOfLocation = " для " + currentLocation.getName();
                        }
                    }
                    emptyAnswerMessage.append(printedNameOfLocation);
                    emptyAnswerMessage.append(" ничего не найдено=(\n\n"
                            + "Рекомендации:\n\n"
                            + "Убедитесь, что все слова"
                            + " написаны без ошибок.\n"
                            + "Попробуйте использовать "
                            + "другие ключевые слова.\n"
                            + "Попробуйте использовать "
                            + "более популярные ключевые слова.");

                    List <String> report = new ArrayList<>();
                    report.add(emptyAnswerMessage.toString());
                    return report;
                } else {
                    Collections.reverse(allTweets);
                }

                currentQuantityOfTries = MAX_QUANTITY_OF_TRIES;
            } catch (TwitterException twExp) {
                ++currentQuantityOfTries;
                if (currentQuantityOfTries == MAX_QUANTITY_OF_TRIES) {
                    TwitterStreamRunner.printIntoStdout(twExp.getMessage()
                            + "\nЧто-то пошло не так=(\n"
                            + "Проверьте наличие соединения.");
                }
            }
        }
        return allTweets;
    }
}
