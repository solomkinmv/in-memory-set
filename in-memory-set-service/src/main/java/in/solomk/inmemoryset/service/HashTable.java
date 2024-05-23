package in.solomk.inmemoryset.service;

import in.solomk.inmemoryset.exception.InvalidSetStateException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implementation of a hash table with open addressing.
 */
@Slf4j
public class HashTable {
    private static final long DELETED = Integer.MIN_VALUE - 1L;
    private final int initialCapacity;
    private final double loadFactor;
    private final double growthFactor;
    private final double downscalingFactor;
    private volatile Long[] array;
    @Getter
    private volatile int size;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public HashTable(int initialCapacity, double loadFactor, double growthFactor, double downscalingFactor) {
        validateInputParameters(initialCapacity, loadFactor, growthFactor, downscalingFactor);

        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.growthFactor = growthFactor;
        this.downscalingFactor = downscalingFactor;
        array = new Long[initialCapacity];
        size = 0;
        log.debug("Created HashTable with initial capacity {}, load factor {}, and growth factor {}",
                initialCapacity, loadFactor, growthFactor);
    }

    private static void validateInputParameters(int initialCapacity, double loadFactor, double growthFactor, double downscalingFactor) {
        Assert.isTrue(initialCapacity > 0, "Initial capacity must be greater than 0");
        Assert.isTrue(loadFactor > 0, "Load factor must be greater than 0");
        Assert.isTrue(loadFactor <= 1, "Load factor must be less than or equal to 1");
        Assert.isTrue(growthFactor >= 1, "Growth factor must be greater or equal to 1");
        Assert.isTrue(downscalingFactor >= 0, "Downscaling factor must be greater or equal to 0");
        Assert.isTrue(downscalingFactor <= 1, "Downscaling factor must be less or equal to 1");
    }

    public boolean add(int value) {
        lock.writeLock().lock();
        try {
            if (contains(value)) {
                return false;
            }

            if (isFull()) {
                if (growthFactor == 1) {
                    log.debug("HashTable is full and can't be resized. Throwing exception.");
                    throw new InvalidSetStateException("HashTable is full and can't be resized. Please remove some elements first.");
                }
                log.debug("HashTable is full. Resizing.");
                resize();
            }

            int index = hash(value);
            while (array[index] != null && array[index] != DELETED) {
                index = (index + 1) % array.length;
            }

            array[index] = (long) value;
            size++;
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean remove(int value) {
        lock.writeLock().lock();
        try {
            if (!contains(value)) {
                return false;
            }

            int index = hash(value);
            while (array[index] != null) {
                if (array[index].equals((long) value)) {
                    array[index] = DELETED;
                    size--;
                    if (shouldDownscale()) {
                        downscale();
                    }
                    return true;
                }
                index = (index + 1) % array.length;
            }
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean contains(int value) {
        lock.readLock().lock();
        try {
            int index = hash(value);
            final int initialIndex = index;
            while (array[index] != null) {
                if (array[index].equals((long) value)) {
                    return true;
                }
                index = (index + 1) % array.length;

                if (index == initialIndex) {
                    break;
                }
            }
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    boolean isFull() {
        return size >= array.length * loadFactor;
    }

    private int hash(int value) {
        return Math.abs(value % array.length);
    }

    private void resize() {
        Long[] oldArray = array;
        long nextCapacity = (long) (array.length * growthFactor);
        if (nextCapacity <= initialCapacity) {
            nextCapacity = initialCapacity;
        } else if (nextCapacity > Integer.MAX_VALUE) {
            nextCapacity = Integer.MAX_VALUE;
        }
        array = new Long[(int) nextCapacity];
        size = 0;

        for (Long value : oldArray) {
            if (value != null && value != DELETED) {
                add(Math.toIntExact(value));
            }
        }
    }

    private boolean shouldDownscale() {
        return size <= array.length * downscalingFactor && array.length > initialCapacity;
    }

    private void downscale() {
        Long[] oldArray = array;
        int nextCapacity = Math.max((int) (array.length * downscalingFactor), initialCapacity);
        array = new Long[nextCapacity];
        size = 0;

        for (Long value : oldArray) {
            if (value != null && value != DELETED) {
                add(Math.toIntExact(value));
            }
        }
    }
}
