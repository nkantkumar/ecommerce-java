package com.tablebooking;

import java.math.BigDecimal;
import java.util.Optional;

public interface SampleNormalizer {
    Optional<Optional<BigDecimal>> normalize(BigDecimal value);
}
