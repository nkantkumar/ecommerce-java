package com.distributed.ecommerce.hybrid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListTest {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");

        Iterator<String> it = list.iterator(); // expectedModCount = 3
        list.remove("B");
        it = list.iterator();   // modCount becomes 4
        it.next(); // ConcurrentModificationException!
    }
}
