package mareca.ui;

/**
 * @author ratzupaltuff class which holds all strings and "magic numbers" for
 *         easier change, especially if you translate the program. I could have
 *         moved the strings of the exceptions here, but I decided to do not,
 *         because you can debug much easier, if you can search the program for
 *         the error occurred, and that is more important than a translation
 */
public enum UserInteractionStrings {

    /**
     * String which is the sign of acceptance of an input
     */
    OK_STRING("OK"),

    /**
     * string that is the separator between x and y of a coordinate
     */
    REGEX_COORDINATE_INNER_SEPERATOR(";"),
    /**
     * string that is the separator between two pairs of coordinates
     */
    REGEX_COORDINATE_OUTER_SEPERATOR(":"),
    /**
     * which character should separate the command and the parameters, default is
     * space
     */
    REGEX_COMMAND_PARAMETER_SEPERATOR(" ");

    private final String userOutput;

    private UserInteractionStrings(String userOutput) {
        this.userOutput = userOutput;
    }

    @Override
    public String toString() {
        return userOutput;
    }

}
