/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dao;

import java.time.LocalDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author BB3605
 */
public class detailsObject {
    private final StringProperty URL, codeDet, longDet;
    private final Property<LocalDateTime> timestamp;
    private final IntegerProperty code;

    public  detailsObject(String URL, String codeDet, String longDet, LocalDateTime timestamp, int code) {
        this.URL = new SimpleStringProperty(URL);
        this.codeDet = new SimpleStringProperty(codeDet);
        this.longDet = new SimpleStringProperty(longDet);
        this.timestamp = new SimpleObjectProperty<>(timestamp);
        this.code = new SimpleIntegerProperty(code);
    }

    public detailsObject() {
        this.URL = null;
        this.codeDet = null;
        this.longDet = null;
        this.timestamp = null;
        this.code = null;
    }

    public detailsObject(detailsObject object) {
        this.URL = new SimpleStringProperty(object.getURL());
        this.codeDet = new SimpleStringProperty(object.getCodeDet());
        this.longDet = new SimpleStringProperty(object.getLongDet());
        this.timestamp = new SimpleObjectProperty<>(object.getTimestamp());
        this.code = new SimpleIntegerProperty(object.getCode());
    }
    
    

    public final int getCode() {
        return code.get();
    }

    public final String getCodeDet() {
        return codeDet.get();
    }

    public String getLongDet() {
        return longDet.get();
    }

    public final LocalDateTime getTimestamp() {
        return timestamp.getValue();
    }

    public final String getURL() {
        return URL.get();
    }
    
    public final void setUrl(String URL){
        this.URL.set(URL);
    }
    
    public final void setTimestamp(LocalDateTime timestamp){
        this.timestamp.setValue(timestamp);
    }
    
    public final void setCodeDet(String codeDet){
        this.codeDet.set(codeDet);
    }
    
    public final void setLongDet(String longDet){
        this.longDet.set(longDet);
    }
    
    public final void setCode(int code){
        this.code.set(code);
    }
}
