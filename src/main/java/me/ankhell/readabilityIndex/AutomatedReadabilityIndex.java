package me.ankhell.readabilityIndex;

public enum AutomatedReadabilityIndex {
    KINDERGARTEN(1, "5-6", 6),
    FIRST_SECOND_GRADE(2, "6-7", 7),
    THIRD_GRADE(3, "7-9", 9),
    FOURTH_GRADE(4, "9-10", 10),
    FIFTH_GRADE(5, "10-11", 11),
    SIXTH_GRADE(6, "11-12", 12),
    SEVENTH_GRADE(7, "12-13", 13),
    EIGHTH_GRADE(8, "13-14", 14),
    NINTH_GRADE(9, "14-15", 15),
    TENTH_GRADE(10, "15-16", 16),
    ELEVENTH_GRADE(11, "16-17", 17),
    TWELFTH_GRADE(12, "17-18", 18),
    COLLEGE_STUDENT(13, "18-24", 24),
    PROFESSOR(14, "24+", 25),
    INCORRECT(-1, "no such age", -1);

    private int score;
    private String age;
    private int intAge;

    AutomatedReadabilityIndex(int score, String age, int intAge) {
        this.score = score;
        this.age = age;
        this.intAge = intAge;
    }

    public static AutomatedReadabilityIndex getByScore(int score) {
        for (AutomatedReadabilityIndex ageScore : AutomatedReadabilityIndex.values()) {
            if (ageScore.score == score) {
                return ageScore;
            }
        }
        return INCORRECT;
    }

    public int getScore() {
        return score;
    }

    public String getAge() {
        return age;
    }

    public int getIntAge() {
        return intAge;
    }
}
