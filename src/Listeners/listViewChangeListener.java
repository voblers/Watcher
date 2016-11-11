/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Listeners;

import Data.Const;
import ErrorHandling.WatchDogNotStartedException;
import Watcher.FXMLDocumentController;
import Watcher.JavaFXApplication4;
import Watcher.watcherManager;
import dao.Site;
import dao.WatchObj;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author BB3605
 */
public class listViewChangeListener implements javafx.beans.value.ChangeListener<Object> {

    @Override
    public void changed(ObservableValue<? extends Object> ov, Object t, Object t1) {
        FXMLDocumentController control = JavaFXApplication4.getLoader().getController();
        try {
            WatchObj statusCode = watcherManager.getStatus(new Site(control.getSelectedItem()));

            switch (statusCode.getStatus()) {
                case Const.online:
                    control.changeStatusLabel("ONLINE");
                    control.changeStatusPaneStyle(Const.onlineStyle);
                    break;
                case Const.offline:
                    control.changeStatusLabel("OFFLINE");
                    control.changeStatusPaneStyle(Const.offlineStyle);
                    break;
                case Const.redirecting:
                    control.changeStatusLabel("REDIRECTING");
                    control.changeStatusPaneStyle(Const.redirectStyle);
                    break;
                case Const.unknown:
                    control.changeStatusLabel("UNKNOWN");
                    control.changeStatusPaneStyle(Const.unknownStyle);
                    break;
            }
        } catch (WatchDogNotStartedException ex) {
            control.changeStatusLabel("UNKNOWN");
            control.changeStatusPaneStyle(Const.unknownStyle);
        }
        try{
            control.getProcessor().renew(control.getSelectedItem());
        } catch(NullPointerException ex){}
    }

}
