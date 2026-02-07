package com.capitalmarkets;

import java.util.*;

public class OOMHeap {
    public static void main(String[] args) {
        List<byte[]> list = new ArrayList<>();
        while (true) {
            list.add(new byte[10 * 1024 * 1024]); // 10MB
        }
    }
}

