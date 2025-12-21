package com.distributed.ecommerce.hybrid;

import java.util.List;
import java.util.stream.Stream;

public class Java17Test {
    public static void main(String[] args) {
        List<Integer> numbers = Stream.of(1, 2, 3, 4, 5)
                .filter(n -> n % 2 == 0)
                .toList();

        System.out.println(numbers);

    }

    public sealed interface Payment
            permits CreditCard, PayPal, BankTransfer {
        void process();
    }

    final class CreditCard implements Payment {
        public void process() { /* ... */ }
    }

    final class PayPal implements Payment {
        public void process() { /* ... */ }
    }

    non-sealed class BankTransfer implements Payment {
        // non-sealed allows further subclassing
        public void process() { /* ... */ }
    }

}
