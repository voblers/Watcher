/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import ErrorHandling.WatchDogNotStartedException;
import Watcher.FXMLDocumentController;
import Watcher.watcherManager;
import dao.Site;
import dao.WatchObj;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author BB3605
 */
public class StatusUpdater implements Runnable {

    private final int rate;
    private final FXMLDocumentController control;

    public StatusUpdater(int rate, FXMLDocumentController inpController) {
        this.rate = rate;
        this.control = inpController;
    }

    @Override
    public void run() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

        ses.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                try {
                    //ArrayList<WatchObj> list = watcherManager.getStatusAll();
                    final WatchObj watchObj = watcherManager.getStatus(new Site(control.getSelectedItem()));
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            control.updateStatus(watchObj);
                        }
                    });
                } catch (WatchDogNotStartedException ex) {
                }

            }
        }, 0, rate, TimeUnit.SECONDS);
    }

}
