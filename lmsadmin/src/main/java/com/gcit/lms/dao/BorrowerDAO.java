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

import com.gcit.lms.entity.Borrower;

@Component
public class BorrowerDAO extends BaseDAO<Borrower> implements ResultSetExtractor<List<Borrower>>{

	public void saveBorrower(Borrower borrower) throws SQLException {
		template.update("INSERT INTO tbl_borrower (name,address,phone) VALUES (?,?,?)", new Object[] { borrower.getName() , borrower.getAddress(), borrower.getPhone()});
	}
	
	public Integer saveBorrowerWithID(Borrower borrower) throws SQLException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_borrower (name,address,phone) VALUES (?,?,?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement pstmt = connection.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, borrower.getName());
				pstmt.setString(2, borrower.getAddress());
				pstmt.setString(3, borrower.getPhone());
				return pstmt;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}

	public void updateBorrower(Borrower borrower) throws SQLException {
		template.update("UPDATE tbl_borrower SET name = ?, address = ?, phone = ? WHERE cardNo = ?",
				new Object[] { borrower.getName(), borrower.getAddress(), borrower.getPhone(), borrower.getCardNo() });
	}

	public void deleteBorrower(Integer cardNo) throws SQLException {
		template.update("DELETE FROM tbl_borrower WHERE cardNo = ?", new Object[] { cardNo });
	}
	
	
	public List<Borrower> readBorrowers(String name) throws SQLException {
		if(name !=null && !name.isEmpty()){
			name = "%"+name+"%";
			return template.query("SELECT * FROM tbl_borrower WHERE name like ?", new Object[]{name}, this);
		}else{
			return template.query("SELECT * FROM tbl_borrower", this);
		}
		
	}

	public List<Borrower> readBorrowers(String borrowerName, Integer pageNo) throws SQLException {
		setPageNo(pageNo);
		if(borrowerName !=null && !borrowerName.isEmpty()){
			borrowerName = "%"+borrowerName+"%";
			return template.query("SELECT * FROM tbl_borrower WHERE name like ?", new Object[]{borrowerName}, this);
		}else{
			return template.query("SELECT * FROM tbl_borrower", this);
		}
		
	}

	public Integer getBorrowersCount() throws SQLException {
		return template.queryForObject("SELECT count(*) as COUNT FROM tbl_borrower", Integer.class);
	}
	
	public Borrower readBorrowerByPK(Integer borrowerId) throws SQLException {
		List<Borrower> borrowers = template.query("SELECT * FROM tbl_borrower WHERE cardNo = ?", new Object[]{borrowerId}, this);
		if(borrowers!=null){
			return borrowers.get(0);
		}
		return null;
	}
	
	@Override
	public List<Borrower> extractData(ResultSet rs) throws SQLException {
		List<Borrower> borrowers = new ArrayList<>();
		while(rs.next()){
			Borrower a = new Borrower();
			a.setCardNo(rs.getInt("cardNo"));
			a.setName(rs.getString("name"));
			a.setAddress(rs.getString("address"));
			a.setPhone(rs.getString("phone"));
			borrowers.add(a);
		}
		
		return borrowers;
	}
	
	public boolean validateCard(Integer cardNo) throws SQLException {
		List<Borrower> temp = template.query("SELECT * FROM tbl_borrower WHERE cardNo = ?", new Object[]{cardNo}, this);
		if(!temp.isEmpty())
			return true;
		return false;
	}
}
