package in.solomk.inmemoryset.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InMemorySetServiceTest {

    @Mock
    private HashTable hashTable;

    private InMemorySetService service;

    @BeforeEach
    void setUp() {
        service = new InMemorySetService(hashTable);
    }

    @Test
    void givenTrueResponseOnHashTableAdd_whenAddItem_thenReturnsTrue() {
        when(hashTable.add(1)).thenReturn(true);

        boolean added = service.addItem(1);

        assertThat(added).isTrue();
    }

    @Test
    void givenFalseResponseOnHashTableAdd_whenAddItem_thenReturnsFalse() {
        when(hashTable.add(1)).thenReturn(false);

        boolean added = service.addItem(1);

        assertThat(added).isFalse();
    }

    @Test
    void givenTrueResponseOnHashTableRemove_whenRemoveItem_thenReturnsTrue() {
        when(hashTable.remove(1)).thenReturn(true);

        boolean removed = service.removeItem(1);

        assertThat(removed).isTrue();
    }

    @Test
    void givenFalseResponseOnHashTableRemove_whenRemoveItem_thenReturnsFalse() {
        when(hashTable.remove(1)).thenReturn(false);

        boolean removed = service.removeItem(1);

        assertThat(removed).isFalse();
    }

    @Test
    void givenTrueResponseOnHashTableContains_whenHasItem_thenReturnsTrue() {
        when(hashTable.contains(1)).thenReturn(true);

        boolean hasItem = service.hasItem(1);

        assertThat(hasItem).isTrue();
    }

    @Test
    void givenFalseResponseOnHashTableContains_whenHasItem_thenReturnsFalse() {
        when(hashTable.contains(1)).thenReturn(false);

        boolean hasItem = service.hasItem(1);

        assertThat(hasItem).isFalse();
    }
}
