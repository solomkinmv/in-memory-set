package in.solomk.inmemoryset.service;

import in.solomk.inmemoryset.exception.InvalidSetStateException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

/**
 * Implementation of a hash table with open addressing.
 */
@Slf4j
public class HashTable {
    private final int initialCapacity;
    private final double loadFactor;
    private final double growthFactor;
    private final double downscalingFactor;
    private Integer[] array;
    @Getter
    private int size;

    public HashTable(int initialCapacity, double loadFactor, double growthFactor, double downscalingFactor) {
        validateInputParameters(initialCapacity, loadFactor, growthFactor, downscalingFactor);

        this.initialCapacity = initialCapacity;
        this.loadFactor = loadFactor;
        this.growthFactor = growthFactor;
        this.downscalingFactor = downscalingFactor;
        array = new Integer[initialCapacity];
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
        while (array[index] != null) {
            index = (index + 1) % array.length;
        }

        array[index] = value;
        size++;
        return true;
    }

    public boolean remove(int value) {
        if (!contains(value)) {
            return false;
        }

        int index = hash(value);
        while (!array[index].equals(value)) {
            index = (index + 1) % array.length;
        }

        array[index] = null;
        size--;

        if (shouldDownscale()) {
            downscale();
        }

        return true;
    }

    public boolean contains(int value) {
        int index = hash(value);
        final int initialIndex = index;
        while (array[index] != null) {
            if (array[index].equals(value)) {
                return true;
            }
            index = (index + 1) % array.length;

            if (index == initialIndex) {
                break;
            }
        }
        return false;
    }

    boolean isFull() {
        return size >= array.length * loadFactor;
    }

    private int hash(int value) {
        return Math.abs(value % array.length);
    }

    private void resize() {
        Integer[] oldArray = array;
        long nextCapacity = (long) (array.length * growthFactor);
        if (nextCapacity <= initialCapacity) {
            nextCapacity = initialCapacity;
        } else if (nextCapacity > Integer.MAX_VALUE) {
            nextCapacity = Integer.MAX_VALUE;
        }
        array = new Integer[(int) nextCapacity];
        size = 0;

        for (Integer value : oldArray) {
            if (value != null) {
                add(value);
            }
        }
    }

    private boolean shouldDownscale() {
        return size <= array.length * downscalingFactor && array.length > initialCapacity;
    }

    private void downscale() {
        Integer[] oldArray = array;
        int nextCapacity = Math.max((int) (array.length * downscalingFactor), initialCapacity);
        array = new Integer[nextCapacity];
        size = 0;

        for (Integer value : oldArray) {
            if (value != null) {
                add(value);
            }
        }
    }
}
