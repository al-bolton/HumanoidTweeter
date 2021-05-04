package com.abolton.HumanoidTweeter.services;

import com.abolton.HumanoidTweeter.models.SteamSpyGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import twitter4j.TwitterException;

import javax.annotation.PostConstruct;
import java.util.Random;

@Service
public class TweetBodyConstructorService {
    private String tweetBody;

    @Autowired
    TweetPosterService tweetPosterService;
    @Autowired
    SteamSpyDataGrabberService steamSpyDataGrabberService;
    @Autowired
    TweetBodyDictionary tweetBodyDictionary;

    private enum APIChoices {
        STEAMSPY
    }

    public String getTweetBody() {
        return tweetBody;
    }

    @PostConstruct
    public void constructTweet() throws TwitterException {
        // Construct the tweet using the different methods for each API, depending on which is called by the
        // randomise function
        switch (chooseAPI()) {
            case STEAMSPY:
                this.tweetBody = steamspyTweet();
                break;
            default:
                this.tweetBody = null;
        }

        // Send this tweetBody to the TweetPoster, which will send it through to the Twitter API to post as a status
        System.out.println(tweetBody);
        // tweetPosterService.makeTweet(tweetBody);
    }

    private APIChoices chooseAPI() {
        // This will randomly select one of the APIs queried for information, to use that info to construct the tweet
        int randomPick = new Random().nextInt(APIChoices.values().length);
        return APIChoices.values()[randomPick];
    }

    private String steamspyTweet() {
        // This method constructs a tweet body String based on one of the games taken from the SteamSpy API
        // First we retrieve a random game from our game list retrieved from the API
        SteamSpyGame game = steamSpyDataGrabberService.getSteamSpyGames()
                .get(new Random().nextInt(steamSpyDataGrabberService.getSteamSpyGames().size()));

        // From this game, a tweetBody is composed using one of the possible intros and outros from our
        // TweetBodyDictionary
        return tweetBodyDictionary.gamerIntros() +
                " I have played " + game.getName() +
                " for a whole " + game.getAverageRecentPlay() + " minutes this week"
                + tweetBodyDictionary.gamerOutros();
    }



}