package com.tejas.hiretrack.service;

import com.tejas.hiretrack.dto.JobMatchResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class JobMatchService {

    private static final Map<String, List<String>> SKILL_DICTIONARY =
            createSkillDictionary();

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "an", "and", "are", "as", "at", "be", "by",
            "for", "from", "has", "have", "in", "is", "it",
            "of", "on", "or", "that", "the", "this", "to",
            "with", "will", "you", "your", "we", "our",
            "using", "required", "preferred", "experience",
            "knowledge", "skills", "skill", "work", "role", "job"
    );

    public JobMatchResult analyze(
            String resumeText,
            String jobDescription) {

        if (resumeText == null || resumeText.isBlank()
                || jobDescription == null
                || jobDescription.isBlank()) {

            return new JobMatchResult(
                    0.0,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    "Insufficient Information",
                    "Please provide both resume text and a job description."
            );
        }

        String cleanedResume = normalize(resumeText);
        String cleanedJob = normalize(jobDescription);

        Set<String> resumeSkills = extractSkills(cleanedResume);
        Set<String> requiredSkills = extractSkills(cleanedJob);

        List<String> matchedSkills = requiredSkills.stream()
                .filter(resumeSkills::contains)
                .sorted()
                .toList();

        List<String> missingSkills = requiredSkills.stream()
                .filter(skill -> !resumeSkills.contains(skill))
                .sorted()
                .toList();

        double skillScore = calculateSkillScore(
                matchedSkills.size(),
                requiredSkills.size()
        );

        double textSimilarity = calculateTfIdfSimilarity(
                cleanedResume,
                cleanedJob
        ) * 100.0;

        double finalScore;

        if (requiredSkills.isEmpty()) {
            finalScore = textSimilarity;
        } else {
            finalScore = (skillScore * 0.70)
                    + (textSimilarity * 0.30);
        }

        finalScore = round(finalScore);

        String suitability = determineSuitability(finalScore);

        String recommendation = createRecommendation(
                requiredSkills,
                missingSkills
        );

        return new JobMatchResult(
                finalScore,
                matchedSkills,
                missingSkills,
                suitability,
                recommendation
        );
    }

    private Set<String> extractSkills(String text) {
        Set<String> detectedSkills = new HashSet<>();
        String paddedText = " " + text + " ";

        for (Map.Entry<String, List<String>> entry
                : SKILL_DICTIONARY.entrySet()) {

            for (String alias : entry.getValue()) {
                String normalisedAlias = normalize(alias);

                if (paddedText.contains(
                        " " + normalisedAlias + " ")) {

                    detectedSkills.add(entry.getKey());
                    break;
                }
            }
        }

        return detectedSkills;
    }

    private double calculateSkillScore(
            int matchedCount,
            int requiredCount) {

        if (requiredCount == 0) {
            return 0.0;
        }

        return ((double) matchedCount / requiredCount) * 100.0;
    }

    private double calculateTfIdfSimilarity(
            String firstText,
            String secondText) {

        Map<String, Double> firstTf =
                calculateTermFrequency(firstText);

        Map<String, Double> secondTf =
                calculateTermFrequency(secondText);

        Set<String> vocabulary = new HashSet<>();
        vocabulary.addAll(firstTf.keySet());
        vocabulary.addAll(secondTf.keySet());

        double dotProduct = 0.0;
        double firstMagnitude = 0.0;
        double secondMagnitude = 0.0;

        for (String term : vocabulary) {
            int documentFrequency = 0;

            if (firstTf.containsKey(term)) {
                documentFrequency++;
            }

            if (secondTf.containsKey(term)) {
                documentFrequency++;
            }

            double inverseDocumentFrequency =
                    Math.log(3.0 / (documentFrequency + 1.0))
                            + 1.0;

            double firstValue =
                    firstTf.getOrDefault(term, 0.0)
                            * inverseDocumentFrequency;

            double secondValue =
                    secondTf.getOrDefault(term, 0.0)
                            * inverseDocumentFrequency;

            dotProduct += firstValue * secondValue;
            firstMagnitude += firstValue * firstValue;
            secondMagnitude += secondValue * secondValue;
        }

        if (firstMagnitude == 0.0 || secondMagnitude == 0.0) {
            return 0.0;
        }

        return dotProduct
                / (Math.sqrt(firstMagnitude)
                * Math.sqrt(secondMagnitude));
    }

    private Map<String, Double> calculateTermFrequency(
            String text) {

        List<String> tokens = tokenize(text);
        Map<String, Double> frequencies = new HashMap<>();

        for (String token : tokens) {
            frequencies.merge(token, 1.0, Double::sum);
        }

        if (!tokens.isEmpty()) {
            frequencies.replaceAll(
                    (term, count) -> count / tokens.size()
            );
        }

        return frequencies;
    }

    private List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();

        Arrays.stream(normalize(text).split("\\s+"))
                .filter(word -> word.length() > 1)
                .filter(word -> !STOP_WORDS.contains(word))
                .forEach(tokens::add);

        return tokens;
    }

    private String normalize(String text) {
        return text.toLowerCase()
                .replaceAll("[^a-z0-9+#]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String determineSuitability(double score) {
        if (score >= 75.0) {
            return "Strong Match";
        }

        if (score >= 50.0) {
            return "Moderate Match";
        }

        return "Low Match";
    }

    private String createRecommendation(
            Set<String> requiredSkills,
            List<String> missingSkills) {

        if (requiredSkills.isEmpty()) {
            return "Add a more detailed job description containing "
                    + "the required technical skills.";
        }

        if (missingSkills.isEmpty()) {
            return "Your resume covers all the recognised technical "
                    + "skills in this job description.";
        }

        return "Consider learning or demonstrating these skills: "
                + String.join(", ", missingSkills) + ".";
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static Map<String, List<String>>
            createSkillDictionary() {

        Map<String, List<String>> skills = new LinkedHashMap<>();

        skills.put("Java", List.of("java"));
        skills.put("Python", List.of("python"));
        skills.put("SQL", List.of("sql"));
        skills.put("MySQL", List.of("mysql"));
        skills.put("Spring Boot", List.of("spring boot"));
        skills.put("Spring MVC", List.of("spring mvc"));
        skills.put("Spring Security", List.of("spring security"));
        skills.put("Spring Data JPA",
                List.of("spring data jpa"));
        skills.put("JPA", List.of("jpa"));
        skills.put("Hibernate", List.of("hibernate"));
        skills.put("REST API",
                List.of("rest api", "restful api", "restful apis"));
        skills.put("Maven", List.of("maven"));
        skills.put("Git", List.of("git"));
        skills.put("GitHub", List.of("github"));
        skills.put("Docker", List.of("docker"));
        skills.put("AWS", List.of("aws", "amazon web services"));
        skills.put("HTML", List.of("html"));
        skills.put("CSS", List.of("css"));
        skills.put("JavaScript",
                List.of("javascript", "java script"));
        skills.put("Bootstrap", List.of("bootstrap"));
        skills.put("React",
                List.of("react", "reactjs", "react js"));
        skills.put("Node.js",
                List.of("node.js", "node js"));
        skills.put("Express.js",
                List.of("express.js", "express js"));
        skills.put("MongoDB", List.of("mongodb", "mongo db"));
        skills.put("Machine Learning",
                List.of("machine learning", "ml"));
        skills.put("Deep Learning",
                List.of("deep learning"));
        skills.put("TensorFlow", List.of("tensorflow"));
        skills.put("Keras", List.of("keras"));
        skills.put("Pandas", List.of("pandas"));
        skills.put("NumPy", List.of("numpy"));
        skills.put("Scikit-learn",
                List.of("scikit learn", "scikit-learn", "sklearn"));
        skills.put("OOP",
                List.of("oop", "object oriented programming"));
        skills.put("Data Structures",
                List.of("data structures", "dsa"));
        skills.put("Algorithms", List.of("algorithms"));

        return Collections.unmodifiableMap(skills);
    }
}