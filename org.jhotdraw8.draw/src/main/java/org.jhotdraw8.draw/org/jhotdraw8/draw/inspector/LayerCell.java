/*
 * @(#)LayerCell.java
 * Copyright © 2022 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.util.Resources;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

/**
 * FXML Controller class.
 * <p>
 * XXX all keys must be customizable FIXME property binding in this class is a
 * mess
 *
 * @author Werner Randelshofer
 */
public class LayerCell extends ListCell<Figure> {

    private HBox node;

    @FXML
    private CheckBox visibleCheckBox;

    @FXML
    private CheckBox lockedCheckBox;

    @FXML
    private Label selectionLabel;

    private WeakReference<DrawingModel> drawingModel;

    private boolean isUpdating;

    private @Nullable Figure item;

    private TextField editField;

    private final LayersInspector inspector;

    public LayerCell(DrawingModel drawingModel, LayersInspector inspector) {
        this(LayersInspector.class.getResource("LayerCell.fxml"), drawingModel, inspector);
    }

    public LayerCell(@NonNull URL fxmlUrl, DrawingModel drawingModel, LayersInspector inspector) {
        this.drawingModel = new WeakReference<>(drawingModel);
        this.inspector = inspector;
        init(fxmlUrl);
    }

    private void init(@NonNull URL fxmlUrl) {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        Resources rsrc = InspectorLabels.getResources();
        loader.setResources(rsrc.asResourceBundle());

        try (InputStream in = fxmlUrl.openStream()) {
            node = loader.load(in);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        visibleCheckBox.selectedProperty().addListener(this::commitLayerVisible);
        lockedCheckBox.selectedProperty().addListener(this::commitLayerLocked);

        visibleCheckBox.getStyleClass().add(InspectorStyleClasses.VISIBLE_CHECK_BOX);
        lockedCheckBox.getStyleClass().add(InspectorStyleClasses.LOCKED_CHECK_BOX);

    }

    @Override
    protected void updateItem(@Nullable Figure item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            this.item = null;
        } else {
            isUpdating = true;
            this.item = item;
            if (isEditing()) {
                if (editField == null) {
                    editField = createTextField();
                }
                if (editField != null) {
                    editField.setText(getItemText());
                }
                setText(null);// hide the text part of the label!!

                if (editField.getParent() == null) {
                    node.getChildren().add(editField);
                }
            } else {
                setText(getItemText());
                if (editField != null && editField.getParent() != null) {
                    node.getChildren().remove(editField);
                }
            }
            setGraphic(node);
            Integer count = inspector.getSelectionCount((Layer) item);
            selectionLabel.setText(count == null ? "" : "(" + count.toString() + ")");

            visibleCheckBox.setSelected(item.get(HideableFigure.VISIBLE));
            lockedCheckBox.setSelected(item.get(LockableFigure.LOCKED));
            isUpdating = false;
        }
    }

    /**
     * Creates a {@code LayerCell} cell factory for use in {@code ListView}
     * controls.
     *
     * @param drawingModel the drawing model
     * @param inspector    the layers inspector
     * @return callback
     */
    public static @NonNull Callback<ListView<Figure>, ListCell<Figure>> forListView(DrawingModel drawingModel, LayersInspector inspector) {
        return list -> new LayerCell(drawingModel, inspector);
    }

    private void commitLayerVisible(Observable o) {
        if (!isUpdating) {
            DrawingModel m = this.drawingModel.get();
            if (m != null) {
                m.set(item, HideableFigure.VISIBLE, visibleCheckBox.isSelected());
            }
        }
    }

    private void commitLayerLocked(Observable o) {
        if (!isUpdating) {
            DrawingModel m = this.drawingModel.get();
            if (m != null) {
                m.set(item, LockableFigure.LOCKED, lockedCheckBox.isSelected());
            }
        }
    }

    public Label getSelectionLabel() {
        return selectionLabel;
    }

    /**
     * Returns the {@link StringConverter} used in this cell.
     *
     * @return the converter
     */
    public final @Nullable StringConverter<Figure> getConverter() {
        return null;//converterProperty().get();
    }

    @Override
    public void startEdit() {
        if (!isEditable() || !getListView().isEditable()) {
            return;
        }
        super.startEdit();
        updateItem(getItem(), false);
        editField.selectAll();
        editField.requestFocus();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        updateItem(getItem(), false);
    }

    private @NonNull String getItemText() {
        return getItem() == null ? "" : getItem().get(StyleableFigure.ID);
    }

    private @NonNull TextField createTextField() {
        final TextField textField = new TextField();

        // Use onAction here rather than onKeyReleased (with check for Enter),
        // as otherwise we encounter RT-34685
        textField.setOnAction(event -> {
            commitEdit(item);
            event.consume();
        });
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
                t.consume();
            }
        });
        return textField;
    }

    @Override
    public void commitEdit(Figure newValue) {
        if (editField != null && isEditing()) {
            DrawingModel m = this.drawingModel.get();
            if (m != null) {
                m.set(item, StyleableFigure.ID, editField.getText());
            }
        }
        super.commitEdit(newValue);
    }
}
