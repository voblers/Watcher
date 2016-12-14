/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Notifications;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.event.Event;
import javafx.event.EventHandler;

/**
 *
 * @author BB3605
 */
public class NotificationQueue {

    public class Notification {

        int ID;
        String message;
        int messageType;
        double timeout;
        EventHandler onAction;

        private LocalDateTime time;

        public Notification(int ID, String message, int messageType, double timeout, EventHandler onAction) {
            this.ID = ID;
            this.message = message;
            this.messageType = messageType;
            this.timeout = timeout;
            this.onAction = onAction;
            this.time = LocalDateTime.now();
        }

        public Notification() {
        }

        public LocalDateTime getTime() {
            return this.time;
        }

        public int getID() {
            return this.ID;
        }
    }

    private final ArrayList<Notification> queue = new ArrayList<>();
    private final List<NotificationListChangeEvent> listeners = new ArrayList<>();

    public NotificationQueue() {

    }

    public void addListener(NotificationListChangeEvent toAdd) {
        listeners.add(toAdd);
    }

    public void addNotification(String message, int messageType, double timout) {
        for (Notification a : queue) {
            if (a.message.equals(message) && a.messageType == messageType) {
                return;
            }
        }

        int ID = queue.isEmpty() ? 1 : queue.size() + 1;
        queue.add(new Notification(ID, message, messageType, timout, new EventHandler() {
            @Override
            public void handle(Event event) {
                removeNotification(ID);
            }
        }));
        listeners.stream().forEach((evt) -> {
            evt.itemAdded(ID);
        });
    }

    public Notification getNext() {
        if (queue.isEmpty()) {
            return new Notification();
        } else {
            return queue.get(0);
        }
    }

    public Notification get(int ID) {
        if (queue.isEmpty()) {
            return new Notification();
        }
        Iterator<Notification> iter = queue.iterator();

        while (iter.hasNext()) {
            Notification a = iter.next();
            if (a.ID == ID) {
                return a;
            }
        }

        return new Notification();
    }

    public void removeNotification(int ID) {
        Iterator<Notification> iter = queue.iterator();

        while (iter.hasNext()) {
            if (iter.next().ID == ID) {
                iter.remove();
                return;
            }
        }
    }
}
