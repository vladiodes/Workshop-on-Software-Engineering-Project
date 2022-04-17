package main.utils;

public class stringFunctions {
    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
    private static int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    /***
     * brought to you by: https://www.baeldung.com/java-levenshtein-distance
     * @param x 1st string
     * @param y 2nd string
     * @return distance between strings
     */
    public static int calculate_distance(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];

        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1]
                                    + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        return dp[x.length()][y.length()];
    }
}
