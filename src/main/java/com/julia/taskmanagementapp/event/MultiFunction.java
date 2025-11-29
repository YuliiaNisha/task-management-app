package com.julia.taskmanagementapp.event;

@FunctionalInterface
public interface MultiFunction<A, B, C, D, E, R> {
    R apply(A a, B b, C c, D d, E e);
}
