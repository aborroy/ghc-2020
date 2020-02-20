package org.alfresco.bean;

import java.util.List;

public class LibraryInput {

	private Integer id;
	private Integer booksCount;
	private Integer signupDays;
	private Integer shipBooksCount;

	private List<Integer> booksInLibrary;


	public Integer getBooksCount() {
		return booksCount;
	}

	public void setBooksCount(Integer booksCount) {
		this.booksCount = booksCount;
	}

	public Integer getSignupDays() {
		return signupDays;
	}

	public void setSignupDays(Integer signupDays) {
		this.signupDays = signupDays;
	}

	public Integer getShipBooksCount() {
		return shipBooksCount;
	}

	public void setShipBooksCount(Integer shipBooksCount) {
		this.shipBooksCount = shipBooksCount;
	}

	public List<Integer> getBooksInLibrary() {
		return booksInLibrary;
	}

	public void setBooksInLibrary(List<Integer> booksInLibrary) {
		this.booksInLibrary = booksInLibrary;
	}

	public void removeBookFromLibrary(Integer book) {
		booksInLibrary.remove(book);
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}
}
