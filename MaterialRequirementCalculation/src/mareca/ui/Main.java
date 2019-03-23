package mareca.ui;

import mareca.UnexpectedInputException;
import edu.kit.informatik.Terminal;

/**
 * @author ratzupaltuff main class, this is where the program starts. This class
 *         handles how to start the program internally and how to give the game
 *         all the needed information (input strings)
 */
public class Main {

    /**
     * main method that serves as the starting point to execute the material requirement calculation (mareca)
     * 
     * @param args arguments given to the main method (not used)
     */
    public static void main(String[] args) {
        do {
            // current command entered
            String inputString = Terminal.readLine();
            try {
                // try to execute the command, if its not possible, an error is thrown
                Commands.executeRightCommand(inputString).execute(inputString);
            } catch (UnexpectedInputException e) {
                // print the error
                Terminal.printError(e.getMessage());
            }
            // to tell the main, when to quit, I added the shouldDoQuit method, to make the
            // Commands Class able to control when to quit
        } while (!Commands.shouldDoQuit());
    }
}
