package com.distributed.ecommerce.hybrid;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;



public class StringSplit {

    public static void main(String[] args) {
        String n= "";
        System.out.println(reverseNumber(n));


        String num =",1,,2,3,,";
        List<String> list = Arrays.stream(num.split(","))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        //System.out.println(list);
    }

    private static int reverseNumber(String n) {
        if(n.isEmpty()){
            return 0;
        }
        return 0;
    }
}
