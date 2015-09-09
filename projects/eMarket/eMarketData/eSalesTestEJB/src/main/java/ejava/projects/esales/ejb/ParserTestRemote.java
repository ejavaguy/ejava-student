package ejava.projects.esales.ejb;

import javax.ejb.Remote;

@Remote
public interface ParserTestRemote {
   void ingest() throws Exception;
}
