/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.parser.v2.vo;

import java.util.ArrayList;
import java.util.StringTokenizer;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
/**
 *
 * @author Scott
 */
public class Weather {
    
    private int temperature;
    private String fieldCondition;
    private String sky;
    private String windDirection;
    private String windSpeed;
    private ArrayList<String> rainDelays;

    public Weather (String weatherLine) {
        // Temperature: 73, Sky: clear, Wind: left to right at 1 MPH, Rain Delays: 12 minutes.
        // "Field:" is only present for a notable field condition (e.g. wet), so it can't
        // be assumed to always be the 2nd token -- each token is matched by its own
        // label instead of relying on a fixed position/count.
        StringTokenizer tok = new StringTokenizer(weatherLine, ",");

        while (tok.hasMoreTokens()) {
            String token = tok.nextToken().trim();
            int colonIdx = token.indexOf(":");
            if (colonIdx == -1) {
                continue;
            }
            String label = token.substring(0, colonIdx).trim();
            String value = token.substring(colonIdx + 1).trim();

            if (label.equalsIgnoreCase("Temperature")) {
                this.setTemperature(Integer.parseInt(value));
            } else if (label.equalsIgnoreCase("Field")) {
                this.setFieldCondition(value);
            } else if (label.equalsIgnoreCase("Sky")) {
                this.setSky(value);
            } else if (label.equalsIgnoreCase("Wind")) {
                if (value.indexOf("MPH") == -1) {
                    this.setWindSpeed("None");
                } else {
                    this.setWindDirection(value.substring(0, value.indexOf(" at ")).trim());
                    this.setWindSpeed(value.substring(value.indexOf(" at ") + 4, value.indexOf("MPH") + 3).trim());
                }
            } else if (label.equalsIgnoreCase("Rain Delays")) {
                if (this.rainDelays == null) {
                    this.rainDelays = new ArrayList<String>();
                }
                // Value may end in "." since this is always the last field on the line.
                this.rainDelays.add(value.endsWith(".") ? value.substring(0, value.length() - 1).trim() : value);
            }
        }
    }
    
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }
    
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    public String getFieldCondition() {
        return fieldCondition;
    }
    
    public void setFieldCondition(String fieldCondition) {
        this.fieldCondition = fieldCondition;
    }

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    public String getSky() {
        return sky;
    }

    public void setSky(String sky) {
        this.sky = sky;
    }

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    public String getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(String windDirection) {
        this.windDirection = windDirection;
    }

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    public ArrayList<String> getRainDelays() {
        return rainDelays;
    }

    public void setRainDelays(ArrayList<String> rainDelays) {
        this.rainDelays = rainDelays;
    }
            
}
