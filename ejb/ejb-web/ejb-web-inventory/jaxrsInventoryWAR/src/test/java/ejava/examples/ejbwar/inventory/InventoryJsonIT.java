package ejava.examples.ejbwar.inventory;

/**
 * This class configures the JAX-RS based integration test for use with
 * application/json.
 */
public class InventoryJsonIT extends InventoryITBase {
    @Override
    protected void setOptions(InventoryTestConfig config) {
        config.setMediaType("application/json");
    }
}
