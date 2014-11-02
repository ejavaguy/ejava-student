/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */ 
package javaeetutorial.hello1;
    
    
import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named      
@RequestScoped
public class Counter implements Serializable {
        
    private int value;
    
    @PostConstruct
    public void init() {
        value=0;
    }

    public Counter() {
    }

    public int getValue() {
        return value++;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
