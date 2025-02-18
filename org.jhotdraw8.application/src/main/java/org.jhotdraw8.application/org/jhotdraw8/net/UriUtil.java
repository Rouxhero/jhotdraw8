/*
 * @(#)UriUtil.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.net;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * UriUtil.
 *
 * @author Werner Randelshofer
 */
public class UriUtil {

    /**
     * Don't let anyone instantiate this class.
     */
    private UriUtil() {
    }

    /**
     * Prevent instance creation.
     */
    private void URIUtil() {
    }

    /**
     * Returns the name of an URI for display in the title bar of a window.
     * <p>
     * If the URI is a file URI, then displays the file name followed
     * by the path to the file. If the file is inside the user home
     * directory, only the relative path is shown.
     * <p>
     * Examples:
     * <pre>
     *     uri = /User/me/Desktop/Hello.txt
     *     ⊢ title = Hello.txt [~/Desktop]
     *
     *     uri = /User/notme/Desktop/Hello.txt
     *     ⊢ title = Hello.txt [/User/notme/Desktop]
     *
     *     uri = /Volumes/NetworkDrive/Hello.txt
     *     ⊢ title = Hello.txt [/Volumes/NetworkDrive]
     * </pre>
     *
     * @param uri the uri
     * @return the name
     */
    public static String getName(@NonNull URI uri) {
        if (uri.getScheme() != null && "file".equals(uri.getScheme())) {
            Path file = Paths.get(clearQuery(uri));
            String userHome = System.getProperty("user.home");
            boolean isInsideHome = false;
            if (userHome != null) {
                Path userHomePath = Paths.get(userHome);
                if (file.startsWith(userHomePath)) {
                    file = userHomePath.relativize(file);
                    isInsideHome = true;
                }
            }
            return file.getFileName() + " ["
                    + (isInsideHome ? "~" + File.separatorChar : "")
                    + file.getParent() + "]";
        }
        return uri.toString();
    }

    /**
     * Adds a query. If a query is already present, adds it after a {@literal '&'}
     * character. Both, the key, and the value may not include the characters
     * {@literal '&'} and '='.
     *
     * @param uri   an uri
     * @param key   the key
     * @param value the value
     * @return the updated query
     */
    public static URI addQuery(@NonNull URI uri, @Nullable String key, @Nullable String value) {
        if (key == null || value == null) {
            return uri;
        }
        if (key.indexOf('=') != -1) {
            throw new IllegalArgumentException("key:" + key);
        }
        if (value.indexOf('=') != -1) {
            throw new IllegalArgumentException("value:" + value);
        }

        return addQuery(uri, key + '=' + value);
    }

    /**
     * Adds a query. If a query is already present, adds it after a {@literal '&'}
     * character. The query may not include the character {@literal '&'}.
     *
     * @param uri   an uri
     * @param query the query
     * @return the updated query
     */
    public static URI addQuery(@NonNull URI uri, @Nullable String query) {
        if (query == null) {
            return uri;
        }
        if (query.indexOf('&') != -1) {
            throw new IllegalArgumentException("query:" + query);
        }

        String oldQuery = uri.getQuery();
        String newQuery = oldQuery == null ? query : oldQuery + "&" + query;

        return setQuery(uri, newQuery);
    }

    /**
     * Sets the query on the specified URI. If a query is already present, it is
     * removed.
     *
     * @param uri   an uri
     * @param query the query
     * @return the update uri
     */
    public static URI setQuery(URI uri, String query) {
        URI u = uri;
        try {
            u = new URI(u.getScheme(),
                    u.getUserInfo(), u.getHost(), u.getPort(),
                    u.getPath(), query,
                    u.getFragment());
        } catch (URISyntaxException ex) {
            ex.printStackTrace();
        }
        return u;
    }

    public static URI clearQuery(URI uri) {

        return setQuery(uri, null);
    }

    /**
     * Parses the query of the URI. Assumes that the query consists of
     * {@literal '&'}-separated, key '=' value pairs.
     *
     * @param uri an URI
     * @return a map
     */
    public static @NonNull Map<String, String> parseQuery(@NonNull URI uri) {
        String query = uri.getQuery();
        Map<String, String> map = new LinkedHashMap<>();
        if (query != null) {
            for (String pair : query.split("&")) {
                int p = pair.indexOf('=');
                String key = pair.substring(0, p);
                String value = pair.substring(p + 1);
                map.put(key, value);
            }
        }
        return map;
    }
}
