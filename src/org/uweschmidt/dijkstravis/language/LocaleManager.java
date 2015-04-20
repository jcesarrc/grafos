/*
 * Copyright (C) 2008, Uwe Schmidt 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the 
 * Software is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software. 
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING 
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE. 
 */
package org.uweschmidt.dijkstravis.language;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class LocaleManager {

	private static final Set<LocaleChangeListener> listener = new HashSet<LocaleChangeListener>();

	private LocaleManager() {
	}

	public static void setLocale(Locale locale) {
		Locale.setDefault(locale);
		notifyListener();
	}
	
	public static Locale getLocale() {
		return Locale.getDefault();
	}

	private static void notifyListener() {
		for (final LocaleChangeListener l : listener) {
			l.localeChanged();
		}
	}

	public static void addLocaleChangeListener(final LocaleChangeListener l) {
		listener.add(l);
	}

	public static void removeLocaleChangeListener(final LocaleChangeListener l) {
		listener.remove(l);
	}

}
