package myorg.mypackage.ex1;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class App {
    private static Log log = LogFactory.getLog(App.class);

    public int returnOne() { 
        //System.out.println( "Here's One!" );
        log.debug( "Here's One!" );
        return 1; 
    }

    public static void main( String[] args ) {
        //System.out.println( "Hello World!" );
        log.info( "Hello World!" );
    }
}
