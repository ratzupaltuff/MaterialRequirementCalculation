package mareca.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mareca.UnexpectedInputException;
import mareca.model.AssemblyCache;
import mareca.model.AssemblyMember;
import mareca.model.AssemblyMemberCountTupel;
import mareca.model.Element;
import edu.kit.informatik.Terminal;

/**
 * @author ratzupaltuff
 */
public enum Commands {
    /**
     * 
     */
    ADD_ASSEMBLY("addAssembly" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_MEMBER_DECLARATION.toString()) {
        @Override
        public void execute(String commandString, AssemblyCache knownAssemblies) throws UnexpectedInputException {
            String dataString = commandString
                    .split(UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString())[1];
            String nameOfAssemblyToAdd = dataString
                    .split(UserInteractionStrings.REGEX_INITIALIZATION_CHARACTER.toString())[0];
            AssemblyMemberCountTupel[] assemblyMemberCountTupelArray = getAssemblyMemberArray(dataString
                    .split(UserInteractionStrings.REGEX_INITIALIZATION_CHARACTER.toString())[1]);
        }
    };

    private static boolean shouldQuit = false;
    private String matchingString;

    private Commands(String matchingString) {
        this.matchingString = matchingString;
    }

    /**
     * execute the right command which is dermined by the command string
     * 
     * @param commandString   string to parse/with which the right command should be
     *                        determined
     * @param knownAssemblies assembly cache as a tiny database
     * @return the right command to user for this commandString
     * @throws UnexpectedInputException if the command is not a valid command
     */
    public static Commands executeRightCommand(String commandString, AssemblyCache knownAssemblies)
            throws UnexpectedInputException {
        for (Commands commandToCheck : Commands.values()) {
            // I decided not only to check if the first word is right and to check
            // afterwards whether the rest is correct, because it is a lot additionally work
            // and I think it is not worth it, the user doesn't need 100% accurate error
            // responses, I assume that he knows which commands are available
            Matcher matcher = Pattern.compile(commandToCheck.getMatchingString()).matcher(commandString);
            if (matcher.matches()) {
                return commandToCheck;
            }
        }
        throw new UnexpectedInputException("the command entered is not in the list of valid commands");
    }

    private String getMatchingString() {
        return matchingString;
    }

    /**
     * define that every command has to declare a method, which describes what to do
     * when this command is called
     * 
     * @param commandString   string which is inputted by the user
     * @param knownAssemblies assembly cache to interact with
     * @throws UnexpectedInputException when there is an unexpected input from the
     *                                  user
     */
    public abstract void execute(String commandString, AssemblyCache knownAssemblies) throws UnexpectedInputException;

    /**
     * method used by the main method, to determine whether to quit or not
     * 
     * @return returns whether to quit or not
     */
    public static boolean shouldDoQuit() {
        return shouldQuit;
    }

    private static AssemblyMemberCountTupel[] getAssemblyMemberArray(String initalizationString) {

        String[] assemblyMemberStringArray = initalizationString
                .split(UserInteractionStrings.REGEX_COORDINATE_OUTER_SEPERATOR.toString());
        AssemblyMemberCountTupel[] assemblyMemberCountTupelArray 
        = new AssemblyMemberCountTupel[assemblyMemberStringArray.length];

        for (int matchNumber = 0; matchNumber < assemblyMemberStringArray.length; matchNumber++) {
            assemblyMemberCountTupelArray[matchNumber] = getAssemblyMemberCountTupel(
                    assemblyMemberStringArray[matchNumber]);
        }
        return assemblyMemberCountTupelArray;
    }

    private static AssemblyMemberCountTupel getAssemblyMemberCountTupel(String assemblyMemberString) {
        String[] assemblyMemberProperties = assemblyMemberString
                .split(UserInteractionStrings.REGEX_COORDINATE_INNER_SEPERATOR.toString());
        int count = Integer.parseInt(assemblyMemberProperties[0]);
        String nameString = assemblyMemberProperties[1];
        return new AssemblyMemberCountTupel(new Element(nameString), count);
    }
}
