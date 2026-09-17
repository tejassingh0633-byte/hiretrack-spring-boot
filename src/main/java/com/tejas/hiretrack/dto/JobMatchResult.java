package com.tejas.hiretrack.dto;

import java.util.List;

public class JobMatchResult {

    private final double matchScore;
    private final List<String> matchedSkills;
    private final List<String> missingSkills;
    private final String suitability;
    private final String recommendation;

    public JobMatchResult(
            double matchScore,
            List<String> matchedSkills,
            List<String> missingSkills,
            String suitability,
            String recommendation) {

        this.matchScore = matchScore;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
        this.suitability = suitability;
        this.recommendation = recommendation;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public String getSuitability() {
        return suitability;
    }

    public String getRecommendation() {
        return recommendation;
    }
}