/*
 * @(#)AbstractReadOnlyList.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Iterator;

public abstract class AbstractReadOnlyList<E> extends AbstractReadOnlyCollection<E> implements ReadOnlyList<E> {

    public AbstractReadOnlyList() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ReadOnlyList)) {
            return false;
        }

        Iterator<E> e1 = iterator();
        Iterator<?> e2 = ((Iterable<?>) o).iterator();
        while (e1.hasNext() && e2.hasNext()) {
            E o1 = e1.next();
            Object o2 = e2.next();
            if (!(o1 == null ? o2 == null : o1.equals(o2))) {
                return false;
            }
        }
        return !(e1.hasNext() || e2.hasNext());
    }

    @Override
    public int hashCode() {
        return ReadOnlyList.iteratorToHashCode(iterator());
    }

}
