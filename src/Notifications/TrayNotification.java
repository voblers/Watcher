/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications;

import Data.Const;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 *
 * @author BB3605
 */
public class TrayNotification implements Runnable {

    private final String title = "Watcher";
    private final String message;
    EventHandler onAction = null;
    double dismiss = 0;
    private final int messageType;

    public TrayNotification(String message, int messageType, double dismiss) {
        this.message = message;
        this.dismiss = dismiss;
        this.messageType = messageType;
    }

    public TrayNotification(String message, int messageType, EventHandler onAction, double dismiss) {
        this.message = message;
        this.onAction = onAction;
        this.messageType = messageType;
    }

    @Override
    public void run() {
        javafx.application.Platform.runLater(() -> {
            Notifications notification = Notifications.create();
            
            if (onAction != null) {
                notification.onAction(onAction);
            }
            
            if (dismiss > 0) {
                notification.hideAfter(Duration.seconds(dismiss));
            } else {
                notification.hideAfter(Duration.INDEFINITE);
            }
            
            switch (messageType) {
                case Const.notificationError:
                    notification.title(title);
                    notification.text(message);
                    notification.showError();
                    break;
                case Const.notificationInfo:
                    notification.title(title);
                    notification.text(message);
                    notification.showInformation();
                    break;
                case Const.notificationSuccess:
                    notification.title(title);
                    notification.text(message);
                    notification.showConfirm();
                    break;
                case Const.notificationWarning:
                    notification.title(title);
                    notification.text(message);
                    notification.showWarning();
                    break;
            }
        });
    }

}
