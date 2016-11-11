/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import Data.Const;

/**
 *
 * @author BB3605
 */
public class WatchObj {
    private int status;
    private int responseCode;
    private Site site;

    public WatchObj(Site inpSite) {
        this.status = Const.unknown;
        this.site = inpSite;
    }

    public WatchObj() {
        this.site = null;
    }
    /**
     *
     * @param status
     * @param inpSite
     */
    public WatchObj(int status, Site inpSite) {
        this.status = status;
        this.site = inpSite;
    }

    public WatchObj(int status, int responseCode) {
        this.status = status;
        this.responseCode = responseCode;
    }    

    public Site getSite() {
        return this.site;
    }

    public int getStatus() {
        return this.status;
    }
    
    public void setStatus(int status){
        this.status = status;
    }
    
    public void setSite(Site inpSite){
        this.site = inpSite;
    }
    
    public int getResponseCode(){
        return this.responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
    
    @Override
    public String toString(){
        return "Reponse code: " + responseCode + "; Status: " + status + "; Site: " + getSite().getAddress();
    }
}
