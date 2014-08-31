package ejava.examples.ejbsessionbank.ejb;

public interface Stats {
	void open();
	void close();
	int getTotal();
	int getDelta();
	void reset();
}
