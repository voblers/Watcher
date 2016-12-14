/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.event.EventHandler;

/**
 *
 * @author BB3605
 */
public class notificationService {

    private final int threadCount = 10;
    private ExecutorService executor = null;
    private TrayService tray = null;
    private final NotificationQueue queue = new NotificationQueue();

    public notificationService() {
        executor = Executors.newFixedThreadPool(threadCount);
        tray = new TrayService();
        executor.execute(tray);

        queue.addListener((int ID) -> {
            TrayNotification notification = new TrayNotification(ID, queue.get(ID).message, queue.get(ID).messageType, queue.get(ID).timeout, queue.get(ID).onAction);
            executor.execute(notification);
        });
    }

    public void showNotification(String message, int messageType, double seconds) {
        queue.addNotification(message, messageType, seconds);
    }

    public void showNotification(String message, int messageType, EventHandler onAction, double seconds) {
        queue.addNotification(message, messageType, seconds);
    }

    public void removenotification(int ID) {
        queue.removeNotification(ID);
    }

}
