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

import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.Library_Branch;

@Component
public class Library_BranchDAO extends BaseDAO<Library_Branch> implements ResultSetExtractor<List<Library_Branch>>{

	public void saveLibraryBranch(Library_Branch library_Branch) throws SQLException {
		template.update("INSERT INTO tbl_library_branch (branchName,branchAddress) VALUES (?,?)",
				new Object[] { library_Branch.getBranchName(), library_Branch.getBranchAddress() });
	}

	public void saveBookLibraryBranch(Library_Branch branch) throws SQLException {
		for (BookCopies b : branch.getBookCopies()) {
			template.update("INSERT INTO tbl_book_copies VALUES (?,?,?)",
					new Object[] { b.getBookId(), branch.getBranchId(), b.getNoOfCopies() });
		}
	}

	public Integer saveLibraryBranchWithID(Library_Branch library_Branch) throws SQLException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_library_Branch (branchName, branchAddress) VALUES (?,?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement pstmt = connection.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, library_Branch.getBranchName());
				pstmt.setString(2, library_Branch.getBranchAddress());
				return pstmt;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}

	public void updateBranch(Library_Branch library_Branch) throws SQLException {
		template.update("UPDATE tbl_library_branch SET branchName = ?, branchAddress = ? WHERE branchId = ?",
				new Object[] { library_Branch.getBranchName(), library_Branch.getBranchAddress(), library_Branch.getBranchId() });
	}

	public void deleteLibraryBranch(Integer branchId) throws SQLException {
		template.update("DELETE FROM tbl_library_Branch WHERE BranchId = ?", new Object[] { branchId });
	}

	public List<Library_Branch> readBranch(String branchName) throws SQLException {
		if (branchName != null && !branchName.isEmpty()) {
			branchName = "%" + branchName + "%";
			return template.query("SELECT * FROM tbl_library_branch WHERE branchName like ?", new Object[] { branchName }, this);
		} else {
			return template.query("SELECT * FROM tbl_library_branch", this);
		}

	}

	public List<Library_Branch> readBranches(String branchName, Integer pageNo) throws SQLException {
		setPageNo(pageNo);
		if(branchName !=null && !branchName.isEmpty()){
			branchName = "%"+branchName+"%";
			return template.query("SELECT * FROM tbl_library_branch WHERE branchName like ?", new Object[]{branchName}, this);
		}else{
			return template.query("SELECT * FROM tbl_library_branch", this);
		}
		
	}

	public Integer getBranchesCount() throws SQLException {
		return template.queryForObject("SELECT count(*) as COUNT FROM tbl_library_branch", Integer.class);
	}
	
	public Library_Branch readBranchByPK(Integer branchId) throws SQLException {
		List<Library_Branch> branches = template.query("SELECT * FROM tbl_library_branch WHERE branchId = ?", new Object[]{branchId}, this);
		if(branches!=null){
			return branches.get(0);
		}
		return null;
	}	
	
	@Override
	public List<Library_Branch> extractData(ResultSet rs) throws SQLException {
		List<Library_Branch> branchs = new ArrayList<>();
		while (rs.next()) {
			Library_Branch a = new Library_Branch();
			a.setBranchId(rs.getInt("branchId"));
			a.setBranchName(rs.getString("branchName"));
			a.setBranchAddress(rs.getString("branchAddress"));
			branchs.add(a);
		}
		return branchs;
	}
}
