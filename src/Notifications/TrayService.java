/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications;

import Watcher.JavaFXApplication4;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javafx.stage.WindowEvent;
import javax.swing.ImageIcon;

/**
 *
 * @author BB3605
 */
public class TrayService implements Runnable {

    public TrayService() {
    }

    @Override
    public void run() {
        //Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon
                = new TrayIcon(createImage("bulb.gif", "tray icon"));
        
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            JavaFXApplication4.show();
        }
    }
        });
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener((ActionEvent e) -> {
            JavaFXApplication4.getStage().fireEvent(new WindowEvent(JavaFXApplication4.getStage(), WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        
        MenuItem showUI = new MenuItem("Show UI");
        showUI.addActionListener((ActionEvent e) -> {
            JavaFXApplication4.show();
        });

        //Add components to pop-up menu
        popup.add(exitItem);
        popup.add(showUI);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
        }
    }
    
    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = TrayService.class.getResource(path);
         
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

}
