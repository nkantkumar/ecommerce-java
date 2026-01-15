package com.fix.misc;

import java.util.Comparator;
import java.util.PriorityQueue;

public class CustomPriority {

    static class Task {
        final String name;

        public int getPriority() {
            return priority;
        }

        final int priority; // smaller = higher priority

        Task(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }
    }

    public static void main(String[] args) {

        Comparator<Task> byPriority = Comparator.comparingInt(t -> t.priority);
        Comparator<Task> byPriority1 = Comparator.comparingInt(Task::getPriority);
        PriorityQueue<Task> taskQueue = new PriorityQueue<>(byPriority);

        PriorityQueue<Task> taskQueueRev = new PriorityQueue<>(byPriority.reversed());

        taskQueue.offer(new Task("Low", 5));
        taskQueue.offer(new Task("High", 1));
        taskQueue.offer(new Task("Medium", 3));

        taskQueueRev.offer(new Task("Low", 5));
        taskQueueRev.offer(new Task("High", 1));
        taskQueueRev.offer(new Task("Medium", 3));

        while (!taskQueue.isEmpty()) {
            Task t = taskQueue.poll();
            System.out.println(t.name + " " + t.priority);
        }

        while (!taskQueueRev.isEmpty()) {
            Task t = taskQueueRev.poll();
            System.out.println(t.name + " " + t.priority);
        }
    }

}
