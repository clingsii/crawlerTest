package com.lc.crawler.utils;



/**
 * Created by Ling Cao on 2016/8/16.
 */
public class StrUtil {

    public static final String encode = "UTF-8";


    private static int LowerOfThree(int first, int second, int third) {
        int min = Math.min(first, second);
        return Math.min(min, third);
    }

    private static int levenshteinDistance(String str1, String str2) {
        int[][] Matrix;
        int n = str1.length();
        int m = str2.length();

        int temp = 0;
        char ch1;
        char ch2;
        int i = 0;
        int j = 0;
        if (n == 0) {
            return m;
        }
        if (m == 0) {

            return n;
        }
        Matrix = new int[n + 1][m + 1];

        for (i = 0; i <= n; i++) {
            Matrix[i][0] = i;
        }

        for (j = 0; j <= m; j++) {
            Matrix[0][j] = j;
        }

        for (i = 1; i <= n; i++) {
            ch1 = str1.charAt(i - 1);
            for (j = 1; j <= m; j++) {
                ch2 = str2.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                Matrix[i][j] = LowerOfThree(Matrix[i - 1][j] + 1, Matrix[i][j - 1] + 1, Matrix[i - 1][j - 1] + temp);
            }
        }
        return Matrix[n][m];
    }

    /// string similarity
    public static float levenshteinDistancePercent(String str1, String str2) {
        int val = levenshteinDistance(str1, str2);
        float value = 1 - (float) val / Math.max(str1.length(), str2.length());
        return value;
    }



    public static void main(String[] args) {
        String s1 = "http://www.sli-demo.com/wishlist/index/add/product/422/form_key/h3OmC1mlgwZzcgDc/";
        String s2 = "     http://www.sli-demo.co              m/wis  hlist/index                      /add/product/423/form_key/h3OmC1mlgwZzcgDc/";


        System.out.println(s2.replaceAll("\\s{2,}"," "));

    }
}
