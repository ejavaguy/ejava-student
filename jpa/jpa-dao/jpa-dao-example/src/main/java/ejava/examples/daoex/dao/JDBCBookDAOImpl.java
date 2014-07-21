package ejava.examples.daoex.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.PersistenceException;

import ejava.examples.daoex.bo.Book;

public class JDBCBookDAOImpl implements BookDAO {
	private Connection connection;
	
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Book create(Book book) throws PersistenceException {
		PreparedStatement statement=null;
		ResultSet rs = null;
		try {
			statement=connection.prepareStatement(
				"insert into JPADAO_BOOK (ID, DESCRIPTION, PAGES, TITLE) " +
				"values (null, ?, ?, ?)");
			statement.setString(1, book.getDescription());
			statement.setInt(2, book.getPages());
			statement.setString(3, book.getTitle());
			statement.execute();
			statement.close();
			
            Field id = Book.class.getDeclaredField("id");
            id.setAccessible(true);
            statement = connection.prepareStatement("call identity()");
            rs = statement.executeQuery();
            if (rs.next()) {
                id.set(book, rs.getLong(1));
            }
			
			return book;
		} catch (SQLException ex) { 
			throw new PersistenceException("SQL error creating book", ex);
		} catch (NoSuchFieldException ex) {
			throw new RuntimeException("Error locating id field", ex);
		} catch (SecurityException ex) {
			throw new RuntimeException("Security error setting id", ex);
		} catch (IllegalArgumentException ex) {
			throw new RuntimeException("Error setting id", ex);
		} catch (IllegalAccessException ex) {
			throw new RuntimeException("Access error setting id", ex);
		} finally {
			try { rs.close(); } catch (Exception ex){}
			try { statement.close(); } catch (Exception ex){}
		}
	}

	@Override
	public Book update(Book book) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Book get(long id) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove(Book book) throws PersistenceException {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Book> findAll(int start, int count) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

}
