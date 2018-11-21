package ejava.examples.jms20.jmsmechanics;

import javax.jms.Message;

public interface MyClient {
    int getCount();
    Message getMessage() throws Exception;
}
