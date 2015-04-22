
package co.edu.poli.medgraph.language;

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
