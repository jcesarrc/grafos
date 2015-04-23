
package co.edu.poli.medgraph.language;

import co.edu.poli.medgraph.util.SC;

public class Translation {
	
	private static String s;

	public static String translate(String key) {
		s = SC.getResourceMap(Translation.class).getString(key);
		if (s == null) {
			System.out.printf("No se encontró el texto para [[%s]]\n", key);
		}
		return s == null ? String.format("[[%s]]", key) : s;
	}
	
}
