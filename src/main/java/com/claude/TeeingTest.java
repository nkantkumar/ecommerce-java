package com.claude;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.*;
public class TeeingTest {
    public static void main(String[] args) {
        var result = Stream.of(1,2,3,4,5)
                .collect(Collectors.teeing(
                        Collectors.summingInt(i -> i),
                        Collectors.counting(),
                        (sum, count) -> sum + " / " + count
                ));

        System.out.println(result);

        var minMax = Stream.of(3,7,2,9,5)
                .collect(Collectors.teeing(
                        Collectors.minBy(Integer::compare),
                        Collectors.maxBy(Integer::compare),
                        (min, max) -> Map.of("min", min, "max", max)
                ));

        System.out.println(minMax);

    }

}
