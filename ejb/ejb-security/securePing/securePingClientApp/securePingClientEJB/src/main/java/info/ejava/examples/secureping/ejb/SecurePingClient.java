package info.ejava.examples.secureping.ejb;

public interface SecurePingClient {
    boolean isCallerInRole(String role);
    String pingAll();
    String pingUser();
    String pingAdmin();
    String pingExcluded();
}
