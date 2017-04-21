package de.codecentric.mule.assertobjectequals;

public enum PathOption {

    /**
     * The actual value entry set of maps can contain more values than the expected set. So you tests do not fail when
     * there are more elements than expected in the result
     */
    CONTAINS_ONLY_ON_MAPS,

    /**
     * The order of map entries is checked. The default is to ignore order of map entries.
     */
    CHECK_MAP_ORDER,

    /**
     * The actual node and its subtree is ignored completely.
     */
    IGNORE
}
