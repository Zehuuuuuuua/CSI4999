package com.newtest.test.test;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;

public class SignInConnection extends AsyncTask {
    private final String TAG = "SignIn Connection";
    private Connection connection;

    //DB Connection strings
    private final String host = "cardguard-db.mysql.database.azure.com:3306";
    private final String database = "cardguard_db";
    private final String adminUsername = "cardguard-admin@cardguard-db";
    private final String adminPassword = "password12345!";

    //For DB query, nothing to do with actual DB connection
    private String username;
    private String password;

    //Also nothing to do with DB connection
    private User user;

    public SignInConnection(String username, String password) {
        this.username = username;
        this.password = password;

        user = null;

        System.out.println("Created SignInConnection object successfully.");
    }

    public User connect() throws SQLException {

        // Check that the driver is installed
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception ex) {
            System.out.println("JDBC Driver not found.");
            ex.printStackTrace();
        }

        System.out.println("MySQL JDBC driver detected in library path.");

        // Initialize connection object
        connection = null;

        try {
            String url = String.format("jdbc:mysql://%s/%s", host, database);

            // Set connection properties
            Properties properties = new Properties();
            properties.setProperty("user", adminUsername);
            properties.setProperty("password", adminPassword);
            properties.setProperty("useSSL", "true");
            properties.setProperty("verifyServerCertificate", "true");
            properties.setProperty("requireSSL", "false");

            // Connect to MySQL server with set parameters
            connection = DriverManager.getConnection(url, properties);
        } catch (SQLException ex) {
            throw new SQLException("Failed to create connection to database.", ex);
        }

        if (connection != null) {
            // Query database for all rows where username and password match (should be 1 at most)
            try {
                System.out.println("Attempting to query database...");

                Statement statement = connection.createStatement();
                ResultSet results = statement.executeQuery(
                        "SELECT * FROM users "
                                + "WHERE username='" + username
                                +"' AND password ='" + password +"';");

                while (results.next()) {
                    String ID = results.getString(1);
                    String username = results.getString(2);
                    String password = results.getString(3);
                    String emailAddress = results.getString(4);
                    String firstName = results.getString(5);
                    String lastName = results.getString(6);
                    String accountType = results.getString(7);

                    user = new User(ID, username, password, firstName, lastName, emailAddress, accountType);
                }
            } catch (SQLException ex) {
                System.out.println("Query Error");
                System.out.print(ex.toString());
            }

            // If user object is found, attempt to populate its transactions ArrayList
            if(user != null) {
                System.out.println(user.toString());

                try {
                    // Query for transactions the user was involved in
                    Statement txStatement = connection.createStatement();
                    ResultSet txResults = txStatement.executeQuery(
                            "SELECT * FROM transactions "
                                    + "WHERE sender='" + username + "' "
                                    + "OR recipient='" + username + "';");

                    // While query isn't empty:
                    while (txResults.next()) {
                        //Read result to necessary transaction variables
                        String transactionID = txResults.getString(1);
                        String senderName = txResults.getString(2);
                        String recipientName = txResults.getString(3);
                        String initiatorName = txResults.getString(4);
                        Float transactionAmount = txResults.getFloat(5);
                        String memo = txResults.getString(6);
                        Date startDate = txResults.getDate(7);
                        Date completionDate = txResults.getDate(8);
                        Boolean inProgress = txResults.getBoolean(9);
                        Boolean confirmed = txResults.getBoolean(10);

                        // Because users are only represented by their names in the transactions
                        //  table, they must be created by querying again
                        User sender = null;
                        User recipient = null;
                        User initiator = null;

                        try {

                            //Query to create sender user
                            Statement usersStatement = connection.createStatement();
                            ResultSet userResults = usersStatement.executeQuery(
                                    "SELECT * FROM users "
                                    + "WHERE username='" + senderName +"';");

                            while(userResults.next()) {
                                String ID = userResults.getString(1);
                                String username = userResults.getString(2);
                                String password = userResults.getString(3);
                                String emailAddress = userResults.getString(4);
                                String firstName = userResults.getString(5);
                                String lastName = userResults.getString(6);
                                String accountType = userResults.getString(7);
                                sender = new User(ID, username, password, firstName, lastName, emailAddress, accountType);
                            }
                        } catch (SQLException ex) {
                            Log.e(TAG, ex.toString());
                            ex.printStackTrace();
                        }

                        try {
                            // Query to create recipient user
                            Statement usersStatement = connection.createStatement();
                            ResultSet userResults = usersStatement.executeQuery(
                                    "SELECT * FROM users "
                                            + "WHERE username='" + recipientName +"';");

                            while (userResults.next()) {
                                String ID = userResults.getString(1);
                                String username = userResults.getString(2);
                                String password = userResults.getString(3);
                                String emailAddress = userResults.getString(4);
                                String firstName = userResults.getString(5);
                                String lastName = userResults.getString(6);
                                String accountType = userResults.getString(7);

                                recipient = new User(ID, username, password, firstName, lastName, emailAddress, accountType);
                            }
                        } catch (SQLException ex) {
                            Log.e(TAG, ex.toString());
                            ex.printStackTrace();
                        }

                        try {
                            // Query to create initiator user
                            Statement usersStatement = connection.createStatement();
                            ResultSet userResults = usersStatement.executeQuery(
                                    "SELECT * FROM users "
                                            + "WHERE username='" + initiatorName +"';");
                            while(userResults.next()) {
                                String ID = userResults.getString(1);
                                String username = userResults.getString(2);
                                String password = userResults.getString(3);
                                String emailAddress = userResults.getString(4);
                                String firstName = userResults.getString(5);
                                String lastName = userResults.getString(6);
                                String accountType = userResults.getString(7);

                                initiator = new User(ID, username, password, firstName, lastName, emailAddress, accountType);
                            }
                        } catch (SQLException ex) {
                            Log.e(TAG, ex.toString());
                            ex.printStackTrace();
                        }

                        if(sender != null && recipient != null && initiator != null) {
                            Transaction tx = new Transaction(transactionID, sender, recipient, initiator,
                                    transactionAmount, memo, startDate, completionDate,
                                    inProgress, confirmed);

                            System.out.println(tx.toString());
                            try {
                                user.addTransaction(tx);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                } catch (SQLException ex) {
                    Log.e(TAG, ex.toString());
                    ex.printStackTrace();
                }
            }
        }

        connection.close(); // Close connection
        return user;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }
}
