/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import Watcher.FXMLDocumentController;
import Watcher.JavaFXApplication4;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author BB3605
 */
public class ChartUpdateService implements Runnable {

    private final int rate;
    private final FXMLDocumentController control;
    private boolean canRun = true;

    public ChartUpdateService(int rate) {
        this.rate = rate;
        this.control = JavaFXApplication4.getLoader().getController();
    }
    
    public void stop(){
        canRun = false;
    }
    
    public void start(){
        canRun = true;
    }

    @Override
    public void run() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

        ses.scheduleAtFixedRate(() -> {
            javafx.application.Platform.runLater(() -> {
                if(canRun){
                    control.getProcessor().renew(control.getSelectedItem());
                }
            });
        }, 0, rate, TimeUnit.SECONDS);
    }

}
