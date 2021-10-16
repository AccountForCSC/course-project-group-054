package view;
import java.util.*;
import controller.*;
import data.*;
import entities.*;


public class View {
    // TODO Transfer all the views from Controller class here
    public static Scanner sc = new Scanner(System.in);

    /**
     * Menu View
     */

    public static String[] mainMenuOptions = {"Sign in to my account", "Create a new account", "Close app"};

    public static void menuView() {
        System.out.println("Welcome to " + Controller.appName);
        for (int i = 1; i <= mainMenuOptions.length; i++) {
            System.out.println(i + ") " + mainMenuOptions[i - 1]);
        }
        String menuInput = sc.nextLine();
        switch (menuInput) {
            case "1" -> loginView();
            case "2" -> signUpView();
            case "3" -> System.exit(1);
            default -> System.out.println("Please enter a valid option.");
        }
    }


    /**
     * Login View
     */
    public static void loginView() {
        // TODO: Implement this method
        System.out.println("Enter your email: ");
        String email = sc.nextLine();

        User user = Controller.getUser(email);
        if (user != null) {
            Controller.authenticateUser(user);
        } else {
            System.out.println("ERROR: There was a problem logging you in. Please try again.");
        }
    }

    /**
     * Login View
     */
    public static void signUpView() {
        String[] outputs = {"Full Name (*): ", "Email (*): ", "Phone: "};
        List<String> inputs = new ArrayList<>();

        System.out.println("Please enter the following information and press enter. Fields marked (*) are required.");
        for (String output: outputs) {
            System.out.println(output);
            String input = sc.nextLine();
            inputs.add(input);
        }

        Data.users.add(new User(inputs.get(0),0, inputs.get(1)));

        System.out.println("Thanks for signing up, " + inputs.get(0) + "! You can now log in.");
    }

    /**
     * Given a list of possible actions that the user can take, output the actions as a numbered list and request that
     * the user choose one, input their choice of action as the number corresponding to the chosen action, and return
     * the number.
     *
     * @param actions a list of the possible actions the user can take
     * @return a number between 1 and the length of the given list of actions, inclusive, representing the action at
     *         the corresponding position in the given list
     */
    public static int getActionView(String[] actions) {
        System.out.println("Please enter the number for the actions below:");
        for (int i = 1; i <= actions.length; i++) {
            System.out.println(i + ". " + actions[i]);
        }
        String input = sc.nextLine();
        int number;
        try {
            number = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Please select a valid option.");
            return getActionView(actions);
        }
        if ((1 <= number) && (number <= 7)) {
            return number;
        } else {
            System.out.println("Please select a valid option.");
            return getActionView(actions);
        }
    }

    /**
     * Create and return a new Group based on user input.
     * @return a new Group based on user input
     */
    public static Group createGroupView() {
        if (!Controller.getUserStatus()) {
            System.out.println("Error: You must be authenticated to create a new group.");
            return null;
        }

        String groupName;
        List<String> members = new ArrayList<>();
        members.add(Controller.getCurrentUser().getEmail());

        // Input the group's name
        System.out.println("Enter name of the group: ");
        groupName = sc.nextLine();

        // Input the names of the group's members
        boolean addAnotherMember = false;

        System.out.println("ADD GROUP MEMBERS:");
        System.out.println("You will now be asked to add group members. " +
                "Press enter if you don't want to add any member");

        do {
            System.out.println("Enter email of member " + members.size() + ": ");
            String member = sc.nextLine();
            if (member.equals("")) { continue; }
            members.add(member);

            System.out.println("Would you like to add more members? (y/n)");

            if (sc.nextLine().equals("y")) {
                addAnotherMember = Boolean.TRUE;
            } else {
                addAnotherMember = Boolean.FALSE;
            }
        } while (addAnotherMember);

        return new Group(groupName, members, new ArrayList<>(), "Edit group description in Manage Group.)");
    }

    /* For testing the code */
    public static void outputGroups() {
        System.out.println(Data.groups);
        System.out.println(Data.groups.get(1).getGroupName());
    }
}
