package org.alfresco.engine;

import java.util.Collection;
import java.util.Comparator;

import org.alfresco.bean.LibraryInput;

public class LibraryPicker {
    /**
     * Pick the next library to sign up.
     *
     * @param libraries All libraries not yet signed up.
     * @return The selected library (or null if none suitable).
     */
    public LibraryInput pickLibrary(Collection<LibraryInput> libraries) {
        return libraries.stream()
                        .filter(library -> hasNewBooks(library.getBooksInLibrary()))
                        .sorted(Comparator.comparingInt(LibraryInput::getSignupDays))
                        .findFirst().orElse(null);
    }

    private boolean hasNewBooks(int[] booksInLibrary) {
        // TODO Determine if this library has any unscanned books.
        return true;
    }
}
