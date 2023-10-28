import java.sql.*;
import java.util.Scanner;

public class Bank {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mondayproject";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "01";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("1. Create Account");
                System.out.println("2. Update Your Account");
                System.out.println("3. View Your Account");
                System.out.println("4. Disable Your Account");
                System.out.println("5. Deposit ");
                System.out.println("6. Withdraw");
                System.out.println("7. Show All Accounts");
                System.out.println("8. View Transaction History");
                System.out.println("0. Exit ");

                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        createAccount(connection, scanner);
                        break;
                    case 2:
                        updateAccount(connection, scanner);
                        break;
                    case 3:
                        viewAccount(connection, scanner);
                        break;
                    case 4:
                        disableAccount(connection, scanner);
                        break;
                    case 5:
                        deposit(connection, scanner);
                        break;
                    case 6:
                        withdraw(connection, scanner);
                        break;
                    case 7:
                        getAllAccounts();
                        break;
                    case 8:
                        viewTransaction(connection,scanner);
                        break;
                    case 0:
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("invalid choice");
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // create
    public static void createAccount(Connection connection, Scanner scanner) {
        int min = 100000;
        int max = 999999;
        int id = (int) (Math.random() * (max - min + 1) + min);
        ;
        int id2 = id + 100;
        int id3 = id2 + 100;
        System.out.println("Enter your name :");
        String name = scanner.next();
        System.out.println("Password :");
        String password = scanner.next();
        double balance = 0;
        String createAccount = "INSERT INTO accounts ( Id, Holder, Password, Balance ) VALUES (?,?,?,?)";
        String defaultTransaction = "INSERT INTO transactions ( Id, Account_id, Type, Amount, Date ) VALUES (?,?,?,?,?)";
        // create account
        try (PreparedStatement preparedStatement = connection.prepareStatement(createAccount)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, password);
            preparedStatement.setDouble(4, balance);
            int rowsinset = preparedStatement.executeUpdate();
            if (rowsinset > 0) {
                System.out.println("New Account has been added successfully");
            } else {
                System.out.println("Failed");
            }
            // create default transaction type deposit that amount = 0
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(defaultTransaction)) {
                java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
                preparedStatement1.setInt(1, id2);
                preparedStatement1.setInt(2, id);
                preparedStatement1.setString(3, "Deposit");
                preparedStatement1.setDouble(4, balance);
                preparedStatement1.setDate(5, date);
                int rowsinset1 = preparedStatement1.executeUpdate();
                if (rowsinset1 > 0) {
                    System.out.println("Transaction created");
                } else {
                    System.out.println("Failed");
                }
            }
            // create default transaction type withdraw that amount = 0
            try (PreparedStatement preparedStatement2 = connection.prepareStatement(defaultTransaction)) {
                java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
                preparedStatement2.setInt(1, id3);
                preparedStatement2.setInt(2, id);
                preparedStatement2.setString(3, "Withdraw");
                preparedStatement2.setDouble(4, balance);
                preparedStatement2.setDate(5, date);
                int rowsinset2 = preparedStatement2.executeUpdate();
                if (rowsinset2 > 0) {
                    System.out.println("Transaction created");
                } else {
                    System.out.println("Failed");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // update
    public static void updateAccount(Connection connection, Scanner scanner) {
        System.out.println("Log To Your Account");
        System.out.println("Input Your Account ID :");
        int id = scanner.nextInt();
        System.out.println("Input Your Account Password :");
        String password = scanner.next();
        System.out.println("Enter New Name :");
        String newName = scanner.next();
        System.out.println("Enter New Password : ");
        String newPassword = scanner.next();
        String sql = "UPDATE accounts SET Holder = ?, Password = ? WHERE Id = ? and Password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newPassword);
            preparedStatement.setInt(3, id);
            preparedStatement.setString(4, password);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println(" updated successfully.");
            } else {
                System.out.println("Id and Password do not match.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // view
    public static void viewAccount(Connection connection, Scanner scanner) {
        System.out.println("Log To Your Account");
        System.out.println("Input Your Account ID :");
        int id = scanner.nextInt();
        System.out.println("Input Your Account Password :");
        String password = scanner.next();
        String sql = "SELECT * FROM accounts WHERE Id = ? AND Password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("Incorrect Id and Email");
            } else {
                System.out.println("ID: " + resultSet.getInt(1));
                System.out.println("Holder's Name: " + resultSet.getString(2));
                System.out.println("Password: " + resultSet.getString(3));
                System.out.println("Balance: " + resultSet.getDouble(4));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // disable account
    public static void disableAccount(Connection connection, Scanner scanner) {
        System.out.println("Enter information to disable account");
        System.out.println("Enter your account Id ");
        int id = scanner.nextInt();
        System.out.println("Enter your account password");
        String password = scanner.next();
        String deleteTransaction = "DELETE FROM transactions WHERE Account_id = ? ";
        String deleteAccount = "DELETE FROM accounts WHERE Id = ? AND Password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteTransaction)) {
            preparedStatement.setInt(1, id);
            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println(" Deleted Transactions successfully.");
            } else {
                System.out.println(" Id and Password is not match.");
            }
            try (PreparedStatement preparedStatement1 = connection.prepareStatement(deleteAccount)){
                preparedStatement1.setInt(1,id);
                preparedStatement1.setString(2,password);
                int rowsDeleted1 = preparedStatement1.executeUpdate();
                if (rowsDeleted1 > 0) {
                    System.out.println(" Deleted Account successfully.");
                } else {
                    System.out.println(" Id and Password is not match.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // deposit
    public static void deposit(Connection connection, Scanner scanner) {
        System.out.println("Log To Your Account");
        System.out.println("Input Your Account ID :");
        int id = scanner.nextInt();
        System.out.println("Input Your Account Password :");
        String password = scanner.next();
        // sql
        String findAccount = "SELECT * FROM accounts WHERE Id = ? AND Password = ?";
        String deposit = "INSERT INTO transactions ( Id, Account_id, Type, Amount, Date ) VALUES (?,?,?,?,?)";
        String updateBalance = "UPDATE accounts SET balance = ( SELECT SUM(amount) FROM transactions WHERE Type = ? AND Account_id = ? ) " +
                "-( SELECT SUM(amount) FROM transactions WHERE Type = ? AND Account_id = ? )" +
                " WHERE Id = ?";
        // current date
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        try (PreparedStatement preparedStatement = connection.prepareStatement(findAccount)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("Incorrect Id and Password");
            } else {
                try (PreparedStatement preparedStatement1 = connection.prepareStatement(deposit)) {
                    System.out.println("Enter Your Amount");
                    double amount = scanner.nextDouble();
                    int min = 100000;
                    int max = 999999;
                    int randomNum = (int) (Math.random() * (max - min + 1) + min);
                    ;
                    preparedStatement1.setInt(1, randomNum);
                    preparedStatement1.setInt(2, id);
                    preparedStatement1.setString(3, "Deposit");
                    preparedStatement1.setDouble(4, amount);
                    preparedStatement1.setDate(5, date);
                    int rowsinset = preparedStatement1.executeUpdate();
                    if (rowsinset > 0) {
                        System.out.println("Deposit successfully");
                    } else {
                        System.out.println("Failed");
                    }
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(updateBalance);) {
                        preparedStatement2.setString(1, "Deposit");
                        preparedStatement2.setInt(2, id);
                        // preparedStatement2.setInt(3,id);
                        preparedStatement2.setString(3, "Withdraw");
                        preparedStatement2.setInt(4, id);
                        preparedStatement2.setInt(5, id);
                        int rowsUpdated = preparedStatement2.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Balance Already Added");
                        } else {
                            System.out.println("Failed");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // withdraw
    public static void withdraw(Connection connection, Scanner scanner) {
        System.out.println("Log To Your Account");
        System.out.println("Input Your Account ID :");
        int id = scanner.nextInt();
        System.out.println("Input Your Account Password :");
        String password = scanner.next();
        // sql
        String findAccount = "SELECT * FROM accounts WHERE Id = ? AND Password = ?";
        String withdraw = "INSERT INTO transactions ( Id, Account_id, Type, Amount, Date ) VALUES (?,?,?,?,?)";
        String updateBalance = "UPDATE accounts SET balance = " +
                "( SELECT SUM(amount) FROM transactions WHERE Type = ? AND Account_id = ? )" +
                "-( SELECT SUM(amount) FROM transactions WHERE Type = ? AND Account_id = ? )  WHERE Id = ?";
        // current date
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        try (PreparedStatement preparedStatement = connection.prepareStatement(findAccount)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("Incorrect Id and Password");
            } else {
                try (PreparedStatement preparedStatement1 = connection.prepareStatement(withdraw)) {
                    System.out.println("Enter Your Amount To Withdraw");
                    double amount = scanner.nextDouble();
                    int min = 100000;
                    int max = 999999;
                    int randomNum = (int) (Math.random() * (max - min + 1) + min);
                    ;
                    preparedStatement1.setInt(1, randomNum);
                    preparedStatement1.setInt(2, id);
                    preparedStatement1.setString(3, "Withdraw");
                    preparedStatement1.setDouble(4, amount);
                    preparedStatement1.setDate(5, date);
                    int rowsinset = preparedStatement1.executeUpdate();
                    if (rowsinset > 0) {
                        System.out.println("Withdraw successfully");
                    } else {
                        System.out.println("Failed");
                    }
                    try (PreparedStatement preparedStatement2 = connection.prepareStatement(updateBalance)) {
                        preparedStatement2.setString(1, "Deposit");
                        preparedStatement2.setInt(2, id);
                        preparedStatement2.setString(3, "Withdraw");
                        preparedStatement2.setInt(4, id);
                        preparedStatement2.setInt(5, id);
                        int rowsUpdated = preparedStatement2.executeUpdate();
                        if (rowsUpdated > 0) {
                            System.out.println("Balance Already Added");
                        } else {
                            System.out.println("Failed");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // getallaccounts
    public static void getAllAccounts() {
        try {
            // Establish a database connection
            Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Create a SQL statement
            Statement statement = connection.createStatement();

            // Execute a SQL query to select all student records
            ResultSet resultSet = statement.executeQuery("SELECT * FROM accounts");

            // Print the retrieved student records
            System.out.println("Accounts Records:");
            System.out.println("----------------");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String holder = resultSet.getString("holder");
                String password = resultSet.getString("password");
                Double balance = resultSet.getDouble("balance");

                System.out.println("ID: " + id);
                System.out.println("Holder Name: " + holder);
                System.out.println("Password: " + password.hashCode());
                System.out.println("Balance: " + balance);
                System.out.println("----------------");
            }

            // Close the database resources
            resultSet.close();
            statement.close();
            connection.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // viewtransaction
    public static void viewTransaction(Connection connection,Scanner scanner) {
        System.out.println("Log To Your Account");
        System.out.println("Input Your Account ID :");
        int id = scanner.nextInt();
        System.out.println("Input Your Account Password :");
        String password = scanner.next();
        String findAccount = "SELECT * FROM accounts WHERE Id = ? AND Password = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(findAccount)){
            preparedStatement.setInt(1,id);
            preparedStatement.setString(2,password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()){
                System.out.println("Incorrect Id and Password");
            }else {
                String findTransactions = "SELECT * FROM transactions WHERE Account_id = ? AND Amount >0";
                try (PreparedStatement preparedStatement1 = connection.prepareStatement(findTransactions)){
                    preparedStatement1.setInt(1,id);
                    ResultSet resultSet1 = preparedStatement1.executeQuery();
                    System.out.println("Transactions Records:");
                    System.out.println("----------------");
                    while (resultSet1.next()) {
                        int idTransaction = resultSet1.getInt("id");
                        String type = resultSet1.getString("type");
                        Double amount = resultSet1.getDouble("amount");
                        Date date = resultSet1.getDate("date");
                        System.out.println("ID: " + idTransaction);
                        //System.out.println("Type Name: " + type);
                        System.out.println("Amount: " + amount);
                        System.out.println("Date: " + date);
                        System.out.println("----------------");
                    }
                    // Close the database resources
                    connection.close();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
