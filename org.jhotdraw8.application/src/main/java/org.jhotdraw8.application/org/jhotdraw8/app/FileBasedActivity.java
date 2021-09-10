/*
 * @(#)FileBasedActivity.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.print.PrinterJob;
import javafx.scene.input.DataFormat;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ReadOnlyOptionsMap;
import org.jhotdraw8.concurrent.SimpleWorkState;
import org.jhotdraw8.concurrent.WorkState;

import java.net.URI;
import java.util.concurrent.CompletionStage;

/**
 * A {@link FileBasedActivity} is a specialization of {@link Activity} for
 * activities that work with content that is stored in a file identified
 * by an {@link URI}.
 *
 * @author Werner Randelshofer
 */
public interface FileBasedActivity extends Activity {
    String MODIFIED_PROPERTY = "modified";
    String URI_PROPERTY = "uri";
    String DATA_FORMAT_PROPERTY = "dataFormat";

    /**
     * The modified property indicates that the content has been
     * modified and needs to be saved to the file.
     * <p>
     * This property is set to true if a change in the content has
     * been detected.
     * <p>
     * The property is only set to false by calling {@link #clearModified()}.
     * This is typically done by an {@code Action} invoked by the user,
     * or by an automatic save function managed by the {@code Application}.
     *
     * @return the modified property
     */
    @NonNull ReadOnlyBooleanProperty modifiedProperty();

    default boolean isModified() {
        return modifiedProperty().get();
    }

    /**
     * Clears the modified property.
     *
     * @see #modifiedProperty()
     */
    void clearModified();

    /**
     * This property is used to identify the file that is
     * used for storing the content.
     * <p>
     * This property is managed by the {@code Action}s that load
     * and save the content from/to the file.
     *
     * @return the resource
     */
    @NonNull ObjectProperty<@Nullable URI> uriProperty();

    default @Nullable URI getURI() {
        return uriProperty().get();
    }

    default void setURI(@Nullable URI newValue) {
        uriProperty().set(newValue);
    }

    /**
     * This property specifies the format that is used for
     * storing the content in the file.
     * <p>
     * This property is managed by {@code Action}s.
     * Typically by actions that load or save the content,
     * and actions that manage file properties.
     *
     * @return the data format of the content
     */
    @NonNull ObjectProperty<@Nullable DataFormat> dataFormatProperty();

    default @Nullable DataFormat getDataFormat() {
        return dataFormatProperty().get();
    }

    default void setDataFormat(@Nullable DataFormat newValue) {
        dataFormatProperty().set(newValue);
    }

    /**
     * Asynchronously reads content data from the specified URI.
     * <p>
     * This method must not change the current document if reading fails or is canceled.
     * <p>
     * The activity must be disabled with a {@link SimpleWorkState} during a read.
     * See {@link Disableable}.
     * <p>
     * Usage:
     * <pre><code>
     * WorkState ws = new WorkState("read");
     * activity.addDisablers(ws);
     * activity.read(uri, format, options, insert, workState).handle((fmt,ex)-&gt;{
     *    ...
     *    activity.removeDisablers(ws);
     * });
     * </code></pre>
     *
     * @param uri       the URI
     * @param format    the desired data format, null means default data format
     *                  should be used
     * @param options   read options
     * @param insert    whether to insert into the current document or to replace it.
     * @param workState the work state for monitoring this invocation of the read method.
     *                  The work state is updated by the read method. The worker can be used
     *                  to cancel the read.
     * @return Returns a CompletionStage which is completed with the data format that was
     * actually used for reading the file.
     */
    @NonNull CompletionStage<@NonNull DataFormat> read(@NonNull URI uri, @Nullable DataFormat format, @NonNull ReadOnlyOptionsMap options, boolean insert, WorkState workState);

    /**
     * Asynchronously writes document data to the specified URI.
     * <p>
     * This method must not change the current document.
     * <p>
     * The activity must be disabled with a {@link SimpleWorkState} during a read.
     * See usage example in {@link #read}.
     *
     * @param uri       the URI
     * @param format    the desired data format, null means default data format
     *                  should be used
     * @param options   write options
     * @param workState the work state for monitoring this invocation of the write method.
     *                  The work state is updated by the write method. The worker can be used
     *                  to cancel the write.
     * @return Returns a CompletionStage which is completed when the write
     * operation has finished.
     */
    @NonNull CompletionStage<Void> write(@NonNull URI uri, @Nullable DataFormat format, @NonNull ReadOnlyOptionsMap options, @NonNull WorkState workState);

    /**
     * Clears the content.
     *
     * @return Returns a CompletionStage which is completed when the clear
     * operation has finished. For example
     * {@code return CompletableFuture.completedFuture(null);}
     */
    @NonNull CompletionStage<Void> clear();

    /**
     * Prints the current content.
     * <p>
     * This method must not change the current document.
     * <p>
     * The activity must be disabled with a {@link SimpleWorkState} during printing.
     * See usage example in {@link #read}.
     *
     * @param job       the printer job
     * @param workState the work state for monitoring this invocation of the print method.
     *                  The work state is updated by the print method. The worker can be used
     *                  to cancel printing.
     * @return Returns a CompletionStage which is completed when the print
     * operation has finished. For example
     * {@code return CompletableFuture.completedFuture(null);}
     */
    @NonNull CompletionStage<Void> print(@NonNull PrinterJob job, @NonNull WorkState workState);

    /**
     * Returns true if this content is empty and can be replaced by
     * another document without that the user loses data.
     *
     * @return true if empty
     */
    default boolean isEmpty() {
        return !isModified() && getURI() == null;
    }

    /**
     * For file-based activity, the title should be bound to the file name
     * from the {@link #uriProperty()}.
     * <p>
     * See {@link Activity#titleProperty()} for a general descriptinn of
     * this property.
     *
     * @return the title of the activity
     */
    @NonNull StringProperty titleProperty();
}
