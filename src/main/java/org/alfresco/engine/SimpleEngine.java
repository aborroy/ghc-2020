package org.alfresco.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.alfresco.bean.Input;
import org.alfresco.bean.LibraryInput;
import org.alfresco.bean.Output;

public class SimpleEngine {

	public Output run(Input in) {

		List<Integer> libsStarted = new ArrayList<>();
		List<Integer> libsUnstarted = IntStream.rangeClosed(0, in.getLibraries().size()).boxed()
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
				onBoarding = null;
			}
			doDay(in, libsStarted);
		}

		return out;
	}

	/**
	 * Pick the next library to sign up.
	 *
	 * @param in The input
	 * @param libraryIds All libraries not yet signed up.
	 * @return The selected library (or null if none suitable).
	 */
	private Integer pickLibrary(Input in, List<Integer> libraryIds) {
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

	private void doDay(Input in, List<Integer> libsStarted)
	{
		for (Integer lib : libsStarted)
		{
			int[] booksInLibrary = in.getLibraries().get(lib).getBooksInLibrary();
			Integer bookSelected = getMaxBook(in.getBookScores(), booksInLibrary);
			in.getLibraries().get(lib).setBooksInLibrary(removeElement(booksInLibrary, bookSelected));
			
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
