package com.infynyxx.akka.experiments;

/**
 * @author Prajwal Tuladhar <praj@infynyxx.com>
 */
public class LogDeserializer {

    private String key;
    private String value;

    public boolean isValid() {
        return (key != null && value != null);
    }

    public String getKey() {
        return key;
    }
    
    public String getValue() {
        return value;
    }
}
