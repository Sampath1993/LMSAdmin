package com.gcit.lms.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gcit.lms.dao.AuthorDAO;
import com.gcit.lms.dao.BookCopiesDAO;
import com.gcit.lms.dao.BookDAO;
import com.gcit.lms.dao.BookLoansDAO;
import com.gcit.lms.dao.BorrowerDAO;
import com.gcit.lms.dao.GenreDAO;
import com.gcit.lms.dao.Library_BranchDAO;
import com.gcit.lms.dao.PublisherDAO;
import com.gcit.lms.entity.Author;
import com.gcit.lms.entity.Book;
import com.gcit.lms.entity.BookCopies;
import com.gcit.lms.entity.BookLoans;
import com.gcit.lms.entity.Borrower;
import com.gcit.lms.entity.Genre;
import com.gcit.lms.entity.Library_Branch;
import com.gcit.lms.entity.Publisher;

@RestController
@Transactional
public class AdminService {

	@Autowired
	AuthorDAO adao;

	@Autowired
	BookDAO bdao;

	@Autowired
	GenreDAO gdao;

	@Autowired
	PublisherDAO pdao;

	@Autowired
	BorrowerDAO brdao;

	@Autowired
	Library_BranchDAO lbdao;

	@Autowired
	BookCopiesDAO bcdao;

	@Autowired
	BookLoansDAO bldao;

	@RequestMapping(value="/Author", method = RequestMethod.POST, consumes="application/json")
	public void saveAuthor(@RequestBody Author author) throws SQLException {
		if (author.getAuthorId() != null) {
			adao.updateAuthor(author);
//			adao.deleteBookAuthor(author);
//			adao.saveBookAuthor(author);
		} else {
			int id = adao.saveAuthorWithID(author);
			author.setAuthorId(id);
//			adao.saveBookAuthor(author);
		}
	}

	@RequestMapping(value="/Author/{authorId}", method = RequestMethod.DELETE, consumes="application/json")
	public void deleteAuthor(@PathVariable Integer authorId) throws SQLException {
		adao.deleteAuthor(authorId);
	}

//	@RequestMapping(value="/Author/{searchString}", method = RequestMethod.GET, produces="application/json")
//	public List<Author> readAuthors(@PathVariable String searchString) throws SQLException {
//		return adao.readAuthors(searchString);
//	}

	@RequestMapping(value="/Authors", method = RequestMethod.GET, produces= {"application/json", "application/xml"})
	public List<Author> readAuthors(@RequestParam(value="q", required=false) String searchString, @RequestParam(value="pageNo", required=false) Integer pageNo) throws SQLException {
		List<Author> authors = adao.readAuthors(searchString, pageNo);
//		for (Author a : authors) {
//			a.setBooks(bdao.readBooksByAuthor(a));
//		}
		return authors;
	}

	@RequestMapping(value="/Genres", method = RequestMethod.GET, produces="application/json")
	public List<Genre> readGenres(@RequestParam(value="q", required=false) String searchString, @RequestParam(value="pageNo", required=false) Integer pageNo) throws SQLException {
		return gdao.readGenres(searchString, pageNo);
	}

	@RequestMapping(value="/Publishers", method = RequestMethod.GET, produces="application/json")
	public List<Publisher> readPublishers(@RequestParam(value="q", required=false) String searchString, @RequestParam(value="pageNo", required=false) Integer pageNo) throws SQLException {
		return pdao.readPublishers(searchString, pageNo);
	}

	@RequestMapping(value="/Borrowers", method = RequestMethod.GET, produces="application/json")
	public List<Borrower> readBorrowers(@RequestParam(value="q", required=false) String searchString, @RequestParam(value="pageNo", required=false) Integer pageNo) throws SQLException {
		return brdao.readBorrowers(searchString, pageNo);
	}

	@RequestMapping(value="/Branches", method = RequestMethod.GET, produces="application/json")
	public List<Library_Branch> readLibraryBranch(@RequestParam(value="q", required=false) String searchString, @RequestParam(value="pageNo", required=false) Integer pageNo) throws SQLException {
		return lbdao.readBranches(searchString, pageNo);
	}

