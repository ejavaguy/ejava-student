package ejava.jpa.examples.query;

import java.io.Serializable;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.US);
        StringBuilder text = new StringBuilder();
        text.append("sale=" + saleId);
        text.append(", customer=" + customerId);
        text.append(", date=" + (date==null ? null : df.format(date)));
        text.append(", amount=" + nf.format(amount));
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
