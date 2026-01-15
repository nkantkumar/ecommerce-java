package com.fix.misc;

import java.util.List;

public final class PaymentEngine {

    private final String name ;
    private final List<String> legalCountries;

    public String getName() {
        return name;
    }

    public List<String> getLegalCountries() {
        return List.copyOf(legalCountries);
    }

    public PaymentEngine(String name, List<String> legalCountries) {
        this.name = name;
        this.legalCountries = List.copyOf(legalCountries);
    }

}
