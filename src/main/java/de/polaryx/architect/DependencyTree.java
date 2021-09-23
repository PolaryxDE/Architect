package de.polaryx.architect;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.*;

class DependencyTree {

    @Getter private final List<Entry> entries;

    DependencyTree() {
        this.entries = new ArrayList<>();
    }

    void add(Entry entry) {
        this.entries.add(entry);
    }

    void increment(Class<? extends IService> handle, int value) {
        for (Entry entry : this.entries) {
            if (entry.handle.equals(handle)) {
                entry.value += value;
            } else if (entry.dependencies.contains(handle)) {
                this.increment(entry.handle, value + 1);
            }
        }
    }

    int getRise(Class<? extends IService> parent) {
        for (Entry entry : this.entries) {
            if (entry.handle.equals(parent)) {
                return entry.value + 1;
            }
        }
        return 1;
    }

    void finish() {
        this.entries.sort(Comparator.comparingInt(o -> o.value));
    }

    @Getter @RequiredArgsConstructor
    static class Entry {
        @Setter private int value = 0;
        private final Class<? extends IService> handle;
        private final List<Class<? extends IService>> dependencies = new ArrayList<>();
        private final Map<Class<? extends IService>, Field> instanceFields = new HashMap<>();
    }
}
