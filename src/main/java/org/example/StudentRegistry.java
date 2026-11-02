package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public final class StudentRegistry {
    private StudentRegistry() {}

    public static Map<Integer, Student> toHashMap(Iterable<Student> students) {
        Map<Integer, Student> map = new HashMap<>();
        for (Student s : students) map.put(s.id(), s);
        return map;
    }

    public static NavigableMap<Integer, Student> toDescendingTreeMap(Iterable<Student> students) {
        NavigableMap<Integer, Student> map = new TreeMap<>((a, b) -> Integer.compare(b, a));
        for (Student s : students) map.put(s.id(), s);
        return map;
    }

    public static List<Student> findStudentsByGradeRange(Map<Integer, Student> students, double minGrade, double maxGrade) {
        if (minGrade < 0.0 || maxGrade > 10.0 || minGrade > maxGrade) {
            throw new IllegalArgumentException("Invalid grade range");
        }
        List<Student> result = new ArrayList<>();
        for (Student s : students.values()) {
            if (s.grade() >= minGrade && s.grade() <= maxGrade) result.add(s);
        }
        return result;
    }

    public static List<Student> getTopNStudentsById(NavigableMap<Integer, Student> students, int n) {
        if (n < 0) throw new IllegalArgumentException("n must be >= 0");
        List<Student> result = new ArrayList<>(Math.min(n, students.size()));
        if (n == 0) return result;
        for (Student s : students.values()) {
            if (result.size() == n) break;
            result.add(s);
        }
        return result;
    }
}