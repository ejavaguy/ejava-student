package ejava.projects.eleague.ejb;

import javax.ejb.Remote;

@Remote
public interface ParserTestRemote {
   void ingest() throws Exception;
}
