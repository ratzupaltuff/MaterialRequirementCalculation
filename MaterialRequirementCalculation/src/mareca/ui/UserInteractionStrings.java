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
     * 
     */
    REGEX_NAME_OF_ASSEMBLY_MEMBER("[a-zA-Z]+"),

    /**
     * 
     */
    REGEX_DEFINITION_CHARACTER("="),

    /**
     * string that is the separator between name of assemblymember and quantity
     */
    REGEX_COORDINATE_INNER_SEPERATOR(":"),
    /**
     * string that is the separator between two assemblymember-quantity pairs
     */
    REGEX_COORDINATE_OUTER_SEPERATOR(";"),

    /**
     * 
     */
    REGEX_NUMBER("(1000|[1-9]\\d{0,2})"),

    /**
     * 
     */
    REGEX_ASSEMBLY_MEMBER_QUANTITY_PAIR(REGEX_NUMBER.toString() + REGEX_COORDINATE_INNER_SEPERATOR.toString()
            + REGEX_NAME_OF_ASSEMBLY_MEMBER.toString()),

    /**
     * 
     */
    REGEX_MULTIPLE_PAIRS("(" + REGEX_ASSEMBLY_MEMBER_QUANTITY_PAIR.toString() + ")" + "(;"
            + REGEX_ASSEMBLY_MEMBER_QUANTITY_PAIR.toString() + ")*"),

    /**
     * 
     */
    REGEX_INITIALIZATION_CHARACTER("="),
    
    /**
     * 
     */
    REGEX_ADDITION_CHARACTER("\\+"),

    /**
     * 
     */
    REGEX_MEMBER_DECLARATION(REGEX_NAME_OF_ASSEMBLY_MEMBER.toString() + REGEX_INITIALIZATION_CHARACTER.toString()
            + REGEX_MULTIPLE_PAIRS.toString()),
    
    /**
     * 
     */
    REGEX_MEMBER_ADDITION(REGEX_NAME_OF_ASSEMBLY_MEMBER.toString() + REGEX_ADDITION_CHARACTER.toString()
    + REGEX_ASSEMBLY_MEMBER_QUANTITY_PAIR.toString()),
    
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
