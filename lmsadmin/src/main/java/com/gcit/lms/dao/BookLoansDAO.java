package com.gcit.lms.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.gcit.lms.entity.BookLoans;

@Component
public class BookLoansDAO extends BaseDAO<BookLoans> implements ResultSetExtractor<List<BookLoans>>{

	public void saveBookLoan(Integer branchId, Integer cardNo, Integer bookId) throws SQLException {
		template.update("INSERT INTO tbl_book_loans (bookId, branchId, cardNo, dateOut, dueDate) VALUES (?,?,?,now(),date_add(now(), INTERVAL 1 WEEK))",
				new Object[] { bookId, branchId, cardNo });
	}

	public void saveBookLoanComplete(BookLoans bookLoans) throws SQLException {
		template.update("INSERT INTO tbl_book_loans (bookId, branchId, cardNo, dateOut, dueDate, dateIn) VALUES (?,?,?,?,?,?)",
				new Object[] { bookLoans.getBookId(), bookLoans.getBranchId(), bookLoans.getCardNo(),
						bookLoans.getDateOut(), bookLoans.getDueDate(), bookLoans.getDateIn() });
	}

	public void overrideDueDate(BookLoans bookLoans) throws SQLException {
		template.update("UPDATE tbl_book_loans SET dueDate = date_add(dueDate, INTERVAL 1 week) WHERE bookId = ? and branchId = ? and cardNo = ?",
				new Object[] { bookLoans.getBookId(), bookLoans.getBranchId(), bookLoans.getCardNo() });
	}

	public void updateBookLoanDueDate(BookLoans bookLoans) throws SQLException {
		template.update("UPDATE tbl_book_loans SET dueDate = ? WHERE bookId = ? and branchId = ? and cardNo = ?", new Object[] {
				bookLoans.getDueDate(), bookLoans.getBookId(), bookLoans.getBranchId(), bookLoans.getCardNo() });
	}

	public void updateBookLoanDateIn(Integer branchId, Integer cardNo, Integer bookId) throws SQLException {
		template.update("UPDATE tbl_book_loans SET dateIn = now() WHERE bookId = ? and branchId = ? and cardNo = ?",
				new Object[] { bookId, branchId, cardNo });
	}

	public void deleteBookLoan(Integer branchId, Integer cardNo, Integer bookId) throws SQLException {
		template.update("DELETE FROM tbl_book_loans WHERE bookId = ? and branchId = ? and cardNo = ?",
				new Object[] { bookId, branchId, cardNo });
	}

	public Integer getBookLoansCount() throws SQLException {
		return template.queryForObject("SELECT count(*) as COUNT FROM tbl_book_loans WHERE dateIn IS null", Integer.class);
	}

	public Integer getLoanedOutBooksCount(BookLoans bookLoans) throws SQLException {
		return template.queryForObject(
				"SELECT count(*) as COUNT FROM tbl_book_loans WHERE bookId = ? AND branchid = ? AND cardNo = ? AND dateIn IS null",
				new Object[] { bookLoans.getBookId(), bookLoans.getBranchId(), bookLoans.getCardNo() }, Integer.class);
	}

	public List<BookLoans> readBookLoans(String name) throws SQLException {
		if (name != null && !name.isEmpty()) {
			name = "%" + name + "%";
			return template.query(
					"SELECT * FROM tbl_book_loans, tbl_borrower WHERE tbl_borrower.name = ? and tbl_borrower.cardNo = tbl_book_loans.cardNo",
					new Object[] { name }, this);
		} else {
			return template.query("SELECT * FROM tbl_book_loans", this);
		}

	}

	public BookLoans getBookLoans(Integer branchId, Integer bookId, Integer cardNo) throws SQLException {
		BookLoans bookLoans = new BookLoans();
		bookLoans.setBookId(bookId);
		bookLoans.setBranchId(branchId);
		bookLoans.setCardNo(cardNo);
		return bookLoans;
	}
	
	public List<BookLoans> readAllLoans(String name, Integer pageNo) throws SQLException {
		return template.query("SELECT * FROM tbl_book_loans WHERE dateIn IS null", this);
	}

	@Override
	public List<BookLoans> extractData(ResultSet rs) throws SQLException {
		List<BookLoans> bookLoans = new ArrayList<>();
		while (rs.next()) {
			BookLoans a = new BookLoans();
			a.setBookId(rs.getInt("bookId"));
			a.setBranchId(rs.getInt("branchId"));
			a.setCardNo(rs.getInt("cardNo"));
			a.setDateOut(rs.getString("dateOut"));
			a.setDueDate(rs.getString("dueDate"));
			a.setDateIn(rs.getString("dateIn"));
			bookLoans.add(a);
		}
		return bookLoans;
	}

}
