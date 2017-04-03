package de.codecentric.mule.assertobjectequals;

public enum XmlCompareOption {
    /**
     * Will remove all comment-Tags "&lt;!-- Comment --&gt;" from test- and
     * control-XML before comparing.
     */
    IGNORE_COMMENTS,

    /**
     * Ignore whitespace by removing all empty text nodes and trimming the
     * non-empty ones.
     */
    IGNORE_WHITESPACE,

    /**
     * Normalize Text-Elements by removing all empty text nodes and normalizing
     * the non-empty ones.
     */
    NORMALIZE_WHITESPACE
}
