package org.alfresco.bean;

import java.util.List;

public class LibraryInput {

	private Integer id;
	private Integer booksCount;
	private Integer signupDays;
	private Integer shipBooksCount;
	/** The books ordered with the most valuable first. */
	private List<Integer> booksInLibrary;
	/** Global value based on number of books and scoring. */
	private Double value;


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

	/** The books ordered with the most valuable first. */
	public List<Integer> getBooksInLibrary() {
		return booksInLibrary;
	}

	public void setBooksInLibrary(List<Integer> booksInLibrary) {
		this.booksInLibrary = booksInLibrary;
	}

	/**
	 * Try to remove the selected book from the library.
	 *
	 * If it's not there then silently ignore the request.
	 */
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

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
