package org.alfresco.bean;

public class LibraryOutput {
	/** The id of the library. */
	private int number;
	/** The list of books to be scanned. */
	private int[] booksForScanning;

	/** The id of the library. */
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}

	/** The list of books to be scanned. */
	public int[] getBooksForScanning() {
		return booksForScanning;
	}
	public void setBooksForScanning(int[] booksForScanning) {
		this.booksForScanning = booksForScanning;
	}

}
