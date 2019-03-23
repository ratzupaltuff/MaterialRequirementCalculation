package mareca.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mareca.UnexpectedInputException;
import mareca.model.Assembly;
import mareca.model.AssemblyMember;
import mareca.model.AssemblyMemberCountTupel;
import edu.kit.informatik.Terminal;

/**
 * @author ratzupaltuff this class is dedicated to care about valid commands,
 *         and how they should be executed on the choosen model of the
 *         representation of the bill of material/material requirement
 *         calculation (mareca)
 */
public enum Commands {
    /**
     * add assemblies with defined submembers
     */
    ADD_ASSEMBLY("addAssembly" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_MEMBER_DECLARATION.toString()) {
        @Override
        public void execute(String commandString) throws UnexpectedInputException {
            String nameOfAssemblyToAdd = getCommandOptions(commandString)
                    .split(UserInteractionStrings.REGEX_INITIALIZATION_CHARACTER.toString())[0];
            AssemblyMemberCountTupel[] assemblyMemberCountTupelArray = getAssemblyMemberArray(
                    getCommandOptions(commandString)
                            .split(UserInteractionStrings.REGEX_INITIALIZATION_CHARACTER.toString())[1],
                    nameOfAssemblyToAdd);

            Assembly.createNewAssembly(nameOfAssemblyToAdd, assemblyMemberCountTupelArray);
            Terminal.printLine("OK"); 
        }
    },
    /**
     * remove an Assembly/convert it to an Element
     */
    REMOVE_ASSEMBLY("removeAssembly" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_NAME_OF_ASSEMBLY_MEMBER.toString()) {
        @Override
        public void execute(String commandString) throws UnexpectedInputException {
            String name = getCommandOptions(commandString);
            AssemblyMember assemblyMember = AssemblyMember.getAssemblyMember(name);
            if (assemblyMember.hasSubElements()) {
                Assembly assemblyToRemoveAssembly = (Assembly) assemblyMember;
                Assembly.replaceSubAssemblyWithElementRecursively(assemblyToRemoveAssembly);
            } else {
                throw new UnexpectedInputException(name + " is already an Element");
            }
            Terminal.printLine("OK");
        }
    },
    /**
     * print an Assembly with its direct contents sorted by name
     */
    PRINT_ASSEMBLY("printAssembly" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_NAME_OF_ASSEMBLY_MEMBER.toString()) {
        @Override
        public void execute(String commandString) throws UnexpectedInputException {
            AssemblyMember assemblyMember = AssemblyMember.getAssemblyMember(getCommandOptions(commandString));
            Terminal.printLine(assemblyMember.toString());
        }
    },

