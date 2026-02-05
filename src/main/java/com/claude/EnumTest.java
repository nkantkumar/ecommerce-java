package com.claude;

enum Day {
    MONDAY, TUESDAY, WEDNESDAY;

    // Constructor is implicitly private
    // Even if you write it explicitly:
    private Day() {
        System.out.println("Creating " + this.name());
    }
}
public class EnumTest {
    public static void main(String[] args) {
        Day today = Day.WEDNESDAY;
        System.out.println("Creating >>>>" );
        Day today1 = Day.WEDNESDAY;

        if(today == today1){
            System.out.println("equalls >>>>" );
        }

        if(today.equals(today1)){
            System.out.println("equalls here >>>>" );
        }

        System.out.println();
    }
}
