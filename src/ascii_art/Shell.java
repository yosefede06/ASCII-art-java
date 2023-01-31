package ascii_art;

import ascii_art.img_to_char.BrightnessImgCharMatcher;
import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import image.Image;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Class that will perform all the interface operations and will be operated by the Driver class,
 * which means Driver main will be used in order to run the software.
 */
public class Shell {
    private static final int MIN_VALID_CAST_CHAR = 32;
    private static final int MAX_VALID_CAST_CHAR = 126;
    private static final int SPACE_INT_ASCII = 32;
    private static final int INITIAL_CHARS_IN_ROW = 64;
    private static final int MIN_PIXELS_PER_CHAR = 2;
    private static final Character[] DEFAULT_IMAGE_CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final String START_SHELL = "<<<";
    private static final String EXIT_COMMAND = "exit";
    private static final String CHARS_COMMAND = "chars";
    private static final String ADD_COMMAND = "add";
    private static final String REMOVE_COMMAND = "remove";
    private static final String ALL_COMMAND = "all";
    private static final String SPACE_COMMAND = "space";
    private static final String RESIZE_COMMAND = "res";
    private static final String ADD_ERROR_MESSAGE = "Did not add due to incorrect format";
    private static final String REMOVE_ERROR_MESSAGE = "Did not remove due to incorrect format";
    private static final String GENERAL_ERROR_MESSAGE = "Did not executed due to incorrect format";
    private static final String RESIZE_UP_COMMAND = "up";
    private static final String RESIZE_DOWN_COMMAND = "down";
    private static final String CONSOLE_COMMAND = "console";
    private static final String RENDER_COMMAND = "render";
    private static final String SUCCESSFUL_RESIZE_MESSAGE = "Width set to <charsInRow>";
    private static final String SUCCESSFUL_RESIZE_MESSAGE_PLACEHOLDER = "<charsInRow>";
    private static final String EXCEED_BOUNDARIES_ERROR_MESSAGE = "Did not change due to exceeding boundaries";
    private static final Boolean DEFAULT_IS_RENDERED_TO_CONSOLE = false;
    private static final String FONT_HTML_RENDER = "Courier New";
    private static final String NAME_FILE_HTML = "out.html";


    private final HashSet<Character> imageCharacters;
    private final Scanner scanner;
    private final int minCharsInRow;
    private final int maxCharsInRow;
    private final HtmlAsciiOutput htmlRender;
    private final BrightnessImgCharMatcher brightnessImgCharMatcher;
    private final ConsoleAsciiOutput consoleRender;
    private Boolean isRenderedToConsole;
    private Integer charsInRow;


    /**
     * Class constructor. Inits instance of the class.
     * @param img Image.
     */
    public Shell(Image img) {
        isRenderedToConsole = DEFAULT_IS_RENDERED_TO_CONSOLE;
        htmlRender = new HtmlAsciiOutput(NAME_FILE_HTML, FONT_HTML_RENDER);
        consoleRender = new ConsoleAsciiOutput();
        brightnessImgCharMatcher = new BrightnessImgCharMatcher(img, FONT_HTML_RENDER);
        scanner = new Scanner(System.in);
        imageCharacters = new HashSet<>(Arrays.asList(DEFAULT_IMAGE_CHARACTERS));
        minCharsInRow = Math.max(1, img.getWidth()/img.getHeight());
        maxCharsInRow = img.getWidth() / MIN_PIXELS_PER_CHAR;
        charsInRow = Math.max(Math.min(INITIAL_CHARS_IN_ROW, maxCharsInRow), minCharsInRow);
    }

    /**
     * Static method that converts a HashSet of characters to a simple character array.
     * @param hashSetToConvert HashSet of characters
     * @return Character[].
     */
    private static Character[] hashSetToArray(HashSet<Character> hashSetToConvert) {
        Character[] arrayConversion = new Character[hashSetToConvert.size()];
        int i = 0;
        for(Character c: hashSetToConvert) {
            arrayConversion[i] = c;
            i++;
        }
        return arrayConversion;
    }

    /**
     * Method that takes care of receiving input from the user and exiting.
     */
    public void run() {
        System.out.print(START_SHELL + " ");
        String command = scanner.nextLine();
        if(!command.equals(EXIT_COMMAND)) {
            runCommand(command.split(" "));
            run();
        }
    }

