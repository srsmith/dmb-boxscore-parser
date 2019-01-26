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
        StringTokenizer tok = new StringTokenizer(weatherLine, ",");
        
        String token = tok.nextToken();
        this.setTemperature(Integer.parseInt(token.substring(token.indexOf(":")+1).trim()));
        
        token = tok.nextToken();
        this.setSky(token.substring(token.indexOf(":")+1).trim());
        
        if (tok.hasMoreTokens()) {
            token = tok.nextToken();
            if (token.indexOf("MPH") == -1) {
                this.setWindSpeed("None");
            } else {
                this.setWindDirection(token.substring(token.indexOf(":")+1, token.indexOf(" at ")).trim());
                this.setWindSpeed(token.substring(token.indexOf(" at ")+4, token.indexOf("MPH")+3).trim());
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
