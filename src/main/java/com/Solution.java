package com;
import com.tablebooking.SampleNormalizer;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.*;

class Solution {
    private SampleNormalizer normalizer;

    public Stream<BigDecimal> solution(Stream<BigDecimal> input,
                                       SampleNormalizer normalizer) {

        if (input == null) {
            return Stream.empty();
        }

        // Step 1: remove nulls and negative values
        List<BigDecimal> filtered = input
                .filter(Objects::nonNull)
                .filter(v -> v.compareTo(BigDecimal.ZERO) >= 0)
                .collect(Collectors.toList());

        // Step 2 + 3:
        // divide into triples, keep only complete triples,
        // remove triples whose average > 30
        List<BigDecimal> processed = IntStream
                .range(0, filtered.size() / 3)
                .mapToObj(i -> filtered.subList(i * 3, i * 3 + 3))
                .filter(triple -> {
                    BigDecimal sum = triple.stream()
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal avg = sum.divide(BigDecimal.valueOf(3));

                    return avg.compareTo(BigDecimal.valueOf(30)) <= 0;
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());

        // Step 4 + 5:
        // normalize and eliminate empty optionals/nulls
        return processed.stream()
                .map(normalizer::normalize)
                .filter(Objects::nonNull)
                .flatMap(Optional::stream)
                .filter(Objects::nonNull)
                .flatMap(Optional::stream)
                .filter(Objects::nonNull);
    }
}