    /**
     * Concatenates the array of image characters to a String.
     * @return Concatenated string.
     */
    private String charsToString() {
        String outputCharacters = "";
        for(Character c: imageCharacters) {
            outputCharacters += c + " ";
        }
        return outputCharacters;
    }

    /**
     * Performs the command actions.
     * @param command Split space array of the user input command.
     */
    private void runCommand(String[] command) {
        // Handles empty command (when user just press enter)
        if(command.length == 0) {
            handleGeneralInvalidCommand();
        }
        String firstCommand = command[0];
        switch(firstCommand) {
            case CHARS_COMMAND:
                System.out.println(charsToString());
                break;
            case ADD_COMMAND:
                handleAddCommand(command);
                break;
            case REMOVE_COMMAND:
                handleRemoveCommand(command);
                break;
            case RESIZE_COMMAND:
                handleResizeCommand(command);
                break;
            case CONSOLE_COMMAND:
                handleConsoleCommand();
                break;
            case RENDER_COMMAND:
                handleRenderCommand();
                break;
            default:
                handleGeneralInvalidCommand();
        }
    }

    /**
     * Private method that handles "render" command from input, by rendering the image in HTML or console format.
     */
    private void handleRenderCommand() {
        char[][] brightnessImg=brightnessImgCharMatcher.chooseChars(charsInRow, Shell.hashSetToArray(imageCharacters));
        if(!isRenderedToConsole) {
            htmlRender.output(brightnessImg);
        }
        else {
            consoleRender.output(brightnessImg);
        }
    }

    /**
     * Private method that handles "console" command from input, which renders in console
     * instead of html output.
     */
    private void handleConsoleCommand() {
        isRenderedToConsole = true;
    }

    /**
     * Handles general invalid input and outputs an error message.
     */
    private void handleGeneralInvalidCommand() {
        System.out.println(GENERAL_ERROR_MESSAGE);
    }

    /**
     * Private method that handles "re" (resize) command from input.
     * @param command Split space array of the user input command.
     */
    private void handleResizeCommand(String[] command) {
        if(command.length != 2) {
            handleGeneralInvalidCommand();
            return;
        }
        String secondCommand = command[1];
        switch (secondCommand) {
            case RESIZE_UP_COMMAND:
                handleResizeUpCommand();
                break;
            case RESIZE_DOWN_COMMAND:
                handleResizeDownCommand();
                break;
            default:
                handleGeneralInvalidCommand();
                break;
        }
    }

    /**
     * Performs the "re up" (resize up) command by doubling the number of ASCII characters per row.
     */
    private void handleResizeUpCommand() {
        if(charsInRow * 2 <= maxCharsInRow) {
            charsInRow *= 2;
            outputSetSize();
        }
        else {
            outputExceedErrorResize();
        }
    }

    /**
     * Performs the "re down" (resize down) command by halving the number of ASCII characters per row.
     */
    private void handleResizeDownCommand() {
        if(charsInRow / 2 >= minCharsInRow) {
            charsInRow /= 2;
            outputSetSize();
        }
        else {
            outputExceedErrorResize();
        }
    }

    /**
     * Outputs error message when user tries to resize image from command line, but it exceeds boundaries.
     */
    private void outputExceedErrorResize() {
        System.out.println(EXCEED_BOUNDARIES_ERROR_MESSAGE);
    }

    /**
     * Outputs new updated size set to console.
     */
    private void outputSetSize() {
        System.out.println(SUCCESSFUL_RESIZE_MESSAGE.replaceAll(SUCCESSFUL_RESIZE_MESSAGE_PLACEHOLDER,
                Integer.toString(charsInRow)));
    }

    /**
     * Private method that handles "add" command from input.
     * @param command Split space array of the user input command.
     */
    private void handleAddCommand(String[] command) {
        // Handles command validity.
        if(!checkUpdateValidity(command)) {
            System.out.println(ADD_ERROR_MESSAGE);
            return;
        }
        String secondCommand = command[1];
        switch (secondCommand) {
            case ALL_COMMAND:
                handleAddAllCommand();
                break;
            case SPACE_COMMAND:
                handleAddSpaceCommand();
                break;
            default:
                handleAddRangeCommand(secondCommand.charAt(0), secondCommand.charAt(2));
        }
    }

