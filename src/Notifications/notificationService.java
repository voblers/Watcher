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
    
    public notificationService() {
        executor = Executors.newFixedThreadPool(threadCount);
        tray = new TrayService();
        executor.execute(tray);
    }
    
    public void showNotification(String message, int messageType, double seconds) {
        TrayNotification notification = new TrayNotification(message, messageType, seconds);
        executor.execute(notification);
    }
    
    public void showNotification(String message, int messageType, EventHandler onAction, double seconds) {
        TrayNotification notification = new TrayNotification(message, messageType, onAction, seconds);
        executor.execute(notification);
    }
}
