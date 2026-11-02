package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.util.Iterator;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

class CustomArrayListTest {
    @Test
    void newListIsEmpty() {
        CustomArrayList<String> list = new CustomArrayList<>();
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    void addPreservesOrder() {
        CustomArrayList<String> list = new CustomArrayList<>();
        list.add("a"); list.add("b"); list.add("c");
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    void addingMoreThanTenElementsGrowsWithoutDataLoss() {
        CustomArrayList<Integer> list = new CustomArrayList<>();
        for (int i = 0; i < 25; i++) list.add(i);
        assertEquals(25, list.size());
        for (int i = 0; i < 25; i++) assertEquals(i, list.get(i));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 4})
    void getWorksForFirstMiddleAndLast(int index) {
        CustomArrayList<Integer> list = new CustomArrayList<>();
        for (int i = 0; i < 5; i++) list.add(i);
        assertEquals(index, list.get(index));
    }

    @Test
    void getRejectsInvalidIndices() {
        CustomArrayList<Integer> list = new CustomArrayList<>();
        list.add(1); list.add(2);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
    }

    @Test
    void removeRejectsInvalidIndices() {
        CustomArrayList<Integer> list = new CustomArrayList<>();
        list.add(1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.remove(1));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 2, 4})
    void removeFirstMiddleLastReturnsCorrectValue(int index) {
        CustomArrayList<Integer> list = new CustomArrayList<>();
        for (int i = 0; i < 5; i++) list.add(i);
        assertEquals(index, list.remove(index));
        assertEquals(4, list.size());
    }

    @Test
    void addNullThrowsNpe() {
        CustomArrayList<String> list = new CustomArrayList<>();
        assertThrows(NullPointerException.class, () -> list.add(null));
    }

    @Test
    void iteratorVisitsElementsInOrder() {
        CustomArrayList<String> list = new CustomArrayList<>();
        list.add("x"); list.add("y");
        Iterator<String> it = list.iterator();
        assertEquals("x", it.next());
        assertEquals("y", it.next());
        assertFalse(it.hasNext());
    }

    @Test
    void exhaustedIteratorThrowsNoSuchElement() {
        CustomArrayList<String> list = new CustomArrayList<>();
        list.add("only");
        Iterator<String> it = list.iterator();
        it.next();
        assertThrows(NoSuchElementException.class, it::next);
    }
}