    /**
     * Private method that handles "remove" command from input.
     * @param command Split space array of the user input command.
     */
    private void handleRemoveCommand(String[] command) {
        // Handles command validity.
        if(!checkUpdateValidity(command)) {
            System.out.println(REMOVE_ERROR_MESSAGE);
            return;
        }
        String secondCommand = command[1];
        switch (secondCommand) {
            case ALL_COMMAND:
                handleRemoveAllCommand();
                break;
            case SPACE_COMMAND:
                handleRemoveSpaceCommand();
                break;
            default:
                handleRemoveRangeCommand(secondCommand.charAt(0), secondCommand.charAt(2));
        }
    }

    /**
     * Performs the "remove *-*" command by removing all the valid ASCII characters from image characters for
     * those who are in the range.
     */
    private void handleRemoveRangeCommand(Character start, Character end) {
        addCharactersInRange(Math.min((int)start, (int)end), Math.max((int)start, (int)end));
    }

    /**
     * Performs the "remove space" command by removing the space ASCII character to image characters.
     */
    private void handleRemoveSpaceCommand() {
        imageCharacters.remove((char)SPACE_INT_ASCII);
    }

    /**
     * Performs the "remove all" command by remove all the valid ASCII characters to image characters.
     */
    private void handleRemoveAllCommand() {
        removeCharactersInRange(MIN_VALID_CAST_CHAR, MAX_VALID_CAST_CHAR);
    }

    /**
     * Private method that removes from image characters the ASCII characters that there integer casting are placed
     * at the range [start, end]. (Assumes start <= end)
     * @param start First integer casting character to remove.
     * @param end Last integer casting character to remove.
     */
    private void removeCharactersInRange(Integer start, Integer end) {
        for(int asciiInt = start; asciiInt <= end; asciiInt++) {
            imageCharacters.remove((char)asciiInt);
        }
    }

    /**
     * Performs the "add *-*" command by adding all the valid ASCII characters to image characters for
     * those who are in the range.
     */
    private void handleAddRangeCommand(Character start, Character end) {
        addCharactersInRange(Math.min((int)start, (int)end), Math.max((int)start, (int)end));
    }

    /**
     * Performs the "add all" command by adding all the valid ASCII characters to image characters.
     */
    private void handleAddAllCommand() {
        addCharactersInRange(MIN_VALID_CAST_CHAR, MAX_VALID_CAST_CHAR);
    }

    /**
     * Private method that adds to image characters the ASCII characters that there integer casting are placed
     * at the range [start, end]. (Assumes start <= end)
     * @param start First integer casting character to add.
     * @param end Last integer casting character to add.
     */
    private void addCharactersInRange(Integer start, Integer end) {
        for(int asciiInt = start; asciiInt <= end; asciiInt++) {
            imageCharacters.add((char)asciiInt);
        }
    }

    /**
     * Performs the "add space" command by adding the space ASCII character to image characters.
     */
    private void handleAddSpaceCommand() {
        imageCharacters.add((char)SPACE_INT_ASCII);
    }

    /**
     * Private method that checks for a char it's validity according to legal user input commands.
     * @param c Character.
     * @return True if it's valid, false otherwise.
     */
    private boolean validASCIIChart(Character c) {
        int castAscii = (int) c;
        return MIN_VALID_CAST_CHAR <=  castAscii&&  castAscii <= MAX_VALID_CAST_CHAR;
    }

    /**
     * Checks validity for range character on add command.
     * @param s Second parameter of add command.
     * @return True if the string valid, false otherwise.
     */
    private boolean checkRangeCharactersValidity(String s) {
        return s.length() == 3 && validASCIIChart(s.charAt(0)) && s.charAt(1) == '-' && validASCIIChart(s.charAt(2));
    }

    /**
     * Checks add and remove command validity
     * @param command Split space array of the user input command.
     * @return True if legal command, false otherwise.
     */
    private boolean checkUpdateValidity(String[] command) {
        return command.length == 2 && (command[1].equals(ALL_COMMAND) ||
                command[1].equals(SPACE_COMMAND) || checkRangeCharactersValidity(command[1]));

    }
}
