/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import Notifications.notificationService;
import ErrorHandling.InternetWatcherServiceAlreadyStartedException;
import ErrorHandling.StatusUpdaterServiceAlreadyStartedException;
import ErrorHandling.WatchDogTimerServiceAlreadyStartedException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author BB3605
 */
public class ServiceHandler {

    private static InternetWatcher inetWatcher = null;
    private static WatchDogTimer dogTimer = null;
    private static StatusUpdater statusUpdate = null;
    private static ChartUpdateService chartUpdate = null;
    private static notificationService notify;

    public static void startService(Object service) throws InternetWatcherServiceAlreadyStartedException, WatchDogTimerServiceAlreadyStartedException, StatusUpdaterServiceAlreadyStartedException {
        if (service instanceof InternetWatcher) {
            if (inetWatcher == null) {
                inetWatcher = (InternetWatcher) service;
                Thread inetWatcherThread = new Thread(inetWatcher);
                inetWatcherThread.start();
            } else {
                throw new InternetWatcherServiceAlreadyStartedException();
            }
        } else if (service instanceof WatchDogTimer) {
            if (dogTimer == null) {
                dogTimer = (WatchDogTimer) service;
                Thread dogTimerThread = new Thread(dogTimer);
                dogTimerThread.start();
            } else {
                throw new WatchDogTimerServiceAlreadyStartedException();
            }
        } else if (service instanceof StatusUpdater) {
            if (statusUpdate == null) {
                statusUpdate = (StatusUpdater) service;
                Thread statusUpdateThread = new Thread(statusUpdate);
                statusUpdateThread.start();
            } else {
                throw new StatusUpdaterServiceAlreadyStartedException();
            }
        }
    }

    public static void stop(Object serviceObject) {
        if (serviceObject instanceof InternetWatcher) {
            if (inetWatcher == null) {
            } else {
                inetWatcher.stop();
            }
        }
        else if(serviceObject instanceof ChartUpdateService){
            
        }
    }

    public static void start(Object serviceObject) {
        if (serviceObject instanceof InternetWatcher) {
            if (inetWatcher == null) {
                try {
                    startService(serviceObject);
                } catch (InternetWatcherServiceAlreadyStartedException | WatchDogTimerServiceAlreadyStartedException | StatusUpdaterServiceAlreadyStartedException ex) {
                    Logger.getLogger(ServiceHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                inetWatcher.start();
            }
        }
        else if(serviceObject instanceof ChartUpdateService){
            
        }
        else if(serviceObject instanceof notificationService){
            if(notify == null)
                notify = new notificationService();
        }
    }
    
    public static notificationService getNotificationService(){
        return notify;
    }
}
