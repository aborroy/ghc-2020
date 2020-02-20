package org.alfresco.bean;

import java.util.ArrayList;
import java.util.List;

public class LibraryOutput {
	/** The id of the library. */
	private int number;
	/** The list of books to be scanned. */
	private List<Integer> booksForScanning = new ArrayList<>();

	/** The id of the library. */
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

	/** The list of books to be scanned. */
	public List<Integer> getBooksForScanning() {
		return booksForScanning;
	}
	public void setBooksForScanning(List<Integer> booksForScanning) {
		this.booksForScanning = booksForScanning;
	}

}
