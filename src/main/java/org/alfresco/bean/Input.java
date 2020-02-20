package org.alfresco.bean;

import java.util.ArrayList;
import java.util.List;

public class Input {

	private Integer booksCount;
	private Integer libsCount;
	private Integer daysForScanning;
	
	private int[] bookScores;
	
	private List<LibraryInput> libraries;

	public Integer getBooksCount() {
		return booksCount;
	}

	public void setBooksCount(Integer booksCount) {
		this.booksCount = booksCount;
	}

	public Integer getLibsCount() {
		return libsCount;
	}

	public void setLibsCount(Integer libsCount) {
		this.libsCount = libsCount;
	}

	public Integer getDaysForScanning() {
		return daysForScanning;
	}

	public void setDaysForScanning(Integer daysForScanning) {
		this.daysForScanning = daysForScanning;
	}

	public int[] getBookScores() {
		return bookScores;
	}

	public void setBookScores(int[] bookScores) {
		this.bookScores = bookScores;
	}

	public List<LibraryInput> getLibraries() {
		if (libraries == null)
		{
			libraries = new ArrayList<>(); 
		}
		return libraries;
	}

	public void setLibraries(List<LibraryInput> libraries) {
		this.libraries = libraries;
	}

	
}
