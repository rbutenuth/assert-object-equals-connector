package de.codecentric.mule.assertobjectequals;

public interface ObjectCompareOptionsFactory {

	/**
	 * @param path A path
	 * @return Options controlling the comparison for a path.
	 */
	public ObjectCompareOptions createOptions(Path path);

}
