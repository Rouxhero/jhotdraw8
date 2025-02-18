/*
 * @(#)NullablePoint2DStyleableKey.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.text.Point2DConverter;
import org.jhotdraw8.styleable.WritableStyleableMapAccessor;
import org.jhotdraw8.text.Converter;

/**
 * Point2DStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class NullablePoint2DStyleableKey extends AbstractStyleableKey<Point2D> implements WritableStyleableMapAccessor<Point2D> {

    private static final long serialVersionUID = 1L;
    private final Converter<Point2D> converter = new Point2DConverter(true);

    /**
     * Creates a new instance with the specified name and with null as the
     * default value.
     *
     * @param name The name of the key.
     */
    public NullablePoint2DStyleableKey(@NonNull String name) {
        this(name, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key          The name of the name. type parameters are given. Otherwise
     *                     specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public NullablePoint2DStyleableKey(@NonNull String key, @Nullable Point2D defaultValue) {
        super(key, Point2D.class, defaultValue);
    }

    @Override
    public @NonNull Converter<Point2D> getCssConverter() {
        return converter;
    }
}
