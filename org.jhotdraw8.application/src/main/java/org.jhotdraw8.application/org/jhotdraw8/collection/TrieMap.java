/*
 * @(#)TrieMap.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TrieMap<K, V> extends AbstractMap<K, V> implements Serializable, Cloneable {
    private final static long serialVersionUID = 0L;
    private transient UniqueIdentity mutator;
    private TrieMapHelper.BitmapIndexedNode<K, V> root;
    private int size;
    private int modCount;

    public TrieMap() {
        this.root = TrieMapHelper.emptyNode();
    }

    public TrieMap(@NonNull Map<? extends K, ? extends V> m) {
        this.root = TrieMapHelper.emptyNode();
        this.putAll(m);
    }

    public TrieMap(@NonNull ReadOnlyMap<? extends K, ? extends V> m) {
        this.root = TrieMapHelper.emptyNode();
        this.putAll(m.asMap());
    }

    public TrieMap(@NonNull PersistentTrieMap<K, V> trieMap) {
        this.root = trieMap;
        this.size = trieMap.size;
    }

    public TrieMap(@NonNull TrieMap<K, V> trieMap) {
        this.mutator = null;
        trieMap.mutator = null;
        this.root = trieMap.root;
        this.size = trieMap.size;
        this.modCount = 0;
    }

    @Override
    public void clear() {
        this.root = TrieMapHelper.emptyNode();
        this.size = 0;
    }

    @Override
    public boolean containsKey(final @NonNull Object o) {
        @SuppressWarnings("unchecked") final K key = (K) o;
        return root.findByKey(key, Objects.hashCode(key), 0).keyExists();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        // Type arguments are needed for Java 8!
        return new AbstractSet<Entry<K, V>>() {
            @Override
            public boolean add(Entry<K, V> kvEntry) {
                TrieMapHelper.ChangeEvent<V> details = new TrieMapHelper.ChangeEvent<>();
                root.updated(getOrCreateMutator(), kvEntry.getKey(), kvEntry.getValue(), Objects.hashCode(kvEntry.getKey()), 0, details);
                return details.isModified();
            }

            @Override
            public void clear() {
                TrieMap.this.clear();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Entry) {
                    @SuppressWarnings("unchecked")
                    Entry<K, V> entry = (Entry<K, V>) o;
                    K key = entry.getKey();
                    TrieMapHelper.SearchResult<V> result = root.findByKey(key, Objects.hashCode(key), 0);
                    return result.keyExists() && Objects.equals(result.get(), entry.getValue());
                }
                return false;
            }

            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new TransientMapEntryIterator<K, V>(TrieMap.this);
            }

            @Override
            public boolean remove(Object o) {
                if (o instanceof Entry) {
                    @SuppressWarnings("unchecked")
                    Entry<K, V> entry = (Entry<K, V>) o;
                    K key = entry.getKey();
                    TrieMapHelper.SearchResult<V> result = root.findByKey(key, Objects.hashCode(key), 0);
                    if (result.keyExists() && Objects.equals(result.get(), entry.getValue())) {
                        removeAndGiveDetails(key);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public int size() {
                return size;
            }
        };
    }

    @Override
    public V get(final @NonNull Object o) {
        @SuppressWarnings("unchecked") final K key = (K) o;
        return root.findByKey(key, Objects.hashCode(key), 0).orElse(null);
    }

    private @NonNull UniqueIdentity getOrCreateMutator() {
        if (mutator == null) {
            mutator = new UniqueIdentity();
        }
        return mutator;
    }


    @Override
    public V put(K key, V value) {
        return putAndGiveDetails(key, value).getOldValue();
    }

    @NonNull TrieMapHelper.ChangeEvent<V> putAndGiveDetails(final K key, final V val) {
        final int keyHash = Objects.hashCode(key);
        final TrieMapHelper.ChangeEvent<V> details = new TrieMapHelper.ChangeEvent<>();

        final TrieMapHelper.BitmapIndexedNode<K, V> newRootNode =
                root.updated(getOrCreateMutator(), key, val, keyHash, 0, details);

        if (details.isModified()) {
            if (details.hasReplacedValue()) {
                final V old = details.getOldValue();
                root = newRootNode;
            } else {
                root = newRootNode;
                size += 1;
            }
            modCount++;
        }

        return details;
    }

    @Override
    public V remove(Object o) {
        @SuppressWarnings("unchecked") final K key = (K) o;
        return removeAndGiveDetails(key).getOldValue();
    }

    @NonNull TrieMapHelper.ChangeEvent<V> removeAndGiveDetails(final K key) {
        final int keyHash = Objects.hashCode(key);
        final TrieMapHelper.ChangeEvent<V> details = new TrieMapHelper.ChangeEvent<>();
        final TrieMapHelper.BitmapIndexedNode<K, V> newRootNode =
                (TrieMapHelper.BitmapIndexedNode<K, V>) root.removed(getOrCreateMutator(), key, keyHash, 0, details);
        if (details.isModified()) {
            assert details.hasReplacedValue();
            root = newRootNode;
            size = size - 1;
            modCount++;
        }
        return details;
    }

    /**
     * Returns a copy of this map that is persistent.
     * <p>
     * This operation is performed in O(1) because the persistent map shares
     * the underlying trie nodes with this map.
     * <p>
     * This map loses exclusive ownership of all trie nodes. Therefore, the
     * first few updates that it performs, are copy-on-write operations, until
     * it exclusively owns some trie nodes that it can update.
     *
     * @return a persistent trie set
     */
    public PersistentTrieMap<K, V> toPersistent() {
        if (size == 0) {
            return PersistentTrieMap.of();
        }
        mutator = null;
        return new PersistentTrieMap<>(root, size);
    }

    static abstract class AbstractTransientMapEntryIterator<K, V> extends TrieMapHelper.AbstractMapIterator<K, V> {
        private final @NonNull TrieMap<K, V> map;
        private int expectedModCount;

        public AbstractTransientMapEntryIterator(@NonNull TrieMap<K, V> map) {
            super(map.root);
            this.map = map;
            this.expectedModCount = map.modCount;
        }

        @Override
        public boolean hasNext() {
            if (expectedModCount != map.modCount) {
                throw new ConcurrentModificationException();
            }
            return super.hasNext();
        }

        public Map.Entry<K, V> nextEntry() {
            if (expectedModCount != map.modCount) {
                throw new ConcurrentModificationException();
            }
            return super.nextEntry();
        }


        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            if (expectedModCount != map.modCount) {
                throw new ConcurrentModificationException();
            }
            Map.Entry<K, V> toRemove = current;

            if (hasNext()) {
                Map.Entry<K, V> next = nextEntry();
                map.remove(toRemove.getKey());
                expectedModCount = map.modCount;
                moveTo(next.getKey(), map.root);
            } else {
                map.remove(toRemove.getKey());
                expectedModCount = map.modCount;
            }

            current = null;
        }
    }

    private static class TransientMapEntryIterator<K, V> extends AbstractTransientMapEntryIterator<K, V> implements Iterator<Entry<K, V>> {

        public TransientMapEntryIterator(@NonNull TrieMap<K, V> map) {
            super(map);
        }

        @Override
        public Entry<K, V> next() {
            return nextEntry();
        }
    }

    @Override
    public TrieMap<K, V> clone() {
        try {
            @SuppressWarnings("unchecked") final TrieMap<K, V> that = (TrieMap<K, V>) super.clone();
            that.mutator = null;
            this.mutator = null;
            return that;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }
}


