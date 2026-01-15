package com.fix.misc;

public class StringAppCount {
    public static void main(String[] args) {
        String text = "Java is great. Java is fun. Java is powerful.";
        String sub = "Java";
        System.out.println("Occurrences: " + countOccurrences(text, sub));  // Output: 3
    }

    private static int countOccurrences(String text, String sub) {
        int count =0;
        int index =0;
        while ((index = text.indexOf(sub, index)) != -1) {
            count++;
            index+= sub.length();

        }
        return count;
    }
}
