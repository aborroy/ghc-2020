package org.alfresco.engine;

import java.util.ArrayList;
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
				onBoarding = pickLibrary(in, libsUnstarted, strategy);
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
	 * @return The selected library (or null if none suitable).
	 */
	private Integer pickLibrary(Input in, List<Integer> libraryIds, Strategy strategy) {
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
		for (Integer libraryId : libsStarted) {
			LibraryInput library = in.getLibraries().get(libraryId);
			List<Integer> booksInLibrary = library.getBooksInLibrary();
			List<Integer> booksSelected = new ArrayList<>();
			for (int i = 0; i < library.getShipBooksCount(); i++) {
				Integer bookSelected = getBook(in.getBookScores(), booksInLibrary);
				if (bookSelected == null) {
					break;
				}
				booksSelected.add(bookSelected);
				removeBookFromAllLibraries(in, bookSelected);
			}
			// Add the books to the output.
			out.getLibsShipping().get(libraryId).getBooksForScanning().addAll(booksSelected);
		}
	}

	private void removeBookFromAllLibraries(Input in, Integer bookSelected) {
		in.getLibraries().stream().forEach(library -> library.removeBookFromLibrary(bookSelected));
	}

	private Integer getBook(int[] bookScores, List<Integer> booksInLibrary) {
		if (booksInLibrary.isEmpty()) {
			return null;
		}
		return booksInLibrary.get(0);
	}

}
