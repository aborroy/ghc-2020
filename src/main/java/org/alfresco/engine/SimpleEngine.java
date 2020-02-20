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
	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEngine.class);

	public Output run(Input in) {

		List<Integer> libsStarted = new ArrayList<>();
		List<Integer> libsUnstarted = IntStream.range(0, in.getLibraries().size()).boxed()
				.collect(Collectors.toList());
		Output out = new Output();
		Integer onBoarding = null;
		int onBoardingDays = 0;

		for (int day = 0; day < in.getDaysForScanning(); day++) {
			if (onBoardingDays == 0) {
				onBoarding = pickLibrary(in, libsUnstarted);
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
		}

		return out;
	}

	/**
	 * Pick the next library to sign up.
	 *
	 * @param in The input object.
	 * @param libraryIds All libraries not yet signed up.
	 * @return The selected library (or null if none suitable).
	 */
	private Integer pickLibrary(Input in, List<Integer> libraryIds) {
		LOGGER.warn("Picking from {}", libraryIds);
		return libraryIds.stream()
						 .map(id -> in.getLibraries().get(id))
						 .filter(library -> hasNewBooks(library.getBooksInLibrary()))
						 .sorted(Comparator.comparingInt(LibraryInput::getSignupDays))
						 .map(LibraryInput::getId)
						 .findFirst().orElse(null);
	}

	private boolean hasNewBooks(int[] booksInLibrary)
	{
		// TODO Determine if this library has any unscanned books.
		return true;
	}

	/**
	 * Pick some books to send for scanning from each library that has been onboarded.
	 *
	 * @param in The input object.
	 * @param out The output object.
	 * @param libsStarted The onboarded libraries.
	 */
	private void doDay(Input in, Output out, List<Integer> libsStarted)
	{
		for (Integer libraryId : libsStarted)
		{
			LibraryInput library = in.getLibraries().get(libraryId);
			int[] booksInLibrary = library.getBooksInLibrary();
			List<Integer> booksSelected = new ArrayList<>();
			for (int i = 0; i < library.getShipBooksCount(); i++) {
				Integer bookSelected = getMaxBook(in.getBookScores(), booksInLibrary);
				if (bookSelected == null) {
					break;
				}
				booksSelected.add(bookSelected);
				library.setBooksInLibrary(removeElement(booksInLibrary, bookSelected));
			}
			// TODO Add the books to the output

		}
	}

	private Integer getMaxBook(int[] bookScores, int[] booksInLibrary) {
		return booksInLibrary[0];
	}

	public static int[] removeElement(int[] arr, int index) {
		if (arr == null || index < 0 || index >= arr.length) {
			return arr;
		}
		return IntStream.range(0, arr.length).filter(i -> i != index).map(i -> arr[i]).toArray();
	}

}
