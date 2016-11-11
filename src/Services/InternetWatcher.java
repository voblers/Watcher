/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Services;

import Watcher.FXMLDocumentController;
import Watcher.JavaFXApplication4;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author BB3605
 */
public class InternetWatcher implements Runnable {

    private String address;
    private int rate;

    private boolean stop = true;

    public InternetWatcher(String site, int rate) {
        this.address = site;
        this.rate = rate;

        GlobalServiceControlVariables.InternetWatcher = true;
    }

    public InternetWatcher() {
    }

    public void stop() {
        GlobalServiceControlVariables.InternetWatcher = true;
        //System.out.println("Stop " + GlobalServiceControlVariables.InternetWatcher);
    }

    public void start() {
        GlobalServiceControlVariables.InternetWatcher = false;
        //System.out.println("Start " + GlobalServiceControlVariables.InternetWatcher);
    }

    @Override
    public void run() {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        final String url = this.address;

        ses.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                //if (!GlobalServiceControlVariables.InternetWatcher) {
                final FXMLDocumentController control = JavaFXApplication4.getLoader().getController();
                HttpURLConnection connection = null;
                try {
                    URL u = new URL(url);
                    connection = (HttpURLConnection) u.openConnection();
                    connection.setRequestMethod("HEAD");
                    final int code = connection.getResponseCode();
                    javafx.application.Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            if (code == 200) {
                                control.isInternet(true);
                            } else {
                                control.isInternet(false);
                            }
                        }
                    });
                    /*if (code == 200) {
                     control.isInternet(true);
                     } else {
                     control.isInternet(false);
                     }*/
                    // You can determine on HTTP return code received. 200 is success.
                } catch (IOException ex) {
                    control.isInternet(false);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
                // }
            }
        }, 0, rate, TimeUnit.SECONDS);
    }

}
