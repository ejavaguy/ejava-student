package ejava.projects.edmv.ejb;

import javax.ejb.Remote;

@Remote
public interface ParserTestRemote {
   void ingest() throws Exception;
}
