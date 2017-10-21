package com.gcit.lms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Publisher;

@Component
public class BookDAO extends BaseDAO<Book> implements ResultSetExtractor<List<Book>>{

	public void saveBook(Book book) throws SQLException {
		template.update("INSERT INTO tbl_book (title,pubId) VALUES (?,?)", new Object[] { book.getTitle(), book.getPublisher().getPublisherId() });
	}

	public void saveBookAuthor(Book book) throws SQLException {
		for (Author a : book.getAuthors()) {
			template.update("INSERT INTO tbl_book_authors VALUES (?, ?)", new Object[] { book.getBookId(), a.getAuthorId() });
		}
	}
	
	public void deleteBookAuthor(Book book) throws SQLException {
		template.update("DELETE FROM tbl_book_authors WHERE bookId = ?", new Object[] { book.getBookId() });
	}

	public Integer saveBookID(Book book) throws SQLException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_book (title,pubId) VALUES (?,?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement pstmt = connection.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, book.getTitle());
				pstmt.setInt(2, book.getPublisher().getPublisherId());
				return pstmt;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}

	public void updateBook(Book book) throws SQLException {
		template.update("UPDATE tbl_book SET title = ?, pubId = ? WHERE bookId = ?", new Object[] { book.getTitle(), book.getPublisher().getPublisherId(), book.getBookId() });
	}

	public void deleteBook(Integer bookId) throws SQLException {
		template.update("DELETE FROM tbl_book WHERE bookId = ?", new Object[] { bookId });
	}

	public List<Book> readBooksByBranch(Integer branchId, Integer pageNo) throws SQLException {
		if (pageNo != null) {
			setPageNo(pageNo);
			String sqlAdd = " LIMIT " + (pageNo - 1) * 10 + ", 10";
		return template.query("SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_copies WHERE branchId = ?)" +sqlAdd,
				new Object[] { branchId }, this);
		} else {
			return template.query("SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_copies WHERE branchId = ?)",
					new Object[] { branchId }, this);
		}
	}

	public List<Book> readBooksByBorrower(Integer branchId, Integer cardNo, Integer pageNo) throws SQLException {
		if (pageNo != null) {
			setPageNo(pageNo);
			String sqlAdd = " LIMIT " + (pageNo - 1) * 10 + ", 10";
		return template.query(
				"SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_loans WHERE branchId = ? AND cardNo = ? AND dateIn IS null)" + sqlAdd,
				new Object[] { branchId, cardNo }, this);
		} else {
			return template.query(
					"SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_loans WHERE branchId = ? AND cardNo = ? AND dateIn IS null)",
					new Object[] { branchId, cardNo }, this);
		}
	}

	public List<Book> readAllBooks() throws SQLException {
		return template.query("SELECT * FROM tbl_book", this);
	}

	public List<Book> readBooksByTitle(String bookTitle) throws SQLException {
		bookTitle = "%" + bookTitle + "%";
		return template.query("SELECT * FROM tbl_book WHERE title like ?", new Object[] { bookTitle }, this);
	}

	public Integer getBooksCount() throws SQLException {
		return template.queryForObject("SELECT count(*) as COUNT FROM tbl_book", Integer.class);
	}
	
	public List<Book> readBooksByAuthor(Integer authorId) throws SQLException{
		return template.query("SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_authors WHERE authorId=?)", new Object[] {authorId}, this);
	}
	
	public List<Book> readBooks(String bookName, Integer pageNo) throws SQLException {
		if (pageNo != null) {
			setPageNo(pageNo);
			String sqlAdd = " LIMIT " + (pageNo - 1) * 10 + ", 10";
		if(bookName !=null && !bookName.isEmpty()){
			bookName = "%"+bookName+"%";
			return template.query("SELECT * FROM tbl_book WHERE title like ?" + sqlAdd, new Object[]{bookName}, this);
		}else{
			return template.query("SELECT * FROM tbl_book" + sqlAdd, this);
		}
		}else {
			return template.query("SELECT * FROM tbl_author", this);
		}
		
	}
	
	public Book readBookByPK(Integer bookId) throws SQLException {
		List<Book> books = template.query("SELECT * FROM tbl_book WHERE bookId = ?", new Object[]{bookId}, this);
		if(books!=null){
			return books.get(0);
		}
		return null;
	}
	
	
	public List<Book> readBooksByAuthor(Author author) throws SQLException{
		return template.query("SELECT * FROM tbl_book WHERE bookId IN (SELECT bookId FROM tbl_book_authors WHERE authorId = ?)", new Object[]{author.getAuthorId()}, this);
	}
	
	@Override
	public List<Book> extractData(ResultSet rs) throws SQLException {
		List<Book> books = new ArrayList<>();
		while (rs.next()) {
			Book b = new Book();
			b.setBookId(rs.getInt("bookId"));
			b.setTitle(rs.getString("title"));
			Publisher pub = new Publisher();
			pub.setPublisherId(rs.getInt("pubId"));
			b.setPublisher(pub);
			books.add(b);
		}
		return books;
	}
}