	@RequestMapping(value="/Books", method = RequestMethod.GET, produces="application/json")
	public List<Book> readBooks(@RequestParam(value="q", required=false) String searchString, @RequestParam(value="pageNo", required=false) Integer pageNo) throws SQLException {
		List<Book> books = bdao.readBooks(searchString, pageNo);
		for (Book b : books) {
			b.setAuthors(adao.readAuthorsByBook(b.getBookId()));
			b.setGenres(gdao.readGenreByBook(b.getBookId()));
			b.setPublisher(pdao.readPublisherByPK(b.getPublisher().getPublisherId()));
		}
		return books;
	}

	@RequestMapping(value="/BookLoans", method = RequestMethod.GET, produces="application/json")
	public List<BookLoans> readAllLoans(@RequestParam(value="q", required=false) String searchString, @RequestParam(value="pageNo", required=false) Integer pageNo) throws SQLException {
		return bldao.readAllLoans(searchString, pageNo);
	}

	@RequestMapping(value="/Author/{authorId}", method = RequestMethod.GET, produces="application/json")
	public Author readAuthorByPK(@PathVariable Integer authorId) throws SQLException {
		Author author = adao.readAuthorByPK(authorId);
//		author.setBooks(bdao.readBooksByAuthor(authorId));
		return author;
	}

	@RequestMapping(value="/Genre/{genreId}", method = RequestMethod.GET, produces="application/json")
	public Genre readGenreByPK(@PathVariable Integer genreId) throws SQLException {
		return gdao.readGenreByPK(genreId);
	}

	@RequestMapping(value="/Publisher/{publisherId}", method = RequestMethod.GET, produces="application/json")
	public Publisher readPublisherByPK(@PathVariable Integer publisherId) throws SQLException {
		return pdao.readPublisherByPK(publisherId);
	}

	@RequestMapping(value="/Borrower/{borrowerId}", method = RequestMethod.GET, produces="application/json")
	public Borrower readBorrowerByPK(@PathVariable Integer borrowerId) throws SQLException {
		return brdao.readBorrowerByPK(borrowerId);
	}

	@RequestMapping(value="/Branch/{branchId}", method = RequestMethod.GET, produces="application/json")
	public Library_Branch readBranchByPK(@PathVariable Integer branchId) throws SQLException {
		return lbdao.readBranchByPK(branchId);
	}

	@RequestMapping(value="/Book/{bookId}", method = RequestMethod.GET, produces="application/json")
	public Book readBookByPK(@PathVariable Integer bookId) throws SQLException {
		Book book = bdao.readBookByPK(bookId);
		book.setAuthors(adao.readAuthorsByBook(bookId));
		book.setGenres(gdao.readGenreByBook(bookId));
		if (book.getPublisher().getPublisherId() != null) {
			book.setPublisher(pdao.readPublisher(book.getPublisher().getPublisherId()));
		}
		return book;
	}

	@RequestMapping(value="/AuthorsCount", method = RequestMethod.GET, produces="application/json")
	public Integer getAuthorsCount() throws SQLException {
		return adao.getAuthorsCount();
	}

	@RequestMapping(value="/GenresCount", method = RequestMethod.GET, produces="application/json")
	public Integer getGenresCount() throws SQLException {
		return gdao.getGenresCount();
	}

	@RequestMapping(value="/PublishersCount", method = RequestMethod.GET, produces="application/json")
	public Integer getPublishersCount() throws SQLException {
		return pdao.getPublishersCount();
	}

	@RequestMapping(value="/BorrowersCount", method = RequestMethod.GET, produces="application/json")
	public Integer getBorrowersCount() throws SQLException {
		return brdao.getBorrowersCount();
	}

	@RequestMapping(value="/BranchesCount", method = RequestMethod.GET, produces="application/json")
	public Integer getBranchesCount() throws SQLException {
		return lbdao.getBranchesCount();
	}

