/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Services;

import Watcher.watcherManager;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author BB3605
 */
public class WatchDogTimer implements Runnable{

    private final int interval;
    
    public WatchDogTimer(int seconds) {
        this.interval = seconds;
    }
    

    @Override
    public void run() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                watcherManager.enable();
            }
        }, 0, interval, TimeUnit.SECONDS);
    }
    
}
