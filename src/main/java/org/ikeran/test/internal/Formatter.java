package org.ikeran.test.internal;

import java.lang.reflect.Array;
import java.util.Map;

/**
 *
 * @author dhasenan
 */
public class Formatter {

	public static String format(Object o) {
		if (o == null) {
			return "null";
		}

		if (o.getClass().isArray()) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			boolean needsComma = false;
			for (int i = 0; i < Array.getLength(o); i++) {
				if (needsComma) {
					sb.append(", ");
				}
				needsComma = true;
				Object p = Array.get(o, i);
				sb.append(format(p));
			}
			sb.append("]");
			return sb.toString();
		}

		if (Iterable.class.isAssignableFrom(o.getClass())) {
			Iterable i = (Iterable) o;
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			boolean needsComma = false;
			for (Object p : i) {
				if (needsComma) {
					sb.append(", ");
				}
				needsComma = true;
				sb.append(format(p));
			}
			sb.append("]");
			return sb.toString();
		}

		if (Map.Entry.class.isAssignableFrom(o.getClass())) {
			Map.Entry entry = (Map.Entry) o;
			return String.format("%s => %s", format(entry.getKey()), format(entry.getValue()));
		}

		return o.toString();
	}

}
