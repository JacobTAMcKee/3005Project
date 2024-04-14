import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class Main {

    static loginStatus createUser(String url, String user, String pword, String fName, String lName, String uName, String password){

        // Current date
        Date d = new Date(Calendar.getInstance().getTimeInMillis());

        // Add new user to the database
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);
            Statement statement = connection.createStatement();
            String insertSQL = "INSERT INTO members (first_name, last_name, join_date, username, password) VALUES ('" +
                    fName + "', '" + lName + "', '" + d + "', '" + uName + "', '" + password + "')";
            int rowsInserted = statement.executeUpdate(insertSQL);
            if (rowsInserted > 0) {
                System.out.println("Account creation successful!");
            } else{
                System.out.println("Error creating account!");
            }
            statement.close();
            connection.close();
            return new loginStatus(true, "member");
        }
        catch(Exception e){
            System.out.println(e);
            return new loginStatus(false, "null");
        }
    }

    static loginStatus login(String url, String user, String pword, String uName, String password){

        // Check if the given user exists and login if so
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);

            // First check members table
            Statement statement = connection.createStatement();
            String querySQL = "SELECT username, password FROM members WHERE username = '" + uName + "' AND password = '" +
                    password + "'";
            ResultSet result = statement.executeQuery(querySQL);
            if (result.isBeforeFirst()) {
                System.out.println("Login successful!");
                statement.close();
                connection.close();
                result.close();
                return new loginStatus(true, "member");
            }

            // Then trainers table
            statement = connection.createStatement();
            querySQL = "SELECT username, password FROM trainers WHERE username = '" + uName + "' AND password = '" +
                    password + "'";
            result = statement.executeQuery(querySQL);
            if (result.isBeforeFirst()) {
                System.out.println("Login successful!");
                statement.close();
                connection.close();
                return new loginStatus(true, "trainer");
            }

            // Then admins table
            statement = connection.createStatement();
            querySQL = "SELECT username, password FROM admins WHERE username = '" + uName + "' AND password = '" +
                    password + "'";
            result = statement.executeQuery(querySQL);
            if (result.isBeforeFirst()) {
                System.out.println("Login successful!");
                statement.close();
                connection.close();
                return new loginStatus(true, "admin");
            }

            return new loginStatus(false, "null");
        }
        catch(Exception e){
            System.out.println(e);
            return new loginStatus(false, "null");
        }
    }

    static boolean updateUser(String url, String user, String pword, String username, String field, String new_field){

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);
            Statement statement = connection.createStatement();
            String insertSQL = "UPDATE members SET " + field + " = '" + new_field + "' where username = " + "'" + username + "'";
            statement.executeUpdate(insertSQL);
            statement.close();
            connection.close();
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    static void addGoal(String url, String user, String pword, String uName, String desc){

        // Add new goal to the goals table
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);
            Statement statement = connection.createStatement();
            String insertSQL = "INSERT INTO goals (member_id, description) SELECT m.member_id, '" + desc + "' FROM members m WHERE m.username = '" +
                    uName + "'";
            System.out.println(insertSQL);
            int rowsInserted = statement.executeUpdate(insertSQL);
            if (rowsInserted > 0) {
                System.out.println("Goal creation successful!");
            } else{
                System.out.println("Error creating goal!");
            }
            statement.close();
            connection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    static void getGoals(String url, String user, String pword, String uName){

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);
            Statement statement = connection.createStatement();
            String querySQL = "SELECT G.* FROM goals G WHERE G.member_id IN (SELECT M.member_id FROM members M WHERE " +
                    "M.username = '" + uName + "')";
            statement.executeQuery(querySQL);
            ResultSet resultSet = statement.getResultSet();
            while(resultSet.next()){
                String desc = resultSet.getString("description");
                System.out.println(desc);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    static void getTrainers(String url, String user, String pword){

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);
            Statement statement = connection.createStatement();
            String querySQL = "SELECT * FROM TRAINERS";
            statement.executeQuery(querySQL);
            ResultSet resultSet = statement.getResultSet();
            while(resultSet.next()){
                String fname = resultSet.getString("first_name");
                String lname = resultSet.getString("last_name");
                Time start = resultSet.getTime("start_time");
                Time end = resultSet.getTime("end_time");
                System.out.println(fname + " " + lname + ": Available " + start + " to " + end);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    static boolean changeAvailTime(String url, String user, String pword, String uName, Time start_time, Time end_time){
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);
            Statement statement = connection.createStatement();
            String querySQL = "UPDATE trainers SET start_time = '" + start_time + "', " + "end_time = '" + end_time + "' where username = " + "'" + uName + "'";
            statement.executeUpdate(querySQL);
            statement.close();
            connection.close();
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    static boolean scheduleSession(String url, String user, String pword, String uName, String trainer_name, Time start_time, Time end_time, Date d, String desc) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);
            Statement statement = connection.createStatement();
            String querySQL = "SELECT * FROM TRAINERS WHERE first_name = '" + trainer_name + "'";
            statement.executeQuery(querySQL);
            ResultSet resultSet = statement.getResultSet();

            // Retrieve trainer details
            resultSet.next();
            int trainer_id = resultSet.getInt("trainer_id");
            Time trainer_start = resultSet.getTime("start_time");
            Time trainer_end = resultSet.getTime("end_time");
            resultSet.close();

            // Check if trainer is available during requested time
            if (start_time.toLocalTime().isBefore(trainer_start.toLocalTime()) || end_time.toLocalTime().isAfter(trainer_end.toLocalTime())) {
                System.out.println("Your requested time for the session is outside of the trainer's available time!");
                return false;
            }
            statement.close();

            // Create the entry in bookings table
            statement = connection.createStatement();
            querySQL = "INSERT INTO bookings (booking_date, description) VALUES ('" + d + "'," + " '" + desc + "')";
            int rowsInserted = statement.executeUpdate(querySQL);
            if (rowsInserted > 0) {
                System.out.println("Session creation successful!");
            } else{
                System.out.println("Error creating session!");
                return false;
            }
            statement.close();

            // Create the entry in bookingsdetails table
            statement = connection.createStatement();
            querySQL = "INSERT INTO bookingsdetails (booking_id, member_id, trainer_id) " +
                    "SELECT b.booking_id, m.member_id, " + trainer_id + " " +
                    "FROM bookings b, members m " +
                    "WHERE b.description = '" + desc + "' AND m.username = '" + uName + "'";
            rowsInserted = statement.executeUpdate(querySQL);
            if (rowsInserted > 0) {
                System.out.println("Session details creation successful!");
            } else{
                System.out.println("Error creating session details!");
                return false;
            }
            statement.close();


            connection.close();
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    static boolean createBilling(String url, String user, String pword, String uName) {
        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);
            Statement statement = connection.createStatement();


            String insertSQL = "INSERT INTO billings (amount_payed, member_id) " +
                    "SELECT 20, m.member_id " +
                    "FROM members m " +
                    "WHERE m.username = '" + uName + "'";

            int rowsInserted = statement.executeUpdate(insertSQL);
            if (rowsInserted > 0) {
                System.out.println("Billing creation successful!");
            } else{
                System.out.println("Error creating billing!");
            }
            statement.close();
            connection.close();
            return true;
        }
        catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    static void getMaintenances(String url, String user, String pword){

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);
            Statement statement = connection.createStatement();
            String querySQL = "SELECT * FROM maintenances";
            statement.executeQuery(querySQL);
            ResultSet resultSet = statement.getResultSet();
            while(resultSet.next()){
                String desc = resultSet.getString("description");
                Date d = resultSet.getDate("start_date");
                System.out.println("Description: " + desc);
                System.out.println("Start Date: " + d);
            }
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    static void getBillings(String url, String user, String pword){

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(url, user, pword);
            Statement statement = connection.createStatement();
            String querySQL = "SELECT * FROM billings";
            statement.executeQuery(querySQL);
            ResultSet resultSet = statement.getResultSet();
            while(resultSet.next()){
                int bid = resultSet.getInt("billing_id");
                int amt = resultSet.getInt("amount_payed");
                int mid = resultSet.getInt("member_id");
                System.out.println("Billing ID: " + bid);
                System.out.println("Member ID: " + amt);
                System.out.println("Amount Payed: $" + mid + ".00");
            }
            resultSet.close();
            statement.close();
            connection.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    static void displayMenu(){
        System.out.println("Please select an option:");
        System.out.println("1: Update Profile");
        System.out.println("2: Add Fitness Goal");
        System.out.println("3: View Goals");
        System.out.println("4: Schedule Private Session");
        System.out.println("5: Join Group Class");
        System.out.println("6: (Trainer Function) Change Available Times");
        System.out.println("7: (Admin Function) View Maintenances");
        System.out.println("8: (Admin Function) Billings");
        System.out.println("0: Exit");
    }

    // Display the login menu when the user is not currently logged in
    static void displayLoginMenu(){
        System.out.println("Welcome! Please login or create a new account.");
        System.out.println("1: Create New Account");
        System.out.println("2: Login");
        System.out.println("0: Exit");
    }
    public static void main(String[] args) {

        // Please change these fields as necessary!
        String url = "jdbc:postgresql://localhost:5432/ProjTest";
        String user = "postgres";
        String pword = "poplop123";

        String currUser = "";
        loginStatus token = new loginStatus(false, "null");
        Scanner s = new Scanner(System.in);
        String username;
        String password;

        while(true) {

            if (!token.logged_in) {
                displayLoginMenu();
                int choice = s.nextInt();
                s.nextLine();
                while(choice < 0 || choice > 2) {
                    System.out.println("Please select a valid option!");
                    choice = s.nextInt();
                    s.nextLine();
                }
                switch(choice){
                    case 0:
                        System.exit(0);
                    case 1:
                        System.out.println("Input First Name:");
                        String first_name = s.nextLine();
                        System.out.println("Input Last Name:");
                        String last_name = s.nextLine();
                        System.out.println("Input New Username:");
                        username = s.nextLine();
                        System.out.println("Input New Password:");
                        password = s.nextLine();
                        token = createUser(url, user, pword, first_name, last_name, username, password);
                        if (token.logged_in) {
                            currUser = username;
                        }
                        else {
                            System.out.println("Please try again!");
                        }
                        break;
                    case 2:
                        System.out.println("Input Username:");
                        username = s.nextLine();
                        System.out.println("Input Password:");
                        password = s.nextLine();
                        token = login(url, user, pword, username, password);
                        if (token.logged_in) {
                            currUser = username;
                        }
                        else {
                            System.out.println("Incorrect credentials, please try again!");
                        }
                        break;
                }
            }

            else {
                displayMenu();
                int choice = s.nextInt();
                s.nextLine();
                while(choice < 0 || choice > 8) {
                    System.out.println("Please select a valid option!");
                    choice = s.nextInt();
                    s.nextLine();
                }
                switch(choice){
                    case 0:
                        System.exit(0);
                    case 1:
                        String field = "temp";
                        System.out.println("Please select a field to update:");
                        System.out.println("1: First Name");
                        System.out.println("2: Last Name");
                        System.out.println("3: Username");
                        System.out.println("4: Password");
                        System.out.println("0: Cancel");
                        String newField;
                        int updateChoice = s.nextInt();
                        s.nextLine();
                        while(updateChoice < 0 || updateChoice > 4) {
                            System.out.println("Please select a valid option!");
                            updateChoice = s.nextInt();
                            s.nextLine();
                        }
                        switch(updateChoice) {
                            case 1:
                                field = "first_name";
                                break;
                            case 2:
                                field = "last_name";
                                break;
                            case 3:
                                field = "username";
                                break;
                            case 4:
                                field = "password";
                                break;
                        }
                        System.out.println("Enter the new value:");
                        newField = s.nextLine();
                        if (updateUser(url, user, pword, currUser, field, newField)) {
                            System.out.println("Field updated successfully!");
                        }
                        else {
                            System.out.println("Field not updated!");
                        }
                        break;
                    case 2:
                        System.out.println("Please input a description for your new goal:");
                        String new_goal = s.nextLine();
                        addGoal(url, user, pword, currUser, new_goal);
                        break;
                    case 3:
                        System.out.println("Current goals:");
                        getGoals(url, user, pword, currUser);
                        break;
                    case 4:
                        System.out.println("Select a trainer's first name from this list of trainers:");
                        getTrainers(url, user, pword);
                        String trainer_fname = s.nextLine();
                        System.out.println("Input a description for the session:");
                        String desc = s.nextLine();
                        System.out.println("Select a start time for the session (HH:MM:SS):");
                        String start_time = s.nextLine();
                        System.out.println("Select an end time for the session (HH:MM:SS):");
                        String end_time = s.nextLine();
                        System.out.println("Select a date for the session (YYYY MM DD):");
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, s.nextInt());
                        cal.set(Calendar.MONTH, s.nextInt()-1);
                        cal.set(Calendar.DAY_OF_MONTH, s.nextInt());
                        s.nextLine();
                        Date d = new Date(cal.getTimeInMillis());
                        Time s_time = java.sql.Time.valueOf(start_time);
                        Time e_time = java.sql.Time.valueOf(end_time);
                        scheduleSession(url, user, pword, currUser, trainer_fname, s_time, e_time, d, desc);

                        System.out.println("This session will cost $20.00 hourly. Please enter card number (XXXX-XXXX-XXXX-XXXX):");
                        String temp = s.nextLine();
                        createBilling(url, user, pword, currUser);
                        break;
                    case 5:
                        System.out.println("Unimplemented :(");
                        break;
                    case 6:
                        if (!token.role.equals("trainer")) {
                            System.out.println("This is a trainer-only function!");
                            break;
                        }
                        System.out.println("Please enter your new start time (HH:MM:SS):");
                        String n_start_time = s.nextLine();
                        System.out.println("Please enter your new end time (HH:MM:SS):");
                        String n_end_time = s.nextLine();
                        Time n_s_time = java.sql.Time.valueOf(n_start_time);
                        Time n_e_time = java.sql.Time.valueOf(n_end_time);
                        if (changeAvailTime(url, user, pword, currUser, n_s_time, n_e_time)) {
                            System.out.println("Time change successful!");
                        } else {
                            System.out.println("Time change unsuccessful!");
                        }
                        break;
                    case 7:
                        if (!token.role.equals("admin")) {
                            System.out.println("This is a admin-only function!");
                            break;
                        }
                        System.out.println("Current Maintenances:");
                        getMaintenances(url, user, pword);
                        break;
                    case 8:
                        if (!token.role.equals("admin")) {
                            System.out.println("This is a admin-only function!");
                            break;
                        }
                        System.out.println("All Billings:");
                        getBillings(url, user, pword);
                        break;
                }
            }
        }
    }
}
