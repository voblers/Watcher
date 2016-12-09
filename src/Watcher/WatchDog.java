/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Watcher;

import Data.Const;
import ErrorHandling.WatchDogNotStartedException;
import Services.HSQL_Manager;
import Services.ServiceHandler;
import dao.Site;
import dao.WatchObj;
import dao.offlineQueueItem;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;

/**
 *
 * @author BB3605
 */
public class WatchDog implements Runnable {

    private final ArrayList<WatchObj> watched;
    private volatile ArrayList<Site> tempAdd = new ArrayList<>();
    private volatile ArrayList<Site> tempRemove = new ArrayList<>();
    private final ArrayList<offlineQueueItem> offlineQueue = new ArrayList<>();

    private boolean canRun = false;
    private boolean stop = false;
    private boolean killedFlag = false;
    private volatile boolean needUpdate = false;

    private final int notificationLevel = 5;

    public WatchDog(ArrayList<Site> inpSites) {
        watched = new ArrayList<>();
        inpSites.stream().forEach((site) -> {
            watched.add(new WatchObj(site));
        });
    }

    /*public synchronized void add(Site inpSite) {
     watched.add(new WatchObj(inpSite));
     }
    
     public synchronized void remove(Site inpSite){
     watched.remove(inpSite);
     }*/
    public synchronized void canRun() {
        System.out.println("Can run");
        canRun = true;
    }

    public synchronized WatchObj getStat(String inpString) {
        for (WatchObj obj : watched) {
            if (obj.getSite().getAddress().equals(inpString)) {
                return obj;
            }
        }
        return new WatchObj(Const.unknown, 0);
    }

    public synchronized ArrayList<WatchObj> getStatAll() {
        return watched;
    }

    @Override
    public void run() {
        canRun = true;
        killedFlag = false;
        //while(true){
        if (canRun) {
            //System.out.println(needUpdate);
            if (needUpdate) {
                synhro();
            }

            if (!watched.isEmpty() && !stop) {
                Thread a = new Thread(() -> {
                    for (WatchObj obj : watched) {
                        if (watcherManager.ifRunning()) {
                            WatchObj status = getStatus(obj.getSite());
                            if (status.getStatus() != obj.getStatus()) {
                                obj.setStatus(status.getStatus());
                                obj.setResponseCode(status.getResponseCode());
                            }
                            if (status.getStatus() != Const.online) {
                                notOnline(obj);
                            } else {
                                HSQL_Manager.putNewStat(obj);
                            }
                        }
                        // }
                    }
                    
                    Iterator<offlineQueueItem> offlineQueueItemIterator = offlineQueue.iterator();
                    while (offlineQueueItemIterator.hasNext()) {
                        try {
                            if (watcherManager.getStatus(new Site(offlineQueueItemIterator.next().getSite())).getStatus() == Const.online) {
                                offlineQueueItemIterator.remove();
                            }
                        } catch (WatchDogNotStartedException ex) {
                        }
                    }
                    });
                    a.setName("getStatus Thread");
                    a.start();
                }

            canRun = false;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(WatchDog.class.getName()).log(Level.SEVERE, null, ex);
            }
            //}
        }

    

    private WatchObj getStatus(Site inpSite) {
        HttpURLConnection connection = null;

        try {
            URL u = new URL(inpSite.getAddress());
            connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(5000);
            int code = connection.getResponseCode();
            //System.out.println(inpSite.getAddress() + " = Rersponse code: " + code);
            if (code == 200) {
                return new WatchObj(Const.online, code);
            } else if (code >= 300 && code < 400) {
                return new WatchObj(Const.redirecting, code);
            } else {
                return new WatchObj(Const.offline, code);
            }

            // You can determine on HTTP return code received. 200 is success.
        } catch (SocketTimeoutException ex) {
            return new WatchObj(Const.Exception, 0);
        } catch (ProtocolException | MalformedURLException ex) {
            return new WatchObj(Const.Exception, 0);
        } catch (IOException ex) {
            return new WatchObj(Const.Exception, 0);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public synchronized void addSite(Site inpSite) {
        tempAdd.add(inpSite);
        needUpdate = true;
    }

    public synchronized void removeSite(Site inpSite) {
        tempRemove.add(inpSite);
        needUpdate = true;
    }

    private synchronized void synhro() {
        tempAdd.stream().forEach((a) -> {
            watched.add(new WatchObj(a));
        });
        tempRemove.stream().forEach((b) -> {
            for (int i = 0; i < watched.size(); i++) {
                if (watched.get(i).getSite().getAddress().equals(b.getAddress())) {
                    watched.remove(i);
                }
            }
        });

        tempAdd.clear();
        tempRemove.clear();
        needUpdate = false;
    }

    public synchronized void stop() {
        this.stop = true;
    }

    public synchronized void start() {
        this.stop = false;
    }

    public synchronized void stopAndDelete() {
        this.stop = true;
        watched.clear();
    }

    public synchronized boolean isKilled() {
        return killedFlag;
    }

    private void notOnline(WatchObj obj) {

        for (offlineQueueItem a : offlineQueue) {
            try {
                if (watcherManager.getStatus(new Site(a.getSite())).getStatus() == Const.online) {
                    offlineQueue.remove(a);
                }
            } catch (WatchDogNotStartedException ex) {
            }

            if (a.getSite().equals(obj.getSite().getAddress())) {
                a.updateSite();

                if (a.getCount() >= notificationLevel) {
                    ServiceHandler.getNotificationService().showNotification(obj.getSite().getAddress()
                            + System.getProperty("line.separator") + "not Online!" + System.getProperty("line.separator")
                            + "Response code: " + obj.getResponseCode(), Const.notificationError, (Event t) -> {
                                FXMLDocumentController control = JavaFXApplication4.getLoader().getController();
                                control.showSelectedSite(obj.getSite().getAddress());
                            }, 0);
                    HSQL_Manager.putNewStat(obj);
                }
                return;
            }
        }
        offlineQueue.add(new offlineQueueItem(obj.getSite().getAddress()));
    }

}
