/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.time.LocalDateTime;

/**
 *
 * @author BB3605
 */
public class offlineQueueItem {
    
    private final String site;
    private final LocalDateTime dateTime;
    private int count;

    public offlineQueueItem(String site) {
        this.site = site;
        this.dateTime = LocalDateTime.now();
        this.count = 1;
    }
    
    public void updateSite(){
        this.count++;
    }

    public String getSite() {
        return site;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public int getCount() {
        return count;
    }
}
