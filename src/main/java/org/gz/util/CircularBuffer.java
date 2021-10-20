package org.gz.util;

import java.nio.BufferOverflowException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

import static java.lang.Math.min;


public class CircularBuffer<T> {

    private final AtomicLong index = new AtomicLong(0);
    private final AtomicLong count = new AtomicLong(0);
    private final AtomicReferenceArray<T> buffer;
    private final int size;

    public CircularBuffer(int size) {
        assert size > 0 : "Size must be positive";
        this.size = size;
        buffer = new AtomicReferenceArray<T>(this.size);
    }

    public int size() {
        return size;
    }

    public void add(T item) {
        assert item != null : "Item must be non-null";
        buffer.set((int) (index.getAndIncrement() % size), item);
        count.getAndIncrement();
    }

    // called by one thread
    public List<T> drain() {
        int _count = (int) min(count.get(), size);
        List<T> result = new ArrayList<T>(_count);
        int idx = -1;
        if (_count == size) {
            idx = (int) index.get();
        }
        for(int i = 0; i < _count; i ++) {
            idx++;
            if (idx == size) idx = 0;
            result.add(buffer.get(idx));
            buffer.set(idx, null);
        }
        count.set(0);
        index.set(0);
        return result;
    }

    public AtomicLong index() {
        return new AtomicLong(index.get());
    }
}