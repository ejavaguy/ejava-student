package ejava.examples.daoex.dao;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
		try (PreparedStatement insertStatement=getInsertPreparedStatement(connection, book);
		     PreparedStatement idStatement=getIdentityStatement(connection)){
			insertStatement.execute();
			
            try (ResultSet rs = idStatement.executeQuery()) {
                if (rs.next()) {
                    Field id = Book.class.getDeclaredField("id");
                    id.setAccessible(true);
                    id.set(book, rs.getLong(1));
                } else {
                    throw new PersistenceException("no identity returned from database");
                }                
            } catch (NoSuchFieldException ex) {
                throw new PersistenceException("Error locating id field", ex);
            } catch (IllegalAccessException ex) {
                throw new PersistenceException("Access error setting id", ex);
            }
			
			return book;
		} catch (SQLException ex) { 
			throw new PersistenceException("SQL error creating book", ex);
		}
	}
	
	private PreparedStatement getInsertPreparedStatement(Connection c, Book book) throws SQLException {
        PreparedStatement statement=connection.prepareStatement(
                "insert into JPADAO_BOOK (ID, DESCRIPTION, PAGES, TITLE) " +
                "values (null, ?, ?, ?)");
        statement.setString(1, book.getDescription());
        statement.setInt(2, book.getPages());
        statement.setString(3, book.getTitle());
        return statement;
	}
	
	private PreparedStatement getIdentityStatement(Connection c) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("call identity()");
        return statement;
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
