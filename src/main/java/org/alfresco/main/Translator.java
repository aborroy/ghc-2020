package org.alfresco.main;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.alfresco.bean.Input;
import org.alfresco.bean.LibraryInput;
import org.alfresco.bean.LibraryOutput;
import org.alfresco.bean.Output;
import org.alfresco.engine.SimpleEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translate files into beans and reverse.
 */
public class Translator {

	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEngine.class);

	/**
	 * Get Input Bean from input file
	 * 
	 * @param file Input file from statement
	 * @return Input Bean with the values from the input file
	 * @throws Exception When reading the file fails
	 */
	public static Input getInput(File file) throws Exception {
		Integer libraryId = 0;
		Input input = new Input();
		// A library reference used when looping through the library definition lines.
		LibraryInput library = null;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int lineCount = 0;
			for (String line; (line = br.readLine()) != null;) {
				int[] numbers = Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray();
				if (lineCount == 0) {
					input.setBooksCount(numbers[0]);
					input.setLibsCount(numbers[1]);
					input.setDaysForScanning(numbers[2]);
				}
				if (lineCount == 1) {
					input.setBookScores(numbers);
				}

				if (lineCount >= 2) {
					// Library
					if (lineCount % 2 == 0) {
						library = new LibraryInput();
						library.setId(libraryId);
						LOGGER.warn("Library {}: {}", libraryId, numbers);
						libraryId++;
						library.setBooksCount(numbers[0]);
						library.setSignupDays(numbers[1]);
						library.setShipBooksCount(numbers[2]);
					}
					// Books
					if (lineCount % 2 == 1) {
						List<Integer> books = Arrays.stream(numbers).boxed().collect(toList());
						books = sortBooksByScores(books, input.getBookScores());
						library.setBooksInLibrary(books);
						Double value = getValue(library, input);
						library.setValue(value);
						List<LibraryInput> libs = input.getLibraries();
						libs.add(library);
						input.setLibraries(libs);
					}
				}
				lineCount++;
			}
		}
		return input;
	}

	/**
	 * Get an ordered list of books for the library having higher scores first
	 * 
	 * @param books      List of books in a Library
	 * @param bookScores List of scores for books
	 * @return List of books using a high score first order
	 */
	private static List<Integer> sortBooksByScores(List<Integer> books, int[] bookScores) {
		return books.stream().sorted((a, b) -> bookScores[b] - bookScores[a]).collect(toList());
	}
	
	/**
	 * Calculating the value of a library based in books scoring and amount of days to deliver the score
	 * @param library Input Library
	 * @param input Input properties
	 * @return Calculated value for the library
	 */
	private static Double getValue(LibraryInput library, Input input)
	{
		Long value = 0l;
		for (Integer bookInLibrary : library.getBooksInLibrary())
		{
			value = value + input.getBookScores()[bookInLibrary];
		}
		Double daysToDeliver = library.getSignupDays() + (Double.valueOf(library.getBooksCount()) / Double.valueOf(library.getShipBooksCount()));
	    return value / daysToDeliver;
	}

	/**
	 * Get Output File from Output Bean
	 * 
	 * @param output  Output Bean with the values for the file
	 * @param outFile Output file path to write in
	 * @throws Exception When writing the file fails
	 */
	public static void writeOutput(Output output, File outFile) throws Exception {

		// First count any libraries that shipped no books.
		long nonShippingLibraries = output.getLibsShipping().values().stream()
				.filter(libraryOutput -> libraryOutput.getBooksForScanning().isEmpty()).count();

		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)))) {
			writer.write("" + (output.getLibsShipping().size() - nonShippingLibraries) + "\n");
			if (output.getLibsShipping() != null) {
				// Nb. The libsShipping map is sorted by insertion order.
				for (LibraryOutput library : output.getLibsShipping().values()) {
					if (!library.getBooksForScanning().isEmpty()) {
						String lineString = library.getNumber() + " " + library.getBooksForScanning().size();
						writer.write(lineString + "\n");
						lineString = Arrays.toString(library.getBooksForScanning().toArray()).replaceAll(",", "");
						writer.write(lineString.substring(1, lineString.length() - 1) + "\n");
					}
				}
			}
		}
	}

}