package in.solomk.inmemoryset.service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InMemorySetService {

    private final HashTable hashTable;

    public boolean addItem(int item) {
        return hashTable.add(item);
    }

    public boolean removeItem(int item) {
        return hashTable.remove(item);
    }

    public boolean hasItem(int item) {
        return hashTable.contains(item);
    }
}
