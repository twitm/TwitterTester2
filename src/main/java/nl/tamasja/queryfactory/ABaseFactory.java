package nl.tamasja.queryfactory;

import java.util.Random;

/**
 * TIS 21-8-2014.11:57
 */
public abstract class ABaseFactory {

    //59
    protected String[] queryStrings = {
            "water shortages",
            "Florida Derby 2013",
            "Kal Penn",
            "Detroit EFM Undemocratic",
            "memories of Mr. Rogers",
            "Chinese Computer Attacks",
            "marshmallow Peeps dioramas",
            "Israel and Turkey reconcile",
            "colony collapse disorder",
            "Argentina's Inflation",
            "Future of MOOCs",
            "unsuccessful kickstarter applicants",
            "solar flare",
            "celebrity DUI",
            "Oscars snub Affleck",
            "Pitbull rapper",
            "Hagel nomination filibustered",
            "Buying clothes online",
            "Angry Birds cartoon",
            "Lawyer jokes",
            "trash the dress",
            "asteroid hits Russia",
            "cruise ship safety",
            "The Middle TV show",
            "Big Dog terminator robot",
            "Gone Girl reviews",
            "cause of the Super Bowl blackout",
            "New York City soda ban blocked",
            "Artists Against Fracking",
            "Richard III burial dispute",
            "Mila Kunis in Oz movie",
            "Iranian weapons to Syria",
            "Maracana Stadium problems",
            "Downton Abbey actor turnover",
            "National Parks sequestered",
            "GMO labeling",
            "Victoria's Secret commercial",
            "Cyprus Bailout Protests",
            "making football safer",
            "UK wine industry",
            "gun advocates are corrupt",
            "Iceland FBI Wikileaks",
            "lighter bail for Pistorius",
            "anti-aging resveratrol",
            "Obama reaction to Syrian chemical weapons",
            "Bush's dog dies",
            "Kardashian maternity style",
            "hush puppies meal",
            "circular economy initiatives",
            "social media as educational tool",
            "3D printing for science",
            "DPRK Nuclear Test",
            "virtual currencies regulation",
            "Lindsey Vonn sidelined",
            "ACPT Crossword Tournament",
            "Maryland casino table games",
            "sequestration opinions",
            "US behind Chaevez cancer",
            "Honey Boo Boo Girl Scout cookies",
            "Tony Mendez",
    };


    public String[] getQueryStrings() {
        return this.queryStrings;
    }

    public String getRandomPhrase() {
        Random random = new Random();
        return this.queryStrings[random.nextInt(this.queryStrings.length)];
    }
}
