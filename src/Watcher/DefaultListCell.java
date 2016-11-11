/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Watcher;

import javafx.scene.Node;
import javafx.scene.control.ListCell;

/**
 *
 * @author BB3605
 * @param <T>
 */
public class DefaultListCell<T> extends ListCell<T>{
    @Override public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
 
        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (item instanceof Node) {
            setText(null);
            Node currentNode = getGraphic();
            Node newNode = (Node) item;
            if (currentNode == null || ! currentNode.equals(newNode)) {
                setGraphic(newNode);
            }
        } else {
            setText(item == null ? "null" : item.toString());
            setGraphic(null);
        }
    }
}