    /**
     * print all subAssemblies recursively sorted by count and then value
     */
    GET_ASSEMBLIES("getAssemblies" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_NAME_OF_ASSEMBLY_MEMBER.toString()) {
        @Override
        public void execute(String commandString) throws UnexpectedInputException {
            AssemblyMember assemblyMember = AssemblyMember.getAssemblyMember(getCommandOptions(commandString));
            if (assemblyMember.hasSubElements()) {
                Assembly assembly = (Assembly) assemblyMember;
                String output = assembly.getAssembliesString();
                Terminal.printLine(output);
            } else {
                throw new UnexpectedInputException(
                        "there is no Assembly with the name: " + getCommandOptions(commandString));
            }
        }
    },
    /**
     * method to print assembly contents, only shows elements sorted by their count,
     * then name
     */
    GET_COMPONENTS("getComponents" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_NAME_OF_ASSEMBLY_MEMBER.toString()) {
        @Override
        public void execute(String commandString) throws UnexpectedInputException {
            AssemblyMember assemblyMember = AssemblyMember.getAssemblyMember(getCommandOptions(commandString));
            if (assemblyMember.hasSubElements()) {
                Assembly assembly = (Assembly) assemblyMember;
                String output = assembly.getElementsString();
                Terminal.printLine(output);
            } else {
                throw new UnexpectedInputException(
                        "there is no Assembly with the name: " + getCommandOptions(commandString));
            }
        }
    },
    /**
     * add an element/assembly to an already existing assembly
     */
    ADD_PART("addPart" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_MEMBER_ADDITION.toString()) {
        @Override
        public void execute(String commandString) throws UnexpectedInputException {
            String[] commandContentStrings = getCommandOptions(commandString)
                    .split(UserInteractionStrings.REGEX_ADDITION_CHARACTER.toString());
            AssemblyMemberCountTupel tupelToAdd = getAssemblyMemberCountTupel(commandContentStrings[1]);
            if (tupelToAdd.getAssemblyMemberString().equals(commandContentStrings[0])) {
                throw new UnexpectedInputException("you cannot add an an Assembly to itself");
            }

            AssemblyMember assemblyMember = AssemblyMember.getAssemblyMember(commandContentStrings[0]);
            if (assemblyMember.hasSubElements()) {
                Assembly assembly = (Assembly) assemblyMember;
                assembly.addSubMember(tupelToAdd.getAssemblyMemberString(), tupelToAdd.getIntCount());
                Terminal.printLine("OK");
            } else {
                throw new UnexpectedInputException(assemblyMember.getName() + " is not an Assembly");
            }
        }
    },
    /**
     * remove an element/assembly from an assembly
     */
    REMOVE_PART("removePart" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_MEMBER_SUBSTRACTION.toString()) {
        @Override
        public void execute(String commandString) throws UnexpectedInputException {
            String[] commandContentStrings = getCommandOptions(commandString).split("-");
            AssemblyMemberCountTupel tupelToRemove = getAssemblyMemberCountTupel(commandContentStrings[1]);
            AssemblyMember assemblyMember = AssemblyMember.getAssemblyMember(commandContentStrings[0]);
            if (assemblyMember.hasSubElements()) {
                Assembly assembly = (Assembly) assemblyMember;
                assembly.removeSubAssemblyPartially(
                        AssemblyMember.getAssemblyMember(tupelToRemove.getAssemblyMemberString()),
                        tupelToRemove.getIntCount());
                Terminal.printLine("OK");
            } else {
                throw new UnexpectedInputException(tupelToRemove.getAssemblyMemberString() + " is not an Assembly");
            }
        }
    },
    /**
     * exit the application
     */
    QUIT("quit") {
        @Override
        public void execute(String commandString) {
            shouldQuit = true;
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
     * @param commandString string to parse/with which the right command should be
     *                      determined
     * @return the right command to use for this commandString
     * @throws UnexpectedInputException if the command is not a valid command
     */
    public static Commands executeRightCommand(String commandString) throws UnexpectedInputException {
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
     * @param commandString string which is inputted by the user
     * @throws UnexpectedInputException when there is an unexpected input from the
     *                                  user that cannot be handled
     */
    public abstract void execute(String commandString) throws UnexpectedInputException;

    /**
     * method used by the main method, to determine whether to quit or not
     * 
     * @return returns whether to quit or not
     */
    public static boolean shouldDoQuit() {
        return shouldQuit;
    }

    /**
     * method to create a string&value tupel array for all subAssemblyMembers to add
     * to the current Assembly, is needed because i cannot save these values in the
     * internal representation
     * 
     * @param initalizationString string to get the values from Format=
     *                            <value>:<name>;<value>:<name>;...
     * @param nameOfParent        name of the parent assembly to detect doubled
     *                            names which are not allowed
     * @return the representing assemblyMemberCountTupelArray
     * @throws UnexpectedInputException if there are doubled names
     */
    private static AssemblyMemberCountTupel[] getAssemblyMemberArray(String initalizationString, String nameOfParent)
            throws UnexpectedInputException {

        String[] assemblyMemberStringArray = initalizationString
                .split(UserInteractionStrings.REGEX_COORDINATE_OUTER_SEPERATOR.toString());

        checkForDoubleNames(assemblyMemberStringArray, nameOfParent);

        AssemblyMemberCountTupel[] assemblyMemberCountTupelArray 
        = new AssemblyMemberCountTupel[assemblyMemberStringArray.length];

        for (int matchNumber = 0; matchNumber < assemblyMemberStringArray.length; matchNumber++) {
            assemblyMemberCountTupelArray[matchNumber] = getAssemblyMemberCountTupel(
                    assemblyMemberStringArray[matchNumber]);
        }
        return assemblyMemberCountTupelArray;
    }

    /**
     * check if there are doubled names in the creation string, no optimizations
     * were made, because it is unlikely to add hundrets of subAssemblyMembers with
     * a text command like this
     * 
     * @param assemblyMemberStrings the assembly member strings, Format=
     *                              <value>:<name>;<value>:<name>;...
     * @param nameOfParent          name of the assembly to compare the strings
     *                              additionally
     * @throws UnexpectedInputException if an doubled entry is found
     */
    private static void checkForDoubleNames(String[] assemblyMemberStrings, String nameOfParent)
            throws UnexpectedInputException {
        // check for every entry
        for (int currentStringNr = 0; currentStringNr < assemblyMemberStrings.length; currentStringNr++) {
            String currentName = assemblyMemberStrings[currentStringNr]
                    .split(UserInteractionStrings.REGEX_COORDINATE_INNER_SEPERATOR.toString())[1];
            // if it matches with any other entry
            for (int numberOfStringBefore = 0; numberOfStringBefore < currentStringNr; numberOfStringBefore++) {
                String currentNameBefore = assemblyMemberStrings[numberOfStringBefore]
                        .split(UserInteractionStrings.REGEX_COORDINATE_INNER_SEPERATOR.toString())[1];
                if (currentName.equals(currentNameBefore)) {
                    throw new UnexpectedInputException("You have entered the name " + currentName + " at least twice");
                }
            }
            if (currentName.equals(nameOfParent)) {
                throw new UnexpectedInputException("the Assembly " + currentName + " cannot contain itself");
            }
        }
    }

    /**
     * get a name&value tupel out of a string
     * 
     * @param assemblyMemberString the string to convert Format= <value>:<name>
     * @return the corresponding name&value tupel
     */
    private static AssemblyMemberCountTupel getAssemblyMemberCountTupel(String assemblyMemberString) {
        String[] assemblyMemberProperties = assemblyMemberString
                .split(UserInteractionStrings.REGEX_COORDINATE_INNER_SEPERATOR.toString());
        int count = Integer.parseInt(assemblyMemberProperties[0]);
        String nameString = assemblyMemberProperties[1];
        return new AssemblyMemberCountTupel(nameString, count);
    }

    /**
     * @param commandString get the string after the first space, effectively
     *                      removes the current command and leaves the raw data
     * @return the data in this commandString
     */
    private static String getCommandOptions(String commandString) {
        String commandStringData = commandString
                .split(UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString())[1];
        return commandStringData;
    }
}
