package com.fix.misc;



import java.util.Arrays;
import java.util.HashMap;

public class StringCount {
    public static void main(String[] args) {
        String str = "APPL GOOG APPL AMAZN";
        var map = new HashMap<String, Integer>();

        for (String st : str.split(" ")){
            map.put(st,map.getOrDefault(st, 0)+1);

        }
        System.out.println(map);

        int[] i= {1,2,4,3};

        System.out.println(Arrays.stream(i).sum());
    }
}
