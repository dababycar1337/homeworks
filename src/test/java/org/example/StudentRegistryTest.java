package org.example;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import static org.junit.jupiter.api.Assertions.*;

class StudentRegistryTest {
    private static List<Student> sample() {
        return List.of(new Student(1, "Ann", 5.0), new Student(2, "Bob", 7.5),
                new Student(3, "Cara", 10.0), new Student(4, "Dan", 0.0), new Student(5, "Eve", 5.0));
    }

    @Test
    void studentRejectsInvalidFields() {
        assertThrows(IllegalArgumentException.class, () -> new Student(0, "x", 5));
        assertThrows(IllegalArgumentException.class, () -> new Student(-1, "x", 5));
        assertThrows(NullPointerException.class, () -> new Student(1, null, 5));
        assertThrows(IllegalArgumentException.class, () -> new Student(1, " ", 5));
        assertThrows(IllegalArgumentException.class, () -> new Student(1, "x", -0.1));
        assertThrows(IllegalArgumentException.class, () -> new Student(1, "x", 10.1));
    }

    @Test
    void gradeRangeIncludesBothBounds() {
        Map<Integer, Student> map = StudentRegistry.toHashMap(sample());
        List<Student> result = StudentRegistry.findStudentsByGradeRange(map, 5.0, 7.5);
        assertEquals(3, result.size());
    }

    @Test
    void gradeRangeRejectsBadBounds() {
        Map<Integer, Student> map = StudentRegistry.toHashMap(sample());
        assertThrows(IllegalArgumentException.class, () -> StudentRegistry.findStudentsByGradeRange(map, -0.1, 5.0));
        assertThrows(IllegalArgumentException.class, () -> StudentRegistry.findStudentsByGradeRange(map, 5.0, 10.1));
        assertThrows(IllegalArgumentException.class, () -> StudentRegistry.findStudentsByGradeRange(map, 8.0, 3.0));
    }

    @Test
    void topNReturnsDescendingIds() {
        NavigableMap<Integer, Student> map = StudentRegistry.toDescendingTreeMap(sample());
        List<Student> top = StudentRegistry.getTopNStudentsById(map, 3);
        assertEquals(3, top.size());
        assertEquals(5, top.get(0).id());
        assertEquals(4, top.get(1).id());
        assertEquals(3, top.get(2).id());
    }

    @Test
    void topNRejectsNegative() {
        NavigableMap<Integer, Student> map = StudentRegistry.toDescendingTreeMap(sample());
        assertThrows(IllegalArgumentException.class, () -> StudentRegistry.getTopNStudentsById(map, -1));
    }
}