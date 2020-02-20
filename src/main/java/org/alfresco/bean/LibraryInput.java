package org.alfresco.bean;

import org.alfresco.engine.SimpleEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryInput {
	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEngine.class);

	private Integer id;
	private Integer booksCount;
	private Integer signupDays;
	private Integer shipBooksCount;

	private int[] booksInLibrary;


	public Integer getBooksCount() {
		return booksCount;
	}

	public void setBooksCount(Integer booksCount) {
		this.booksCount = booksCount;
	}

	public Integer getSignupDays() {
		LOGGER.warn("SignupDays: {}" , signupDays);
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

	public int[] getBooksInLibrary() {
		return booksInLibrary;
	}

	public void setBooksInLibrary(int[] booksInLibrary) {
		this.booksInLibrary = booksInLibrary;
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
