package org.alfresco.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.alfresco.bean.Input;
import org.alfresco.bean.LibraryInput;
import org.alfresco.bean.LibraryOutput;
import org.alfresco.bean.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleEngine {

	public enum Strategy {
		SHORTER_SIGNUPDAYS_MORE_BOOKS, 
		MORE_VALUABLE_LIBRARY_FIRST
	}

	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEngine.class);

	public Output run(Input in, Strategy strategy) {

		List<Integer> libsStarted = new ArrayList<>();
		List<Integer> libsUnstarted = IntStream.range(0, in.getLibraries().size()).boxed().collect(Collectors.toList());
		Output out = new Output();
		Integer onBoarding = null;
		int onBoardingDays = 0;

		for (int day = 0; day < in.getDaysForScanning(); day++) {
			if (onBoardingDays == 0) {
				onBoarding = pickLibrary(in, libsUnstarted, strategy, in.getDaysForScanning() - day);
				if (onBoarding != null) {
					onBoardingDays = in.getLibraries().get(onBoarding).getSignupDays();
					libsUnstarted.remove(onBoarding);
				}
			} else {
				onBoardingDays--;
			}
			if (onBoardingDays == 0) {
				libsStarted.add(onBoarding);
				LibraryOutput libraryOutput = new LibraryOutput();
				libraryOutput.setNumber(onBoarding);
				out.getLibsShipping().put(onBoarding, libraryOutput);
				onBoarding = null;
			}
			doDay(in, out, libsStarted);
			LOGGER.warn("Done day {}, libsStarted {}", day, libsStarted.size());
		}

		return out;
	}

	/**
	 * Pick the next library to sign up.
	 *
	 * @param in         The input object.
	 * @param libraryIds All libraries not yet signed up.
	 * @param daysLeft Number of days for scannning available
	 * @return The selected library (or null if none suitable).
	 */
	private Integer pickLibrary(Input in, List<Integer> libraryIds, Strategy strategy, Integer daysLeft) {
		LOGGER.debug("Picking from {} with strategy {}", libraryIds, strategy);
		switch (strategy) {
			case SHORTER_SIGNUPDAYS_MORE_BOOKS:
				{
					Integer libId = libraryIds.stream().map(id -> in.getLibraries().get(id))
							.filter(library -> hasNewBooks(library.getBooksInLibrary()))
							.sorted(Comparator.comparingInt(LibraryInput::getSignupDays)).map(LibraryInput::getId).findFirst()
							.orElse(null);
					if (libId == null) {
						return null;
					}
					int bestSignupDays = in.getLibraries().get(libId).getSignupDays();
					return libraryIds.stream().map(id -> in.getLibraries().get(id))
							.filter(library -> hasNewBooks(library.getBooksInLibrary()))
							.filter(library -> library.getSignupDays() == bestSignupDays)
							.sorted((a, b) -> b.getBooksInLibrary().size() - a.getBooksInLibrary().size())
							.map(LibraryInput::getId).findFirst().orElse(null);
				}
			case MORE_VALUABLE_LIBRARY_FIRST:
				{
					// Re-evaluate Library value now that some books has been shipped
					in.getLibraries().stream().forEach(library -> library.setValue(getLibraryValue(library, in.getBookScores(), daysLeft)));
					
					return libraryIds.stream().map(id -> in.getLibraries().get(id))
					  .filter(library -> hasNewBooks(library.getBooksInLibrary()))
				      .max(Comparator.comparing(LibraryInput::getValue))
				      .map(LibraryInput::getId)
				      .orElse(null);
				}
			default:
				return null;
		}
	}

	private boolean hasNewBooks(List<Integer> booksInLibrary) {
		// Determine if this library has any unscanned books.
		return !booksInLibrary.isEmpty();
	}

	/**
	 * Pick some books to send for scanning from each library that has been
	 * onboarded.
	 *
	 * @param in          The input object.
	 * @param out         The output object.
	 * @param libsStarted The onboarded libraries.
	 */
	private void doDay(Input in, Output out, List<Integer> libsStarted) {
		
		List<Integer> emptyLibraries = new ArrayList<>();
		List<Integer> runningLibraries = new ArrayList<>();
		runningLibraries.addAll(libsStarted);
		
		while (runningLibraries.size() > 0)
		{
			// Get Library giving highest score on each book scanning selection
			Integer libraryId = maxValueLibrary(in, libsStarted);
			if (libraryId == null)
			{
				libraryId = runningLibraries.get(0);
			}
			
			LibraryInput library = in.getLibraries().get(libraryId);
			List<Integer> booksInLibrary = library.getBooksInLibrary();
			List<Integer> booksSelected = new ArrayList<>();
			for (int i = 0; i < library.getShipBooksCount(); i++) {
				Integer bookSelected = getBook(booksInLibrary);
				if (bookSelected == null) {
					emptyLibraries.add(libraryId);
					break;
				}
				booksSelected.add(bookSelected);
				removeBookFromAllLibraries(in, bookSelected);
			}
			// Add the books to the output.
			out.getLibsShipping().get(libraryId).getBooksForScanning().addAll(booksSelected);
			
			runningLibraries.remove(libraryId);
			
		}
		
		// Skip libs with no books for next iterations
		libsStarted.removeAll(emptyLibraries);
		
	}
	
	/**
	 * Get library obtaining the max value in the day
	 * 
	 * @param in The input object.
	 * @param libsStarted The running libraries
	 * @return Id of the library with max value in the day
	 */
	private Integer maxValueLibrary(Input in, List<Integer> libsStarted)
	{
		Integer maxValue = 0;
		Integer libIdMaxValue = null;
		for (Integer libraryId : libsStarted) {
			LibraryInput library = in.getLibraries().get(libraryId);
			List<Integer> booksInLibrary = library.getBooksInLibrary();
			List<Integer> booksSelected = new ArrayList<>();
			for (int i = 0; i < library.getShipBooksCount(); i++) {
				Integer bookSelected = getBook(booksInLibrary);
				if (bookSelected == null) {
					break;
				}
				booksSelected.add(bookSelected);
			}
			Integer libValue = 0;
			for (Integer book : booksSelected)
			{
				libValue = libValue + (in.getBookScores()[book]);
			}
			if (libValue > maxValue)
			{
				maxValue = libValue;
				libIdMaxValue = libraryId;
			}
		}
		return libIdMaxValue;
	}

	private void removeBookFromAllLibraries(Input in, Integer bookSelected) {
		in.getLibraries().stream().forEach(library -> library.removeBookFromLibrary(bookSelected));
	}

	private Integer getBook(List<Integer> booksInLibrary) {
		if (booksInLibrary.isEmpty()) {
			return null;
		}
		return booksInLibrary.get(0);
	}

	/**
	 * Calculating the value of a library based in books scoring and amount of days to deliver the score
	 * @param library Input Library
	 * @param bookScores Array with book scores by position
	 * @param daysForScanning Number of days left form scanning
	 * @return Calculated value for the library
	 */
	public static Double getLibraryValue(LibraryInput library, int[] bookScores, Integer daysForScanning)
	{
		Long value = 0l;
		for (Integer bookInLibrary : library.getBooksInLibrary())
		{
			value = value + bookScores[bookInLibrary];
		}
		Double daysToDeliver = library.getSignupDays() + (Double.valueOf(library.getBooksCount()) / Double.valueOf(library.getShipBooksCount()));
	    return value * (daysForScanning / daysToDeliver);
	}

}
