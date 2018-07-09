package info.ejava.examples.secureping.ejb;

public interface SecurePing {
    String whoAmI();
    boolean isCallerInRole(String role);
    String pingAll();
    String pingUser();
    String pingAdmin();
    String pingExcluded();
}
