package com.tablebooking;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Solu1 {
    interface SampleNormalizer {
        Optional<Optional<BigDecimal>> normalize(BigDecimal value);
    }


    public Stream<BigDecimal> solution(Stream<BigDecimal> samples, SampleNormalizer normalizer) {
        // Defensive check: You mustn't return null
        if (samples == null || normalizer == null) {
            return Stream.empty();
        }

        // 1. Eliminate all null and negative values (noises)
        List<BigDecimal> filteredList = samples
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
        // Map and eliminate empty/null optionals.
        return joinedStream
                .map(val -> safeNormalize(val, normalizer))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }
    private Optional<BigDecimal> safeNormalize(BigDecimal val, SampleNormalizer normalizer) {
        if (val == null || normalizer == null) {
            return Optional.empty();
        }
        try {
            Optional<Optional<BigDecimal>> outerOpt = normalizer.normalize(val);
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
            // Defensive: if the buggy normalizer throws any exception, treat it as empty
            return Optional.empty();
        }
    }
}