	@RequestMapping(value="/BooksCount", method = RequestMethod.GET, produces="application/json")
	public Integer getBooksCount() throws SQLException {
		return bdao.getBooksCount();
	}

	@RequestMapping(value="/BookLoansCount", method = RequestMethod.GET, produces="application/json")
	public Integer getBookLoansCount() throws SQLException {
		return bldao.getBookLoansCount();
	}

	@RequestMapping(value="/Book", method = RequestMethod.POST, produces="application/json", consumes="application/json")
	public Integer saveBook(@RequestBody Book book) throws SQLException {
		int id = 0;
		if (book.getBookId() != null) {
			bdao.updateBook(book);
			bdao.deleteBookAuthor(book);
			bdao.saveBookAuthor(book);
		} else {
			id = bdao.saveBookID(book);
			book.setBookId(id);
			bdao.saveBookAuthor(book);
		}
		return id;
	}

	@RequestMapping(value="/Book/{bookId}", method = RequestMethod.DELETE, consumes="application/json")
	public void deleteBook(@PathVariable Integer bookId) throws SQLException {
		bdao.deleteBook(bookId);
	}

//	@RequestMapping(value="/readBooks/{searchString}", method = RequestMethod.GET, produces="application/json")
//	public List<Book> readBooks(@PathVariable String searchString) throws SQLException {
//		return bdao.readBooksByTitle(searchString);
//	}

//	@RequestMapping(value="/Books", method = RequestMethod.GET, produces="application/json")
//	public List<Book> readBooks() throws SQLException {
//		return bdao.readAllBooks();
//	}

//	@RequestMapping(value="/readAllAuthors", method = RequestMethod.GET, produces="application/json")
//	public List<Author> readAuthors() throws SQLException {
//		return adao.readAuthors(null);
//	}
//
//	@RequestMapping(value="/readAllPublishers", method = RequestMethod.GET, produces="application/json")
//	public List<Publisher> readPublishers() throws SQLException {
//		return pdao.readPublishers(null);
//	}
//
//	@RequestMapping(value="/readAllGenres", method = RequestMethod.GET, produces="application/json")
//	public List<Genre> readGenres() throws SQLException {
//		return gdao.readGenres(null);
//	}
//
//	@RequestMapping(value="/readAllBranches", method = RequestMethod.GET, produces="application/json")
//	public List<Library_Branch> readBranches() throws SQLException {
//		return lbdao.readBranch(null);
//	}
	
	@RequestMapping(value="/Branch", method = RequestMethod.POST, consumes="application/json", produces="application/json")
	public Integer saveLibraryBranch(@RequestBody Library_Branch libraryBranch) throws SQLException {
		Integer id = 0;
		if (libraryBranch.getBranchId() != null) {
			lbdao.updateBranch(libraryBranch);
		} else {
			id = lbdao.saveLibraryBranchWithID(libraryBranch);
		}
		return id;
	}

	@RequestMapping(value="/BookCopies/{branchId}/{bookId}", method = RequestMethod.GET, produces="application/json")
	public BookCopies getBookCopies(@PathVariable Integer branchId, @PathVariable Integer bookId) throws SQLException {
		BookCopies temp = bcdao.getBookCopies(bookId, branchId);
		return temp;
	}

	@RequestMapping(value="/BookCopies", method = RequestMethod.POST, consumes="application/json")
	public void saveBookCopies(@RequestBody BookCopies bookCopies) throws SQLException {
		bcdao.saveBC(bookCopies);
	}

	@RequestMapping(value="/Branch/{branchId}", method = RequestMethod.DELETE, consumes="application/json")
	public void deleteLibraryBranch(@PathVariable Integer branchId) throws SQLException {
		lbdao.deleteLibraryBranch(branchId);
	}

//	@RequestMapping(value="/readBranch/{searchString}", method = RequestMethod.GET, produces="application/json")
//	public List<Library_Branch> readLibraryBranch(@PathVariable String searchString) throws SQLException {
//		return lbdao.readBranch(searchString);
//	}

