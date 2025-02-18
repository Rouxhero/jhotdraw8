/*
 * @(#)CssPaperSizeConverter.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssDimension2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssTokenType;
import org.jhotdraw8.css.CssTokenizer;
import org.jhotdraw8.css.StreamCssTokenizer;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.IdSupplier;
import org.jhotdraw8.text.Converter;

import java.io.IOException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts a {@code CssDimension2D} into a {@code String} and vice versa.
 *
 * @author Werner Randelshofer
 */
public class CssPaperSizeConverter implements Converter<CssDimension2D> {

    private final CssSizeConverter sizeConverter = new CssSizeConverter(false);
    private static final @NonNull Map<String, CssDimension2D> paperSizes;
    private static final @NonNull Map<CssDimension2D, String> sizePapers;

    static {
        Map<String, CssDimension2D> m = new LinkedHashMap<>();
        m.put("A0", new CssDimension2D(CssSize.from(841, "mm"), CssSize.from(1189, "mm")));
        m.put("A1", new CssDimension2D(CssSize.from(594, "mm"), CssSize.from(841, "mm")));
        m.put("A2", new CssDimension2D(CssSize.from(420, "mm"), CssSize.from(594, "mm")));
        m.put("A3", new CssDimension2D(CssSize.from(297, "mm"), CssSize.from(420, "mm")));
        m.put("A4", new CssDimension2D(CssSize.from(210, "mm"), CssSize.from(297, "mm")));
        m.put("A5", new CssDimension2D(CssSize.from(148, "mm"), CssSize.from(210, "mm")));
        m.put("A6", new CssDimension2D(CssSize.from(105, "mm"), CssSize.from(148, "mm")));
        m.put("DesignatedLong", new CssDimension2D(CssSize.from(110, "mm"), CssSize.from(220, "mm")));
        m.put("Letter", new CssDimension2D(CssSize.from(8.5, "in"), CssSize.from(11, "in")));
        m.put("Legal", new CssDimension2D(CssSize.from(8.4, "in"), CssSize.from(14, "in")));
        m.put("Tabloid", new CssDimension2D(CssSize.from(11.0, "in"), CssSize.from(17.0, "in")));
        m.put("Executive", new CssDimension2D(CssSize.from(7.25, "in"), CssSize.from(10.5, "in")));
        m.put("x8x10", new CssDimension2D(CssSize.from(8, "in"), CssSize.from(10, "in")));
        m.put("MonarchEnvelope", new CssDimension2D(CssSize.from(3.87, "in"), CssSize.from(7.5, "in")));
        m.put("Number10Envelope", new CssDimension2D(CssSize.from(4.125, "in"), CssSize.from(9.5, "in")));
        m.put("C", new CssDimension2D(CssSize.from(17.0, "in"), CssSize.from(22.0, "in")));
        m.put("B4", new CssDimension2D(CssSize.from(257, "mm"), CssSize.from(364, "mm")));
        m.put("B5", new CssDimension2D(CssSize.from(182, "mm"), CssSize.from(257, "mm")));
        m.put("B6", new CssDimension2D(CssSize.from(128, "mm"), CssSize.from(182, "mm")));
        m.put("JapanesePostcard", new CssDimension2D(CssSize.from(100, "mm"), CssSize.from(148, "mm")));
        paperSizes = m;

        Map<CssDimension2D, String> x = new LinkedHashMap<>();
        for (Map.Entry<String, CssDimension2D> e : m.entrySet()) {
            final CssDimension2D v = e.getValue();
            x.put(v, e.getKey() + " portrait");
            x.put(new CssDimension2D(v.getHeight(), v.getWidth()), e.getKey() + " landscape");
        }
        sizePapers = x;
    }

    private static final String LANDSCAPE = "landscape";
    private static final String PORTRAIT = "portrait";

    public CssPaperSizeConverter() {
    }

    private @Nullable CssDimension2D parsePageSize(@NonNull CssTokenizer tt, IdResolver idResolver) throws ParseException, IOException {
        if (tt.next() == CssTokenType.TT_IDENT) {
            CssDimension2D paperSize = paperSizes.get(tt.currentString());
            if (paperSize == null) {
                throw new ParseException("Illegal paper format:" + tt.currentString(), tt.getStartPosition());
            }
            if (tt.next() == CssTokenType.TT_IDENT) {
                switch (tt.currentString()) {
                case LANDSCAPE:
                    paperSize = new CssDimension2D(paperSize.getHeight(), paperSize.getWidth());
                    break;
                case PORTRAIT:
                    break;
                default:
                    tt.pushBack();
                }
            } else {
                tt.pushBack();
            }
            return paperSize;
        } else {
            tt.pushBack();
            CssSize x = sizeConverter.parse(tt, idResolver);
            CssSize y = sizeConverter.parse(tt, idResolver);
            return new CssDimension2D(x, y);
        }
    }

    @Override
    public void toString(@NonNull Appendable out, @Nullable IdSupplier idSupplier, @NonNull CssDimension2D value) throws IOException {
        String paper = sizePapers.get(value);
        if (paper != null) {
            out.append(paper);
        } else {
            out.append(sizeConverter.toString(value.getWidth()));
            out.append(' ');
            out.append(sizeConverter.toString(value.getHeight()));
        }
    }

    @Override
    public @Nullable CssDimension2D fromString(@NonNull CharBuffer buf, @Nullable IdResolver idResolver) throws ParseException, IOException {
        CssTokenizer tt = new StreamCssTokenizer(buf);
        return parsePageSize(tt, idResolver);

    }

    @Override
    public @Nullable CssDimension2D getDefaultValue() {
        return new CssDimension2D(CssSize.from(0), CssSize.from(0));
    }

    @Override
    public @NonNull String getHelpText() {
        StringBuilder buf = new StringBuilder();
        for (String s : paperSizes.keySet()) {
            if (buf.length() != 0) {
                buf.append('｜');
            }
            buf.append(s);
        }
        return "Format of ⟨PageSize⟩: " + "⟨width⟩mm ⟨height⟩mm｜⟨PaperFormat⟩ landscape｜⟨PaperFormat⟩ portrait"
                + "\nFormat of ⟨PaperFormat⟩: " + buf.toString();
    }
}
