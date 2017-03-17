package de.codecentric.mule.assertobjectequals;

public class ObjectCompareOptions {
	private boolean containsCheckOnly;
	private boolean mapOrderRelevant;

	public ObjectCompareOptions(boolean containsCheckOnly, boolean mapOrderRelevant) {
		this.containsCheckOnly = containsCheckOnly;
		this.mapOrderRelevant = mapOrderRelevant;
	}

	public void setContainsCheckOnly(boolean aContainsCheckOnly) {
		containsCheckOnly = aContainsCheckOnly;
	}

	public boolean isContainsCheckOnly() {
		return containsCheckOnly;
	}

	public void setMapOrderRelevant(boolean aMapOrderRelevant) {
		mapOrderRelevant = aMapOrderRelevant;
	}

	public boolean isMapOrderRelevant() {
		return mapOrderRelevant;
	}
}
