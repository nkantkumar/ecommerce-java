package com;

import java.util.*;
import com.tablebooking.SampleNormalizer;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.groupingBy;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// interface SampleNormalizer {
//     Optional<Optional<BigDecimal>> normalize(BigDecimal value);
// }

class SamplePreprocessor {

    private final SampleNormalizer normalizer;

    SamplePreprocessor(SampleNormalizer normalizer) {
        this.normalizer = normalizer;
    }

    Stream<BigDecimal> preprocess(Stream<BigDecimal> input) {
        // Defensive check: You mustn't return null
        if (input == null) {
            return Stream.empty();
        }

        // 1. Eliminate all null and negative values (noises)
        List<BigDecimal> filteredList = input
                .filter(s -> s != null && s.compareTo(BigDecimal.ZERO) >= 0)
                .collect(Collectors.toList());

        // 2. Divide the data into triple-groups of three samples and retain only complete triples
        List<List<BigDecimal>> triples = new ArrayList<>();
        for (int i = 0; i + 2 < filteredList.size(); i += 3) {
            triples.add(Arrays.asList(
                    filteredList.get(i),
                    filteredList.get(i + 1),
                    filteredList.get(i + 2)
            ));
        }

        Stream<BigDecimal> joinedStream = triples.stream()
                .filter(triple -> {
                    BigDecimal sum = triple.get(0).add(triple.get(1)).add(triple.get(2));
                    return sum.compareTo(new BigDecimal("90")) <= 0;
                })
                .flatMap(List::stream);

        // 5. Use SampleNormalizer to normalize the values.
        // 6. Return a Stream<BigDecimal>, eliminating empty/null optionals.
        return joinedStream
                .map(this::safeNormalize)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    /**
     * Safely normalizes a value using the member normalizer, handling all potential nulls and empty Optionals.
     */
    private Optional<BigDecimal> safeNormalize(BigDecimal val) {
        if (val == null || this.normalizer == null) {
            return Optional.empty();
        }
        try {
            Optional<Optional<BigDecimal>> outerOpt = this.normalizer.normalize(val);
            if (outerOpt == null || !outerOpt.isPresent()) {
                return Optional.empty();
            }

            Optional<BigDecimal> innerOpt = outerOpt.get();
            if (innerOpt == null || !innerOpt.isPresent()) {
                return Optional.empty();
            }

            BigDecimal normalizedValue = innerOpt.get();
            if (normalizedValue == null) {
                return Optional.empty();
            }

            return Optional.of(normalizedValue);
        } catch (Exception e) {
            // If the buggy normalizer throws any exception, treat it as empty
            return Optional.empty();
        }
    }
}
