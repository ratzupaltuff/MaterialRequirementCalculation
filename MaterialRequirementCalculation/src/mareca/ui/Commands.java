package mareca.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mareca.UnexpectedInputException;
import mareca.model.Assembly;
import mareca.model.AssemblyMember;
import mareca.model.AssemblyMemberCountTupel;
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
        public void execute(String commandString, Assembly knownAssemblies) throws UnexpectedInputException {
            String nameOfAssemblyToAdd = getCommandOptions(commandString)
                    .split(UserInteractionStrings.REGEX_INITIALIZATION_CHARACTER.toString())[0];
            AssemblyMemberCountTupel[] assemblyMemberCountTupelArray = getAssemblyMemberArray(
                    getCommandOptions(commandString)
                            .split(UserInteractionStrings.REGEX_INITIALIZATION_CHARACTER.toString())[1],
                    nameOfAssemblyToAdd);

            knownAssemblies.createNewSubAssembly(nameOfAssemblyToAdd, assemblyMemberCountTupelArray, 1);
            Terminal.printLine("OK");
        }
    },
    /**
     * 
     *
     */
    REMOVE_ASSEMBLY("removeAssembly" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_NAME_OF_ASSEMBLY_MEMBER.toString()) {
        @Override
        public void execute(String commandString, Assembly knownAssemblies) throws UnexpectedInputException {
            String name = getCommandOptions(commandString);
            AssemblyMember assemblyMember = AssemblyMember.getAssemblyMember(name);
            if (assemblyMember.hasSubElements()) {
                Assembly assemblyToRemoveAssembly = (Assembly) assemblyMember;
                knownAssemblies.replaceSubAssemblyWithElementRecursively(assemblyToRemoveAssembly);
            } else {
                throw new UnexpectedInputException(name + " is already an Element");
            }
            Terminal.printLine("OK");
        }
    },
    /**
     * 
     */
    PRINT_ASSEMBLY("printAssembly" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_NAME_OF_ASSEMBLY_MEMBER.toString()) {
        @Override
        public void execute(String commandString, Assembly knownAssemblies) throws UnexpectedInputException {
            AssemblyMember assemblyMember = AssemblyMember.getAssemblyMember(getCommandOptions(commandString));
            Terminal.printLine(assemblyMember.toString());
        }
    },

    /**
     *
     */
    GET_ASSEMBLIES("getAssemblies" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_NAME_OF_ASSEMBLY_MEMBER.toString()) {
        @Override
        public void execute(String commandString, Assembly knownAssemblies) throws UnexpectedInputException {
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
     * 
     */
    GET_COMPONENTS("getComponents" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_NAME_OF_ASSEMBLY_MEMBER.toString()) {
        @Override
        public void execute(String commandString, Assembly knownAssemblies) throws UnexpectedInputException {
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
     *
     */
    ADD_PART("addPart" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_MEMBER_ADDITION.toString()) {
        @Override
        public void execute(String commandString, Assembly knownAssemblies) throws UnexpectedInputException {
            String[] commandContentStrings = getCommandOptions(commandString)
                    .split(UserInteractionStrings.REGEX_ADDITION_CHARACTER.toString());
            AssemblyMemberCountTupel tupelToAdd = getAssemblyMemberCountTupel(commandContentStrings[1]);
            if (tupelToAdd.getAssemblyMemberString().equals(commandContentStrings[0])) {
                throw new UnexpectedInputException("you cannot add an an Assembly to itself");
            }
            AssemblyMember assemblyMember = AssemblyMember.getAssemblyMember(false, commandContentStrings[0]);
            if (assemblyMember.hasSubElements()) {
                Assembly assembly = (Assembly) assemblyMember;
                assembly.addSubMember(AssemblyMember.getAssemblyMember(false, tupelToAdd.getAssemblyMemberString()),
                        tupelToAdd.getIntCount());
                Terminal.printLine("OK");
            } else {
                throw new UnexpectedInputException(assemblyMember.getName() + " is not an Assembly");
            }
        }
    },
    /**
    *
    */
    REMOVE_PART("removePart" + UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString()
            + UserInteractionStrings.REGEX_MEMBER_SUBSTRACTION.toString()) {
        @Override
        public void execute(String commandString, Assembly knownAssemblies) throws UnexpectedInputException {
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
     * command to exit the application
     */
    QUIT("quit") {
        @Override
        public void execute(String commandString, Assembly knownAssemblies) {
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
     * @param commandString   string to parse/with which the right command should be
     *                        determined
     * @param knownAssemblies assembly cache as a tiny database
     * @return the right command to user for this commandString
     * @throws UnexpectedInputException if the command is not a valid command
     */
    public static Commands executeRightCommand(String commandString, Assembly knownAssemblies)
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
    public abstract void execute(String commandString, Assembly knownAssemblies) throws UnexpectedInputException;

    /**
     * method used by the main method, to determine whether to quit or not
     * 
     * @return returns whether to quit or not
     */
    public static boolean shouldDoQuit() {
        return shouldQuit;
    }

    private static AssemblyMemberCountTupel[] getAssemblyMemberArray(String initalizationString, String nameOfParent)
            throws UnexpectedInputException {

        String[] assemblyMemberStringArray = initalizationString
                .split(UserInteractionStrings.REGEX_COORDINATE_OUTER_SEPERATOR.toString());

        checkForDoubleNames(assemblyMemberStringArray, nameOfParent);

        AssemblyMemberCountTupel[] assemblyMemberCountTupelArray = new AssemblyMemberCountTupel[assemblyMemberStringArray.length];

        for (int matchNumber = 0; matchNumber < assemblyMemberStringArray.length; matchNumber++) {
            assemblyMemberCountTupelArray[matchNumber] = getAssemblyMemberCountTupel(
                    assemblyMemberStringArray[matchNumber]);
        }
        return assemblyMemberCountTupelArray;
    }

    private static void checkForDoubleNames(String[] assemblyMemberStrings, String nameOfParent)
            throws UnexpectedInputException {
        for (int currentStringNr = 0; currentStringNr < assemblyMemberStrings.length; currentStringNr++) {
            String currentName = assemblyMemberStrings[currentStringNr]
                    .split(UserInteractionStrings.REGEX_COORDINATE_INNER_SEPERATOR.toString())[1];
            for (int numberOfStringBefore = 0; numberOfStringBefore < currentStringNr; numberOfStringBefore++) {
                String currentNameBefore = assemblyMemberStrings[numberOfStringBefore]
                        .split(UserInteractionStrings.REGEX_COORDINATE_INNER_SEPERATOR.toString())[1];
                if (currentName.equals(currentNameBefore)) {
                    throw new UnexpectedInputException("You have entered a name at least twice");
                }
            }
            if (currentName.equals(nameOfParent)) {
                throw new UnexpectedInputException("An Assembly cannot contain itself");
            }
        }
    }

    private static AssemblyMemberCountTupel getAssemblyMemberCountTupel(String assemblyMemberString) {
        String[] assemblyMemberProperties = assemblyMemberString
                .split(UserInteractionStrings.REGEX_COORDINATE_INNER_SEPERATOR.toString());
        int count = Integer.parseInt(assemblyMemberProperties[0]);
        String nameString = assemblyMemberProperties[1];
        return new AssemblyMemberCountTupel(nameString, count);
    }

    private static String getCommandOptions(String commandString) {
        String commandStringData = commandString
                .split(UserInteractionStrings.REGEX_COMMAND_PARAMETER_SEPERATOR.toString())[1];
        return commandStringData;
    }
}
