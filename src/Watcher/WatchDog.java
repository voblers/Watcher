/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Watcher;

import Data.Const;
import Services.HSQL_Manager;
import Services.ServiceHandler;
import dao.Site;
import dao.WatchObj;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 *
 * @author BB3605
 */
public class WatchDog implements Runnable {

    private ArrayList<WatchObj> watched;
    private volatile ArrayList<Site> tempAdd = new ArrayList<>();
    private volatile ArrayList<Site> tempRemove = new ArrayList<>();

    private boolean canRun = false;
    private boolean stop = false;
    private boolean killedFlag = false;
    private volatile boolean needUpdate = false;

    public WatchDog(ArrayList<Site> inpSites) {
        watched = new ArrayList<>();
        for (Site site : inpSites) {
            watched.add(new WatchObj(site));
        }
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
                        //System.out.println(watched.size());
                        WatchObj status = getStatus(obj.getSite());
                        //System.out.println(obj.getSite().getAddress() + " Status: " + status.getResponseCode());
                        //if (status.getStatus() != Const.Exception) {
                            //System.out.println("a");
                            if (status.getStatus() != obj.getStatus()) {
                                obj.setStatus(status.getStatus());
                                //System.out.println("b");
                                obj.setResponseCode(status.getResponseCode());
                                //System.out.println("d");
                                if (status.getStatus() == Const.online) {
                                    
                                } else {
                                    ServiceHandler.getNotificationService().showNotification(obj.getSite().getAddress()
                                            + System.getProperty("line.separator") + "not Online!" + System.getProperty("line.separator")
                                            + "Response code: " + obj.getResponseCode(), Const.notificationError, new EventHandler() {

                                                @Override
                                                public void handle(Event t) {
                                                    FXMLDocumentController control = JavaFXApplication4.getLoader().getController();
                                                    control.showSelectedSite(obj.getSite().getAddress());
                                                }
                                            }, 0);
                                }
                            }
                            HSQL_Manager.putNewStat(obj);
                       // }
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
        for (Site a : tempAdd) {
            watched.add(new WatchObj(a));
        }

        for (Site b : tempRemove) {
            for (int i = 0; i < watched.size(); i++) {
                if (watched.get(i).getSite().getAddress().equals(b.getAddress())) {
                    watched.remove(i);
                }
            }
        }

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

}
