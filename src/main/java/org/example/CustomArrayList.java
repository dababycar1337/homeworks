package org.example;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class CustomArrayList<E> implements CustomList<E> {
    private static final int default_capacity = 10;
    private static final double growth_factor = 1.5;

    private Object[] elements;
    private int size;

    public CustomArrayList() {
        this(default_capacity);
    }

    public CustomArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity must be >= 0");
        }
        this.elements = new Object[initialCapacity];
    }

    @Override
    public void add(E element) {
        Objects.requireNonNull(element, "element");
        ensureCapacity(size + 1);
        elements[size++] = element;
    }

    @Override
    @SuppressWarnings("unchecked")
    public E get(int index) {
        checkIndex(index);
        return (E) elements[index];
    }

    @Override
    @SuppressWarnings("unchecked")
    public E remove(int index) {
        checkIndex(index);
        E removed = (E) elements[index];
        int moved = size - index - 1;
        if (moved > 0) {
            System.arraycopy(elements, index + 1, elements, index, moved);
        }
        elements[--size] = null;
        return removed;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public E next() {
                if (cursor >= size) {
                    throw new NoSuchElementException();
                }
                return get(cursor++);
            }
        };
    }

    private void ensureCapacity(int required) {
        if (required <= elements.length) return;
        int newCapacity = (int) Math.ceil(elements.length * growth_factor);
        if (newCapacity < required) newCapacity = required;
        if (newCapacity <= 0) newCapacity = default_capacity;
        elements = Arrays.copyOf(elements, newCapacity);
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", size: " + size);
        }
    }
}