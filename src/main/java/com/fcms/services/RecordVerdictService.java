package com.fcms.services;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class RecordVerdictService {

    // Dummy verdict options
    public List<String> getVerdictOptions() {
        return Arrays.asList(
                "Guilty",
                "Not Guilty",
                "Case Dismissed",
                "Plea Bargain Accepted",
                "Hung Jury",
                "Mistrial"
        );
    }

    // Simulated save to database
    public boolean saveVerdict(String verdict, String sentence, String notes, LocalDate date) {
        System.out.println("----- VERDICT SAVED -----");
        System.out.println("Verdict: " + verdict);
        System.out.println("Sentence: " + sentence);
        System.out.println("Notes: " + notes);
        System.out.println("Date: " + date);
        System.out.println("--------------------------");
        return true;
    }
}
