
package co.edu.poli.medgraph.util;

public class Texts {
	
	private static String s;

	public static String read(String key) {
		s = SC.getResourceMap(Texts.class).getString(key);
		if (s == null) {
			System.out.printf("No se encontró el texto para [[%s]]\n", key);
		}
		return s == null ? String.format("[[%s]]", key) : s;
	}
	
}
