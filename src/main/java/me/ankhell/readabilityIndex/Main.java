package me.ankhell.readabilityIndex;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            if (args == null) throw new IOException("Args are null");
            Path path = Paths.get(args[0]);
            String input = new String(Files.readAllBytes(path));
            System.out.println("The text is:");
            System.out.println(input);
            long sentences = Arrays.stream(input.split("[?!.]")).count();
            long words = Arrays.stream(input.split(",?[?!.]?\\s++")).count();
            int characters = input.replaceAll("\\s++", "").length();
            int syllables = Arrays
                    .stream(input.split(",?[?!.]?\\s++"))
                    .mapToInt(Main::countSyllables)
                    .sum();
            long polySyllables = Arrays
                    .stream(input.split(",?[?!.]?\\s++"))
                    .mapToInt(Main::countSyllables)
                    .filter(x -> x > 2)
                    .count();
            System.out.println(
                    String.format(Locale.ENGLISH,
                                  "Words: %d%nSentences: %d%nCharacters: %d%nSyllables: %d%nPolysyllables:%d",
                                  words,
                                  sentences,
                                  characters,
                                  syllables,
                                  polySyllables
                    )
            );

            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            String choice = reader.readLine();
            if ("all".equals(choice)) {
                choice = "ARI, FK, SMOG, CL";
            }
            StringBuilder stringBuilder = new StringBuilder("\n");
            double avgAge = 0D;
            if (choice.contains("ARI")) {
                double result = calculateARI(characters, words, sentences);
                avgAge = setOrUpdateAvgAge(avgAge, result);
                stringBuilder.append(getResultString(result, ReadabilityIndex.ARI));
            }
            if (choice.contains("FK")) {
                double result = calculateFK(words, sentences, syllables);
                avgAge = setOrUpdateAvgAge(avgAge, result);
                stringBuilder.append(getResultString(result, ReadabilityIndex.FK));
            }
            if (choice.contains("SMOG")) {
                double result = calculateSMOG(polySyllables, sentences);
                avgAge = setOrUpdateAvgAge(avgAge, result);
                stringBuilder.append(getResultString(result, ReadabilityIndex.SMOG));
            }
            if (choice.contains("CL")) {
                double result = calculateCL(sentences, words, characters);
                avgAge = setOrUpdateAvgAge(avgAge, result);
                stringBuilder.append(getResultString(result, ReadabilityIndex.CL));
            }
            stringBuilder.append("\nThis text should be understood in average by ")
                         .append(avgAge)
                         .append(" year olds");
            System.out.println(stringBuilder.toString());

        } catch (NoSuchFileException e) {
            System.out.println(e.getFile());
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }

    public static double setOrUpdateAvgAge(double avgAge, double result) {
        return avgAge == 0
                ? AutomatedReadabilityIndex.getByScore((int) Math.round(result)).getIntAge()
                : (avgAge + AutomatedReadabilityIndex.getByScore((int) Math.round(result)).getIntAge()) / 2;
    }

    public static double calculateFK(long words, long sentences, long syllables) {
        return 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
    }

    public static double calculateARI(long characters, long words, long sentences) {
        return 4.71 * ((double) characters / words) + 0.5 * ((double) words / sentences) - 21.43;
    }

    public static double calculateSMOG(long polysyllables, long sentences) {
        return 1.043 * Math.sqrt((double) polysyllables * (30D / sentences)) + 3.1391;
    }

    public static double calculateCL(long sentences, long words, long characters) {
        double l = ((double) characters / words) * 100;
        double s = ((double) sentences / words) * 100;
        return 0.0588 * l - 0.296 * s - 15.8;
    }

    public static String getResultString(double score, @NotNull ReadabilityIndex readabilityIndex) {
        return String.format(
                "%s: %.2f (about %d year olds).%n",
                readabilityIndex.getOutputName(),
                score,
                AutomatedReadabilityIndex.getByScore((int) Math.round(score)).getIntAge()
        );
    }

    public static int countSyllables(@NotNull String input) {
        String insideString = input;
        int matches = 0;
        int from = 0;
        Pattern pattern = Pattern.compile("([aeiouyAEIOUY][^aeiouyAEIOUY\\s]|[aiouyAIOUY]$)");
        if (input.charAt(input.length() - 1) == 'e') {
            insideString = insideString.substring(0, insideString.length() - 1);
        }
        Matcher matcher = pattern.matcher(insideString);
        while (matcher.find(from)) {
            matches++;
            from = matcher.start() + 1;
        }
        return matches > 0 ? matches : 1;
    }
}

