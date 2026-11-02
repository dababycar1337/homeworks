package org.example;

import java.util.Objects;

public record Student(int id, String name, double grade) {
    public Student {
        if (id <= 0) throw new IllegalArgumentException("id must be positive");
        Objects.requireNonNull(name, "name");
        if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        if (grade < 0.0 || grade > 10.0) throw new IllegalArgumentException("grade must be in [0, 10]");
    }
}