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
public class Site {
    private String address;

    public Site(String address) {
        this.address = address;
    }

    public Site(Site inpSite) {
        this.address = inpSite.getAddress();
    }
    
    public String getAddress(){
        return address;
    }
    public void setAddress(String address){
        this.address = address;
    }
    
}