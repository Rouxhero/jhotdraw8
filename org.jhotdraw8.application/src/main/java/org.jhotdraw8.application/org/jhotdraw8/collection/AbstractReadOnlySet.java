/*
 * @(#)AbstractReadOnlySet.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;
import org.jhotdraw8.annotation.NonNull;

import java.util.Iterator;
import java.util.Set;

public abstract class AbstractReadOnlySet<E> extends AbstractReadOnlyCollection<E> implements ReadOnlySet<E> {
    
    
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ReadOnlySet)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        ReadOnlyCollection<E> c = (ReadOnlyCollection<E>) o;
        if (c.size() != size()) {
            return false;
        }
        try {
            return containsAll(c);
        } catch (ClassCastException | NullPointerException unused) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        for (E e : this) {
            hashCode = 31 * hashCode + (e == null ? 0 : e.hashCode());
        }
        return hashCode;
    }

  
    public void copyInto(Object[] out, int offset) {
        int i = offset;
        for (E e : this) {
            out[i++] = e;
        }
    }

}
