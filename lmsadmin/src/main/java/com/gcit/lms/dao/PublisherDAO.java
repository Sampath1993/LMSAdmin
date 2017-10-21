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

import com.gcit.lms.entity.Publisher;

@Component
public class PublisherDAO extends BaseDAO<Publisher> implements ResultSetExtractor<List<Publisher>>{

	public void savePublisher(Publisher publisher) throws SQLException {
		template.update("INSERT INTO tbl_publisher (publisherName, publisherAddress, publisherPhone) VALUES (?,?,?)",
				new Object[] { publisher.getPublisherName(), publisher.getPublisherAddress(),
						publisher.getPublisherPhone() });
	}

	public Integer savePublisherWithID(Publisher publisher) throws SQLException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_publisher (publisherName, publisherAddress, publisherPhone) VALUES (?,?,?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement pstmt = connection.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, publisher.getPublisherName());
				pstmt.setString(2, publisher.getPublisherAddress());
				pstmt.setString(3, publisher.getPublisherPhone());
				return pstmt;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}

	public void updatePublisher(Publisher publisher) throws SQLException {
		template.update("UPDATE tbl_publisher SET publisherName = ? , publisherAddress = ?, publisherPhone = ? WHERE publisherId = ?",
				new Object[] { publisher.getPublisherName(), publisher.getPublisherAddress(), publisher.getPublisherPhone() ,publisher.getPublisherId() });
	}

	public void deletePublisher(Integer publisherId) throws SQLException {
		template.update("DELETE FROM tbl_publisher WHERE publisherId = ?", new Object[] { publisherId });
	}

	public List<Publisher> readPublishers(String publisherName) throws SQLException {
		if (publisherName != null && !publisherName.isEmpty()) {
			publisherName = "%" + publisherName + "%";
			return template.query("SELECT * FROM tbl_publisher WHERE publisherName like ?", new Object[] { publisherName }, this);
		} else {
			return template.query("SELECT * FROM tbl_publisher", this);
		}

	}

	public Publisher readPublisher(Integer publisherId) throws SQLException{
		List<Publisher> temp = template.query("SELECT * FROM tbl_publisher WHERE publisherId = ?", new Object[] { publisherId }, this);
		if(temp!=null && !temp.isEmpty())
		{
			return temp.get(0);
		}
		
		return null;
	}
	
	public List<Publisher> readPublishers(String publisherName, Integer pageNo) throws SQLException {
		setPageNo(pageNo);
		if(publisherName !=null && !publisherName.isEmpty()){
			publisherName = "%"+publisherName+"%";
			return template.query("SELECT * FROM tbl_publisher WHERE publisherName like ?", new Object[]{publisherName}, this);
		}else{
			return template.query("SELECT * FROM tbl_publisher", this);
		}
		
	}

	public Integer getPublishersCount() throws SQLException {
		return template.queryForObject("SELECT count(*) as COUNT FROM tbl_publisher", Integer.class);
	}
	
	public Publisher readPublisherByPK(Integer publisherId) throws SQLException {
		List<Publisher> publishers = template.query("SELECT * FROM tbl_publisher WHERE publisherId = ?", new Object[]{publisherId}, this);
		if(publishers!=null){
			return publishers.get(0);
		}
		return null;
	}
	
	@Override
	public List<Publisher> extractData(ResultSet rs) throws SQLException {
		List<Publisher> publishers = new ArrayList<>();
		while (rs.next()) {
			Publisher a = new Publisher();
			a.setPublisherId(rs.getInt("publisherId"));
			a.setPublisherName(rs.getString("publisherName"));
			a.setPublisherAddress(rs.getString("publisherAddress"));
			a.setPublisherPhone(rs.getString("publisherPhone"));
			publishers.add(a);
		}

		return publishers;
	}

}
