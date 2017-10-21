package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.lms.entity.BookCopies;

@Component
public class BookCopiesDAO extends BaseDAO<BookCopies> implements ResultSetExtractor<List<BookCopies>> {

	public void saveBookCopies(BookCopies bookCopies) throws SQLException {
		template.update("INSERT INTO tbl_book_copies (bookId, branchId, noOfCopies) VALUES (?,?,?)",
				new Object[] { bookCopies.getBookId(), bookCopies.getBranchId(), bookCopies.getNoOfCopies() });
	}

	public void saveBC(BookCopies bookCopies) throws SQLException {
		template.update("INSERT INTO tbl_book_copies (bookId, branchId, noOfCopies) VALUES (?,?,5)",
				new Object[] { bookCopies.getBookId(), bookCopies.getBranchId() });
	}

	public void updateBookCopies(BookCopies bookCopies) throws SQLException {
		template.update("UPDATE tbl_book_copies SET noOfCopies = ? WHERE bookId = ? and branchID = ?",
				new Object[] { bookCopies.getNoOfCopies(), bookCopies.getBookId(), bookCopies.getBranchId() });
	}

	public void deleteAuthor(BookCopies bookCopies) throws SQLException {
		template.update("DELETE FROM tbl_book_copies WHERE bookId = ? AND branchId = ?",
				new Object[] { bookCopies.getBookId(), bookCopies.getBranchId() });
	}

	public List<BookCopies> readBooks(String branchName) throws SQLException {
		if (branchName != null && !branchName.isEmpty()) {
			branchName = "%" + branchName + "%";
			return template.query(
					"SELECT * FROM tbl_book_copies, tbl_library_branch, tbl_book WHERE branchName like ? AND tbl_book_copies.bookId = tbl_book.bookId AND tbl_book_copies.branchId = tbl_library_branch.branchId",
					new Object[] { branchName }, this);
		} else {
			return template.query("SELECT * FROM tbl_book_copies", this);
		}

	}

	public Integer getCheckOutBooksCount(Integer branchId) throws SQLException {
		return template.queryForObject("SELECT count(*) AS COUNT FROM tbl_book_copies WHERE branchId=?",
				new Object[] { branchId }, Integer.class);
	}

	public BookCopies showBookCopies(Integer branchId, Integer bookId) throws SQLException {
		List<BookCopies> books = template.query("SELECT * FROM tbl_book_copies WHERE branchId = ? AND bookId = ?",
				new Object[] { branchId, bookId }, this);

		if (books != null) {
			return books.get(0);
		}
		return null;
	}

	public Integer countBookCopies(BookCopies bookCopies) throws SQLException {
		List<BookCopies> bc = template.query("SELECT * FROM tbl_book_copies WHERE branchId = ? AND bookId = ?",
				new Object[] { bookCopies.getBranchId(), bookCopies.getBookId() }, this);

		if (bc != null) {

			BookCopies bookCopy = bc.get(0);
			return bookCopy.getNoOfCopies();

		}
		return null;
	}

	public List<BookCopies> readBranches(String bookName) throws SQLException {
		if (bookName != null && !bookName.isEmpty()) {
			bookName = "%" + bookName + "%";
			return template.query(
					"SELECT * FROM tbl_book_copies, tbl_library_branch, tbl_book WHERE title like ? AND tbl_book_copies.bookId = tbl_book.bookId AND tbl_book_copies.branchId = tbl_library_branch.branchId",
					new Object[] { bookName }, this);
		} else {
			return template.query("SELECT * FROM tbl_book_copies", this);
		}

	}

	public BookCopies getBookCopies(Integer bookId, Integer branchId) {
		BookCopies bookCopies = new BookCopies();
		bookCopies.setBookId(bookId);
		bookCopies.setBranchId(branchId);
		return bookCopies;
	}

	public void checkOutBookCopies(Integer bookId, Integer branchId) throws SQLException {
		template.update("UPDATE tbl_book_copies SET noOfCopies = noOfCopies-1 WHERE bookId = ? and branchID = ?",
				new Object[] { bookId, branchId });
	}

	public void returnBookCopies(Integer bookId, Integer branchId) throws SQLException {
		template.update("UPDATE tbl_book_copies SET noOfCopies = noOfCopies+1 WHERE bookId = ? and branchID = ?",
				new Object[] { bookId, branchId });
	}

	@Override
	public List<BookCopies> extractData(ResultSet rs) throws SQLException {
		List<BookCopies> bookCopies = new ArrayList<>();
		while (rs.next()) {
			BookCopies a = new BookCopies();
			a.setBookId(rs.getInt("bookId"));
			a.setBranchId(rs.getInt("branchId"));
			a.setNoOfCopies(rs.getInt("noOfCopies"));
			bookCopies.add(a);
		}
		return bookCopies;
	}

}
