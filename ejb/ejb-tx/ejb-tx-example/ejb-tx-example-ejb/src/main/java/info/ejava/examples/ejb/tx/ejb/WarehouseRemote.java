package info.ejava.examples.ejb.tx.ejb;

import java.util.List;

import info.ejava.examples.ejb.tx.bo.Product;
import info.ejava.examples.ejb.tx.bo.Shipment;

import javax.ejb.Remote;

@Remote
public interface WarehouseRemote {

    int cleanup();
    Product getProduct(int productId);
    Product createProductAndShipments(Product product, List<Shipment> shipments) throws UnexpectedState;
    Product createProductAndShipments_NotSupported(Product product, List<Shipment> shipments) throws UnexpectedState;
    Product createProductAndShipments_RequiresNew(Product product, List<Shipment> shipments) throws UnexpectedState;
    Product createProductAndShipments_Async(Product product, int quantity) throws UnexpectedState;
    Product createProductAndThrowChecked(Product product) throws MyCheckedProductException;
    Product createProductAndThrowCheckedRollback(Product product) throws MyCheckedRollbackProductException;
    Product createProductAndRollback(Product product);
}
