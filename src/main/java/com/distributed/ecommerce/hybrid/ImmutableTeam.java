package com.distributed.ecommerce.hybrid;

import java.util.ArrayList;
import java.util.List;

public final class ImmutableTeam {
    private final List<String> members;

    public ImmutableTeam(List<String> members) {
        // Defensive copy on input
        this.members = new ArrayList<>(members);
    }

    public List<String> getMembers() {
        // Defensive copy on output
        return new ArrayList<>(members);
    }
}