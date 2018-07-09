package info.ejava.examples.secureping.ejb;

public interface SecurePingClient {
    String whoAmI();
    boolean isCallerInRole(String role);
    String pingAll();
    String pingUser();
    String pingAdmin();
    String pingExcluded();
}
