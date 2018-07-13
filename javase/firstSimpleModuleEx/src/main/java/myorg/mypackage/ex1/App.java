package myorg.mypackage.ex1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public int returnOne() { 
        //System.out.println( "Here's One!" );
        logger.debug( "Here's One!" );
        return 1; 
    }

    public static void main( String[] args ) {
        //System.out.println( "Hello World!" );
        logger.info( "Hello World!" );
    }
}
