package com.distributed.ecommerce.hybrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImmutableTeamTest {
    public static void main(String[] args) {
        List<String> original = new ArrayList<>(Arrays.asList("Alice", "Bob"));
        ImmutableTeam team = new ImmutableTeam(original);
        original.add("Charlie");  // Doesn't affect team
        team.getMembers().add("Dave");
        System.out.println(team.getMembers());
    }
}
