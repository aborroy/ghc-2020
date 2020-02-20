package org.alfresco.main;

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
	
	// Calculate MAX Tags Count to provide a limit in pairing algorithm
	static Integer CURRENT_MAX_TAGS_COUNT = 0;

	// Obtain Input Bean from Input File
	public static Input getInput(File file) throws Exception {
		Integer libraryId = 0;
		Input input = new Input();
		// A library reference used when looping through the library definition lines.
		LibraryInput library = null;
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int lineCount = 0;
			for (String line; (line = br.readLine()) != null;) {
				int[] numbers = Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray();
				if (lineCount == 0)
				{
					input.setBooksCount(numbers[0]);
					input.setLibsCount(numbers[1]);
					input.setDaysForScanning(numbers[2]);
				}
				if (lineCount == 1)
				{
					input.setBookScores(numbers);
				}

				if (lineCount >= 2)
				{
					// Library
					if (lineCount % 2 == 0) 
					{
						library = new LibraryInput();
						library.setId(libraryId);
						LOGGER.warn("Library {}: {}", libraryId, numbers);
						libraryId++;
						library.setBooksCount(numbers[0]);
						library.setSignupDays(numbers[1]);
						library.setShipBooksCount(numbers[2]);
					}
					// Books
					if (lineCount % 2 == 1)
					{
						library.setBooksInLibrary(numbers);
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

	// Write Output File from Output Bean
	public static void writeOutput(Output output, File outFile) throws Exception {
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile)))) {
			writer.write("" + output.getLibsShipping().size() + "\n");
			if (output.getLibsShipping() != null) {
				// Nb. The libsShipping map is sorted by insertion order.
				for (LibraryOutput library : output.getLibsShipping().values()) {
					String lineString = library.getNumber() + " " + library.getBooksForScanning().size();
				    writer.write(lineString + "\n");
				    lineString = Arrays.toString(library.getBooksForScanning().toArray()).replaceAll(",", "");
				    writer.write(lineString.substring(1, lineString.length() - 1) + "\n");
				}
			}
		}
	}

}