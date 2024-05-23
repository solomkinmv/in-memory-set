package in.solomk.inmemoryset.service;

import in.solomk.inmemoryset.exception.InvalidSetStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HashTableTest {

    private HashTable hashTable;

    @Nested
    class HashTableParametersValidationTests {
        @Test
        void givenNegativeCapacity_whenCreateHashTable_thenThrowException() {
            assertThatThrownBy(() -> new HashTable(-1, 0.75, 2, 0.5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Initial capacity must be greater than 0");
        }

        @Test
        void givenZeroCapacity_whenCreateHashTable_thenThrowException() {
            assertThatThrownBy(() -> new HashTable(0, 0.75, 2, 0.5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Initial capacity must be greater than 0");
        }

        @Test
        void givenNegativeLoadFactor_whenCreateHashTable_thenThrowException() {
            assertThatThrownBy(() -> new HashTable(16, -0.75, 2, 0.5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Load factor must be greater than 0");
        }

        @Test
        void givenZeroLoadFactor_whenCreateHashTable_thenThrowException() {
            assertThatThrownBy(() -> new HashTable(16, 0, 2, 0.5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Load factor must be greater than 0");
        }

        @Test
        void givenNegativeGrowthFactor_whenCreateHashTable_thenThrowException() {
            assertThatThrownBy(() -> new HashTable(16, 0.75, -2, 0.5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Growth factor must be greater or equal to 1");
        }

        @Test
        void givenZeroGrowthFactor_whenCreateHashTable_thenThrowException() {
            assertThatThrownBy(() -> new HashTable(16, 0.75, 0, 0.5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Growth factor must be greater or equal to 1");
        }

        @Test
        void givenNegativeDownscalingFactor_whenCreateHashTable_thenThrowException() {
            assertThatThrownBy(() -> new HashTable(16, 0.75, 2, -0.5))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Downscaling factor must be greater or equal to 0");
        }

        @Test
        void givenOneDownscalingFactor_whenCreateHashTable_thenThrowException() {
            assertThatThrownBy(() -> new HashTable(16, 0.75, 2, 1.01))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Downscaling factor must be less or equal to 1");
        }
    }

    @Nested
    class HashTableWithLoadFactorOneAndGrowthFactorOne {
        private static final int INITIAL_CAPACITY = 16;


        @BeforeEach
        void setUp() {
            hashTable = new HashTable(INITIAL_CAPACITY, 1, 1, 1);
        }

        @Test
        void givenTableIsFull_whenAdd_thenThrowException() {
            for (int i = 0; i < INITIAL_CAPACITY; i++) {
                hashTable.add(i);
            }

            assertThatThrownBy(() -> hashTable.add(INITIAL_CAPACITY))
                    .isInstanceOf(InvalidSetStateException.class)
                    .hasMessage("HashTable is full and can't be resized. Please remove some elements first.");
        }
    }

    @Nested
    class HashTableWithLoadFactorOne {

        private static final int INITIAL_CAPACITY = 16;


        @BeforeEach
        void setUp() {
            hashTable = new HashTable(INITIAL_CAPACITY, 1, 2, 0.5);
        }

        @Test
        void givenTableIsNotFull_whenCheckIfFull_thenReturnsFalse() {
            boolean isFull = hashTable.isFull();

            assertThat(isFull).isFalse();
        }

        @Test
        void givenTableIsFull_whenCheckIfFull_thenReturnsTrue() {
            for (int i = 0; i < INITIAL_CAPACITY; i++) {
                hashTable.add(i);
            }

            boolean isFull = hashTable.isFull();

            assertThat(isFull).isTrue();
        }

        @Test
        void givenTableIsFull_whenAddItem_thenCapacityIncreasedAndItemIsAdded() {
            for (int i = 0; i < INITIAL_CAPACITY; i++) {
                hashTable.add(i);
            }

            hashTable.add(INITIAL_CAPACITY);

            assertThat(hashTable.contains(INITIAL_CAPACITY)).isTrue();
            assertThat(hashTable.isFull()).isFalse();
        }
    }

    @Nested
    class HashTableWithResizableCapacity {

        private static final int INITIAL_CAPACITY = 16;

        @BeforeEach
        void setUp() {
            hashTable = new HashTable(INITIAL_CAPACITY, 0.75, 2, 0.25);
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5})
        void givenValue_whenAdd_thenReturnsTrue(int value) {
            boolean added = hashTable.add(value);

            assertThat(added).isTrue();
        }

        @Test
        void givenValueAlreadyInTable_whenAdd_thenReturnsFalse() {
            hashTable.add(1);

            boolean added = hashTable.add(1);

            assertThat(added).isFalse();
        }

        @Test
        void givenValueInTable_whenRemove_thenReturnsTrue() {
            hashTable.add(1);

            boolean removed = hashTable.remove(1);

            assertThat(removed).isTrue();
        }

        @Test
        void givenValueNotInTable_whenRemove_thenReturnsFalse() {
            boolean removed = hashTable.remove(1);

            assertThat(removed).isFalse();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3, 4, 5})
        void givenValueInTable_whenContains_thenReturnsTrue(int value) {
            hashTable.add(value);

            boolean contains = hashTable.contains(value);

            assertThat(contains).isTrue();
        }

        @Test
        void givenValueNotInTable_whenContains_thenReturnsFalse() {
            boolean contains = hashTable.contains(1);

            assertThat(contains).isFalse();
        }

        @Test
        void givenValuesRemoved_whenAdd_thenHandlesDownscaling() {
            for (int i = 0; i < 16; i++) {
                hashTable.add(i);
            }

            for (int i = 0; i < 8; i++) {
                hashTable.remove(i);
            }

            assertThat(hashTable.isFull()).isFalse();
        }

        @ParameterizedTest
        @ValueSource(ints = {Integer.MAX_VALUE, Integer.MIN_VALUE, -1})
        void givenExtremeValues_whenAdd_thenReturnsTrue(int value) {
            boolean added = hashTable.add(value);

            assertThat(added).isTrue();
            assertThat(hashTable.contains(value)).isTrue();
        }

        @Test
        void givenMultipleEqualValues_whenAdd_thenOnlyOneIsAdded() {
            hashTable.add(1);
            hashTable.add(1);

            assertThat(hashTable.remove(1)).isTrue();
            assertThat(hashTable.contains(1)).isFalse();
        }

        @Test
        void givenValuesWithSameHash_whenAdd_thenHandlesClustering() {
            hashTable.add(1);
            hashTable.add(1 + INITIAL_CAPACITY);

            assertThat(hashTable.contains(1)).isTrue();
            assertThat(hashTable.contains(17)).isTrue();
            assertThat(hashTable.contains(33)).isFalse();
        }

        @Test
        @Timeout(value = 5)
        void givenEmptyTable_whenAddMillionNumbers_thenHashTableIsScaled() {
            for (int i = 0; i < 1_000_000; i++) {
                assertThat(hashTable.add(i)).isTrue();
            }

            for (int i = 0; i < 1_000_000; i++) {
                assertThat(hashTable.contains(i)).isTrue();
            }

            assertThat(hashTable.isFull()).isFalse();
        }

        @Test
        @Disabled
        @Timeout(value = 5)
        void givenTableAtMaxCapacity_whenAdd_thenBehavesCorrectly() {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                hashTable.add(i);
            }

            assertThat(hashTable.add(Integer.MAX_VALUE)).isFalse();
        }

        @Test
        @Disabled
        @Timeout(value = 5)
        void givenTableAtMinCapacity_whenRemove_thenBehavesCorrectly() {
            hashTable.add(1);
            hashTable.remove(1);

            assertThat(hashTable.remove(1)).isFalse();
        }

        @Test
        @Disabled
        @Timeout(value = 5)
        // todo: move concurrency tests to separate class
        void givenConcurrentModifications_whenAdd_thenBehavesCorrectly() throws InterruptedException {
            try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {

                for (int i = 0; i < 1_000_000; i++) {
                    int finalI = i;
                    executorService.submit(() -> hashTable.add(finalI));
                }

                executorService.shutdown();
                //noinspection ResultOfMethodCallIgnored
                executorService.awaitTermination(1, TimeUnit.MINUTES);
            }

            assertThat(hashTable.getSize()).isEqualTo(1_000_000);
        }

        @Test
        @Timeout(value = 5)
        void givenLargeNumberOfElements_whenAdd_thenPerformanceIsAcceptable() {
            for (int i = 0; i < 1_000_000; i++) {
                hashTable.add(i);
            }
        }

        @Test
        @Timeout(value = 5)
        void givenLargeNumberOfElements_whenRemove_thenPerformanceIsAcceptable() {
            for (int i = 0; i < 1_000_000; i++) {
                hashTable.add(i);
            }

            for (int i = 0; i < 1_000_000; i++) {
                hashTable.remove(i);
            }
        }

        @Test
        @Timeout(value = 5)
        void givenLargeNumberOfElements_whenContains_thenPerformanceIsAcceptable() {
            for (int i = 0; i < 1_000_000; i++) {
                hashTable.add(i);
            }

            for (int i = 0; i < 1_000_000; i++) {
                hashTable.contains(i);
            }
        }
    }
}
