package org.alfresco.bean;

import java.util.LinkedHashMap;

public class Output {
	/** Ordered map from library id to the corresponding library output object. */
	LinkedHashMap<Integer, LibraryOutput> libsShipping = new LinkedHashMap<>();

	/** Ordered map from library id to the corresponding library output object. */
	public LinkedHashMap<Integer, LibraryOutput> getLibsShipping() {
		return libsShipping;
	}

	public void setLibsShipping(LinkedHashMap<Integer, LibraryOutput> libsShipping) {
		this.libsShipping = libsShipping;
	}


}
