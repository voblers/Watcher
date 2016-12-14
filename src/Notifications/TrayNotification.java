/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications;

import Data.Const;
import Services.ServiceHandler;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

/**
 *
 * @author BB3605
 */
public class TrayNotification implements Runnable {

    private final int ID;
    private final String title = "Watcher";
    private final String message;
    EventHandler onAction = null;
    double dismiss = 0;
    private final int messageType;

    public TrayNotification(int ID, String message, int messageType, double dismiss) {
        this.message = message;
        this.dismiss = dismiss;
        this.messageType = messageType;
        this.ID = ID;
    }

    public TrayNotification(int ID, String message, int messageType, double dismiss, EventHandler onAction) {
        this.message = message;
        this.onAction = onAction;
        this.messageType = messageType;
        this.dismiss = dismiss;
        this.ID = ID;
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

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    ServiceHandler.getNotificationService().removenotification(ID);
                }
            }, (long) (1000 * dismiss));

        });
    }

}
