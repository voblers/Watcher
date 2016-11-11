 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Watcher;

import ErrorHandling.InternetWatcherServiceAlreadyStartedException;
import ErrorHandling.SiteAlreadyAddedException;
import ErrorHandling.StatusUpdaterServiceAlreadyStartedException;
import ErrorHandling.WatchDogNotStartedException;
import ErrorHandling.WatchDogTimerServiceAlreadyStartedException;
import Services.GlobalServiceControlVariables;
import Services.InternetWatcher;
import Services.ServiceHandler;
import Services.WatchDogTimer;
import dao.Site;
import dao.WatchObj;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author BB3605
 */
public class watcherManager {

    private static final ArrayList<Site> sites = new ArrayList<>();
    private static WatchDog watchDog = null;
    private static boolean isStarted = false;

    public synchronized static ArrayList<Site> getSites() {
        return sites;
    }

    public static void addSite(Site inpSite) throws WatchDogNotStartedException, SiteAlreadyAddedException {
        for (Site site : sites) {
            if (site.equals(inpSite)) {
                throw new SiteAlreadyAddedException();
            }
        }
        if (watchDog != null) {
            watchDog.addSite(inpSite);
        }
        sites.add(inpSite);
    }

    public static void removeSite(Site inpSite) {
        if (watchDog != null) {
            watchDog.removeSite(inpSite);
            sites.remove(inpSite);
        }
    }

    public static synchronized void start() {
        if (watchDog == null) {
            watchDog = new WatchDog(sites);
            ScheduledExecutorService service = Executors
                    .newSingleThreadScheduledExecutor();
            service.scheduleAtFixedRate(watchDog, 0, 5, TimeUnit.SECONDS);
        } else {
            watchDog.start();
        }
        
        GlobalServiceControlVariables.InternetWatcher = false;
        isStarted = true;
    }

    public synchronized static WatchObj getStatus(Site inpSite) throws WatchDogNotStartedException {
        if (!isStarted) {
            throw new WatchDogNotStartedException();
        }
        for (Site a : sites) {
            if (a.getAddress().equals(inpSite.getAddress())) {
                return watchDog.getStat(inpSite.getAddress());
            }
        }
        return new WatchObj();
    }

    public synchronized static ArrayList<WatchObj> getStatusAll() throws WatchDogNotStartedException {
        if (!isStarted) {
            throw new WatchDogNotStartedException();
        }

        return watchDog.getStatAll();
    }

    public static synchronized void stop() {
        watchDog.stop();
        GlobalServiceControlVariables.InternetWatcher = true;
        isStarted = false;
    }

    public static synchronized void fullStop() { //Broken
        watchDog.stopAndDelete();
        while (!watchDog.isKilled()) {
        }
        ServiceHandler.stop(new InternetWatcher());
        isStarted = false;
    }

    public static void enable() {
        watchDog.canRun();
    }
}
