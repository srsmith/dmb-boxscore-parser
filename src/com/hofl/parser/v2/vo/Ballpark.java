/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.parser.v2.vo;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Scott
 */
public class Ballpark {
    
    private String ballparkName;

    public Ballpark() {
        
    }
    
    public Ballpark(String ballparkName) {
        this.ballparkName = ballparkName;
    }
    
    public String getBallparkName() {
        return ballparkName;
    }

    public void setBallparkName(String ballparkName) {
        this.ballparkName = ballparkName;
    }
    
}
