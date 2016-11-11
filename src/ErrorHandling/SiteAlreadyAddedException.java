/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ErrorHandling;

/**
 *
 * @author BB3605
 */
public class SiteAlreadyAddedException extends Exception{

    public SiteAlreadyAddedException() {
        super("The site is already in WatchDog queue");
    }
    
}
