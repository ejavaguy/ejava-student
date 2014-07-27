package ejava.examples.orm.ejbql;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Receipt implements Serializable {
    private static final long serialVersionUID = 1L;
    private long saleId;
    private long customerId;
    private Date date;
    private double amount;
    
    public Receipt(long saleId, long customerId, Date date, BigDecimal amount) {
        this(customerId, saleId, date, amount.doubleValue());
    }
    public Receipt(long saleId, long customerId, Date date, double amount) {
        this.customerId = customerId;
        this.saleId = saleId;
        this.date = date;
        this.amount = amount;
    }
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append("sale=" + saleId);
        text.append(", customer=" + customerId);
        text.append(", date=" + date);
        text.append(", amount=" + amount);
        return text.toString();
    }
    public double getAmount() {
        return amount;
    }
    public long getCustomerId() {
        return customerId;
    }
    public Date getDate() {
        return date;
    }
    public long getSaleId() {
        return saleId;
    }
}
