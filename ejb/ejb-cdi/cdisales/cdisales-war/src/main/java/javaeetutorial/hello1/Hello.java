/**
 * Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved.
 *
 * You may not modify, use, reproduce, or distribute this software except in
 * compliance with  the terms of the License at:
 * http://java.net/projects/javaeetutorial/pages/BerkeleyLicense
 */ 
package javaeetutorial.hello1;
    
    
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named      
@RequestScoped
public class Hello {
    private static final Logger logger = LoggerFactory.getLogger(Hello.class);
        
    private String name;

    public Hello() {
    }

    public String getName() {
        logger.debug("getName()={}", name);
        return name;
    }

    public void setName(String user_name) {
        this.name = user_name;
    }
}
