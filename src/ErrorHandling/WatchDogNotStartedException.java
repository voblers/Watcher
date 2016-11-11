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
public class WatchDogNotStartedException extends Exception{

    public WatchDogNotStartedException() {
        super("Please start WatchDog process before working with watcherManager class");
    }
    
}
