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
public class StatusUpdaterServiceAlreadyStartedException extends Exception{

    public StatusUpdaterServiceAlreadyStartedException() {
        super("Requested service already started. Only one instance can exist");
    }
    
}
