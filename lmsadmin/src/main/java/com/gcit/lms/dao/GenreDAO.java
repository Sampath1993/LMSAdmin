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

import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.Genre;

@Component
public class GenreDAO extends BaseDAO <Genre> implements ResultSetExtractor<List<Genre>>{
	
	public void saveGenre(Genre genre) throws SQLException {
		template.update("INSERT INTO tbl_genre (genre_name) VALUES (?)", new Object[] { genre.getGenreName() });
	}
	
	public void saveBookGenre(Genre genre) throws SQLException {
		for(Book b: genre.getBooks()){
			template.update("INSERT INTO tbl_book_genres VALUES (?,?)", new Object[] { genre.getGenreId(), b.getBookId()});
		}
	}
	
	public void deleteBookGenre(Genre genre) throws SQLException {
		template.update("DELETE FROM tbl_book_genres WHERE genre_id = ?", new Object[] {genre.getGenreId()});
	}
	
	public Integer saveGenreWithID(Genre genre) throws SQLException {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		final String sql = "INSERT INTO tbl_genre (genre_name) VALUES (?)";
		template.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement pstmt = connection.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, genre.getGenreName());
				return pstmt;
			}
		}, keyHolder);
		
		return keyHolder.getKey().intValue();
	}

	public void updateGenre(Genre genre) throws SQLException {
		template.update("UPDATE tbl_genre SET genre_name = ? WHERE genre_id = ?",
				new Object[] { genre.getGenreName(), genre.getGenreId() });
	}

	public void deleteGenre(Integer genreId) throws SQLException {
		template.update("DELETE FROM tbl_genre WHERE genre_id = ?", new Object[] { genreId });
	}
	
	public List<Genre> readGenreByBook(Integer bookId) throws SQLException {
		return template.query("SELECT * FROM tbl_genre WHERE genre_id IN (SELECT genre_id FROM tbl_book_genres WHERE bookId=?)", new Object[] {bookId}, this);
	}
	
	public Genre readGenreByPK(Integer genreId) throws SQLException {
		List<Genre> genres = template.query("SELECT * FROM tbl_genre WHERE genre_id = ?", new Object[]{genreId}, this);
		if(genres!=null){
			return genres.get(0);
		}
		return null;
	}
	
	public List<Genre> readGenres(String genreName) throws SQLException {
		if(genreName !=null && !genreName.isEmpty()){
			genreName = "%"+genreName+"%";
			return template.query("SELECT * FROM tbl_genre WHERE genre_name like ?", new Object[]{genreName}, this);
		}else{
			return template.query("SELECT * FROM tbl_genre", this);
		}
		
	}
	
	public List<Genre> readGenres(String genreName, Integer pageNo) throws SQLException {
		setPageNo(pageNo);
		if(genreName !=null && !genreName.isEmpty()){
			genreName = "%"+genreName+"%";
			return template.query("SELECT * FROM tbl_genre WHERE genreName like ?", new Object[]{genreName}, this);
		}else{
			return template.query("SELECT * FROM tbl_genre", this);
		}
		
	}

	public Integer getGenresCount() throws SQLException {
		return template.queryForObject("SELECT count(*) as COUNT FROM tbl_genre", Integer.class);
	}
	
	@Override
	public List<Genre> extractData(ResultSet rs) throws SQLException {
		List<Genre> genres = new ArrayList<>();
		while(rs.next()){
			Genre a = new Genre();
			a.setGenreId(rs.getInt("genre_id"));
			a.setGenreName(rs.getString("genre_name"));
			genres.add(a);
		}
		
		return genres;
	}
}
