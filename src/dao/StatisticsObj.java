/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

/**
 *
 * @author BB3605
 */
public class StatisticsObj{
    public WatchObj site;
    public double count;

    public StatisticsObj(WatchObj site, double count) {
        this.site = site;
        this.count = count;
    }
}