	@RequestMapping(value="/Publisher", method = RequestMethod.POST, consumes="application/json")
	public void savePublisher(@RequestBody Publisher publisher) throws SQLException {
		if (publisher.getPublisherId() != null) {
			pdao.updatePublisher(publisher);
		} else {
			pdao.savePublisher(publisher);
		}
	}

	@RequestMapping(value="/Publisher/{publisher}", method = RequestMethod.DELETE, consumes="application/json")
	public void deletePublisher(@PathVariable Integer publisherId) throws SQLException {
		pdao.deletePublisher(publisherId);
	}

//	@RequestMapping(value="/readPublisher/{searchString}", method = RequestMethod.GET, produces="application/json")
//	public List<Publisher> readPublisher(@PathVariable String searchString) throws SQLException {
//		return pdao.readPublishers(searchString);
//	}

	@RequestMapping(value="/Genre", method = RequestMethod.POST, consumes="application/json")
	public void saveGenre(@RequestBody Genre genre) throws SQLException {
		if (genre.getGenreId() != null) {
			gdao.updateGenre(genre);
			gdao.deleteBookGenre(genre);
			gdao.saveBookGenre(genre);
		} else {
			int id = gdao.saveGenreWithID(genre);
			genre.setGenreId(id);
			gdao.saveBookGenre(genre);
		}
	}

	@RequestMapping(value="/Genre/{genreId}", method = RequestMethod.DELETE, consumes="application/json")
	public void deleteGenre(@PathVariable Integer genreId) throws SQLException {
		gdao.deleteGenre(genreId);
	}

//	@RequestMapping(value="/readGenre/{searchString}", method = RequestMethod.GET, produces="application/json")
//	public List<Genre> readGenre(@PathVariable String searchString) throws SQLException {
//		return gdao.readGenres(searchString);
//	}

	@RequestMapping(value="/Borrower", method = RequestMethod.POST, consumes="application/json")
	public void saveBorrower(@RequestBody Borrower borrower) throws SQLException {
		if (borrower.getCardNo() != null) {
			brdao.updateBorrower(borrower);
		} else {
			brdao.saveBorrower(borrower);
		}
	}

	@RequestMapping(value="/Borrower/{cardNo}", method = RequestMethod.DELETE, consumes="application/json")
	public void deleteBorrower(@PathVariable Integer cardNo) throws SQLException {
		brdao.deleteBorrower(cardNo);
	}

//	@RequestMapping(value="/readBorrower/{searchString}", method = RequestMethod.GET, produces="application/json")
//	public List<Borrower> readBorrower(@PathVariable String searchString) throws SQLException {
//		return brdao.readBorrowers(searchString);
//	}

	@RequestMapping(value="/BookLoans", method = RequestMethod.POST, consumes="application/json")
	public void saveBookLoans(@RequestBody BookLoans bookLoans) throws SQLException {
		if (bookLoans.getDateIn() != null) {
			bldao.saveBookLoanComplete(bookLoans);
		} else {
			bldao.saveBookLoan(bookLoans.getBranchId(), bookLoans.getCardNo(), bookLoans.getBookId());
		}
	}

	@RequestMapping(value="/BookLoans/{branchId}/{cardNo}/{bookId}", method = RequestMethod.DELETE, consumes="application/json")
	public void deleteBookLoans(@PathVariable Integer branchId, @PathVariable Integer cardNo, @PathVariable Integer bookId) throws SQLException {
		bldao.deleteBookLoan(branchId, cardNo, bookId);
	}

	@RequestMapping(value="/BookLoans", method = RequestMethod.PUT, consumes="application/json")
	public void overrideBookLoanDueDate(@RequestBody BookLoans bookLoans) throws SQLException {
		bldao.overrideDueDate(bookLoans);
	}

//	@RequestMapping(value="/updateBookLoanDueDate", method = RequestMethod.POST, consumes="application/json")
//	public void updateBookLoanDueDate(@RequestBody BookLoans bookLoans) throws SQLException {
//		bldao.updateBookLoanDueDate(bookLoans);
//	}

}
