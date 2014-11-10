package info.ejava.examples.secureping.ejb;

public interface SecurePing {
    boolean isCallerInRole(String role);
    String getPrincipal();
    String pingAll();
    String pingUser();
    String pingAdmin();
    String pingExcluded();
}
