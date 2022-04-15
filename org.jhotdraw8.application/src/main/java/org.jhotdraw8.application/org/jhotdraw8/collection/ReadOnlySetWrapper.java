/*
 * @(#)ReadOnlySetWrapper.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.Iterator;
import java.util.Set;

/**
 * Wraps a {@link Set} in the {@link ReadOnlySet} API.
 * <p>
 * The underlying Set is referenced - not copied. This allows to pass a
 * set to a client while preventing that the client can modify the set directly.
 *
 * @author Werner Randelshofer
 */
public final class ReadOnlySetWrapper<E> extends AbstractReadOnlySet<E> {
 protected final Set<? extends E> backingSet;

    public ReadOnlySetWrapper(Set<? extends E> backingSet) {
        this.backingSet = backingSet;
    }
      @Override
    public boolean contains(Object o) {
        return backingSet.contains(o);
    }
    @Override
    public @NonNull Iterator<E> iterator() {
        return new Iterator<E>() {
            private final Iterator<? extends E> i = backingSet.iterator();
            @Override
            public boolean hasNext() {
                return i.hasNext();
            }
            @Override
            public E next() {
                return i.next();
            }
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    @Override
    public int size() {
        return backingSet.size();
    }


}
