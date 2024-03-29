/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class Amazon {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Amazon store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public Amazon(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Amazon

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public static double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      int[] columnWidths = new int[numCol];

      // First, find the maximum width required for each column
      if (outputHeader) {
         for (int i = 1; i <= numCol; i++) {
            columnWidths[i - 1] = rsmd.getColumnName(i).length();
         }
         while (rs.next()) {
            for (int i = 1; i <= numCol; i++) {
                  int length = rs.getString(i).trim().length();
                  if (length > columnWidths[i - 1]) {
                     columnWidths[i - 1] = length;
                  }
            }
         }
         rs.beforeFirst(); // Reset the cursor to the beginning
      }

      // Next, print out the headers with appropriate spacing
      if (outputHeader) {
         for (int i = 1; i <= numCol; i++) {
            System.out.print(String.format("%-" + columnWidths[i - 1] + "s\t", rsmd.getColumnName(i)));
         }
         System.out.println();
         outputHeader = false;
      }

      // Finally, print out the rows with padding to align under the headers
      while (rs.next()) {
         for (int i = 1; i <= numCol; i++) {
            String value = rs.getString(i).trim();
            System.out.print(String.format("%-" + columnWidths[i - 1] + "s\t", value));
         }
         System.out.println();
         ++rowCount;
      }//end while

      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Amazon.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Amazon esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Amazon object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Amazon (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");

                //the following functionalities basically used by managers
                System.out.println("5. Update Product");
                System.out.println("6. View 5 recent Product Updates Info");
                System.out.println("7. View 5 Popular Items");
                System.out.println("8. View 5 Popular Customers");
                System.out.println("9. Place Product Supply Request to Warehouse");
		System.out.println("10. Admin options");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql); break;
                   case 4: viewRecentOrders(esql); break;
                   case 5: updateProduct(esql); break;
                   case 6: viewRecentUpdates(esql); break;
                   case 7: viewPopularProducts(esql); break;
                   case 8: viewPopularCustomers(esql); break;
                   case 9: placeProductSupplyRequests(esql); break;
                   case 10: viewAndUpdateInfo(esql); break;

                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   //helper pair class
   public static class Pair<K, V> {

      private final K left;
      private final V right;

      public Pair(K left, V right) {
         this.left = left;
         this.right = right;
      }

      public K getLeft() {
         return left;
      }

      public V getRight() {
         return right;
      }
   }


   //data is a list of lists, whose first dimension is each entry, and second dimension is size 3, the ID, latitude, longitude
   //latitude and longitude is the refernece for measurement
   public static int SelectByDistance(String idName, List<List<String>> data, double latitude, double longitude, int limit){
      List<Pair<Integer, Double>> locations = new ArrayList<Pair<Integer, Double>>();
      List<Integer> ids = new ArrayList<Integer>(); //a little redundant but much more efficient to search for valid selection
      for (int i = 0; i < data.size(); i++)
         locations.add(new Pair<Integer, Double>(Integer.parseInt(data.get(i).get(0)), calculateDistance(latitude, longitude, Double.parseDouble(data.get(i).get(1)), Double.parseDouble(data.get(i).get(2)))));
      Collections.sort(locations, Comparator.comparing(p -> p.getRight()));
      System.out.println(String.format("%-" + (idName.length() + 1) + "s%-10s", idName, "Distance"));
      for (int i = 0; i < locations.size(); i++){
         ids.add(locations.get(i).getLeft());
         if (limit == -1 || i < limit)
            System.out.println(String.format("%-" + (idName.length() + 1) + "d%-10.2f", locations.get(i).getLeft(), locations.get(i).getRight()));
      }

      int sel = readChoice();
      while (!ids.contains(sel)){
         System.out.println("Invalid selection. Please enter an id among the listed options.");
         sel = readChoice();
      }
      return sel;
   }

   public static int SelectByDistance(String idName, List<List<String>> data, double latitude, double longitude) { return SelectByDistance(idName, data, latitude, longitude, -1); }
   /*
    * Creates a new user
    **/
   public static void CreateUser(Amazon esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="Customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   
   
   private static String curr_user_id = null;

   public static String getUserId() {
        return curr_user_id;
    }


   public static String LogIn(Amazon esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();

         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
         List<List<String>> result = esql.executeQueryAndReturnResult(query);

         if (!result.isEmpty()) {
		 curr_user_id = result.get(0).get(0);
		 return name;
            } else {
                return null; // Login failed
            }
      }catch(Exception e){
	      System.err.println (e.getMessage ());
	      return null;
      }
   }//end

// Rest of the functions definition go in here

   public static void viewStores(Amazon esql) {
   try{ 
         String id = curr_user_id;
	      String query = String.format("SELECT S.*, U.latitude, U.longitude FROM Store S, Users U WHERE U.userID = '%s'", id);
         List<List<String>> data = esql.executeQueryAndReturnResult(query);
         //storeID,latitude,longitude,managerID,dateEstablished, userLatitude, userLongitude
         //we have all stores, now show within 30mi
         double limit = 30;
         //make the last index 
         for (int i = 0; i < data.size(); i++){
            double dist = calculateDistance(Double.parseDouble(data.get(i).get(5)), Double.parseDouble(data.get(i).get(6)), Double.parseDouble(data.get(i).get(1)), Double.parseDouble(data.get(i).get(2)));
            data.get(i).add(String.format("%.2f", dist));
         }
         Collections.sort(data, Comparator.comparing(p -> Double.parseDouble(p.get(7))));
         System.out.println(String.format("%-10s%-10s%-15s%-15s", "Store ID", "Distance", "Manager ID", "Date Established"));
         for (int i = 0; i < data.size(); i++){
            if (Double.parseDouble(data.get(i).get(7)) > limit) break; //stop checking, since we're sorted if this one is too far away, all future ones are too
            System.out.println(String.format("%-10s%-10s%-15s%-15s", data.get(i).get(0), data.get(i).get(7) + "mi", data.get(i).get(3), data.get(i).get(4)));
         }

      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

   public static void viewProducts(Amazon esql) {
   try{
         System.out.println("Please select a store (any valid ID may be entered, but these are the nearest 10 stores)");
         String myId = esql.getUserId();
         List<List<String>> stores = esql.executeQueryAndReturnResult("SELECT storeId, latitude, longitude FROM STORE");
         List<List<String>> myloc = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM USERS where userId = %s", myId));
         int storeId = SelectByDistance("Store ID", stores, Double.parseDouble(myloc.get(0).get(0)), Double.parseDouble(myloc.get(0).get(1)), 10);
         String query = String.format("SELECT productName, numberOfUnits, pricePerUnit FROM Product P WHERE P.storeID = '%s' ORDER BY productName", storeId);
         esql.executeQueryAndPrintResult(query);
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

   public static void placeOrder(Amazon esql) {
      try{
         System.out.println("Please select a store (any valid ID may be entered, but these are the nearest 10 stores)");
         String myId = esql.getUserId();
         List<List<String>> stores = esql.executeQueryAndReturnResult("SELECT storeId, latitude, longitude FROM STORE");
         List<List<String>> myloc = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM USERS where userId = %s", myId));
         int storeId = SelectByDistance("Store ID", stores, Double.parseDouble(myloc.get(0).get(0)), Double.parseDouble(myloc.get(0).get(1)), 10);

         esql.executeQueryAndPrintResult(String.format("SELECT productName, numberOfUnits, pricePerUnit FROM PRODUCT WHERE storeId = %s ORDER BY productName", storeId));

         System.out.print("\tEnter a product name: ");
         String prod_name = in.readLine();
         while (esql.executeQuery(String.format("SELECT productName FROM PRODUCT WHERE storeId = %d AND productName = '%s'", storeId, prod_name)) == 0){
            System.out.print("This store does not have this product. Please enter a valid product name: ");
            prod_name = in.readLine();
         }

         System.out.print("\tEnter number of units: ");
         int nUnits = Integer.parseInt(in.readLine());

         String query = String.format("SELECT * FROM Product P WHERE P.storeID = %d AND P.productName = '%s' AND P.numberOfUnits >= %d", storeId, prod_name, nUnits);
         int rows = esql.executeQuery(query);
               
         if(rows > 0){
            esql.executeUpdate(String.format("INSERT INTO ORDERS (orderNumber, customerID, storeID, productName, unitsOrdered, orderTime) VALUES (nextval('orders_orderNumber_seq'), %s, %s, '%s', %s, TIMESTAMP '%s')", myId, storeId, prod_name, nUnits, new java.sql.Timestamp(System.currentTimeMillis()).toString().split("\\.")[0]));
            esql.executeUpdate(String.format("UPDATE Product SET numberOfUnits = numberOfUnits - %d WHERE Product.storeID = %d AND Product.productName = '%s'", nUnits, storeId, prod_name));

            System.out.println(String.format("Congratulations, you purchased %d units of '%s' from store %d", nUnits, prod_name, storeId));
         } else {
            System.out.println("Not enough units available.");
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

   //helper function to narrow 1 choice if you manage multiple
   private static void updateProduct(Amazon esql, String myId, int storeId){
      try{
         esql.executeQueryAndPrintResult(String.format("SELECT productName, numberOfUnits, pricePerUnit FROM PRODUCT WHERE storeId = %s ORDER BY productName", storeId));
         System.out.print("\tEnter the name of a product to modify: ");
         String prod_name = in.readLine();
         while (esql.executeQuery(String.format("SELECT productName FROM PRODUCT WHERE storeId = %d AND productName = '%s'", storeId, prod_name)) == 0){
            System.out.print("This store does not have this product. Please enter a valid product name: ");
            prod_name = in.readLine();
         }

         System.out.print("\tEnter updated number of units: ");
         int nUnits = Integer.parseInt(in.readLine());

         System.out.print("\tEnter updated price per unit: ");
         int ppu = Integer.parseInt(in.readLine());

         //updateNumber,managerID,storeID,productName,updatedOn
         esql.executeUpdate(String.format("INSERT INTO PRODUCTUPDATES (updateNumber,managerID,storeID,productName,updatedOn) VALUES (nextval('productupdates_updateNumber_seq'), %s, %d, '%s', TIMESTAMP '%s')", myId, storeId, prod_name, new java.sql.Timestamp(System.currentTimeMillis()).toString().split("\\.")[0]));
         //storeID,productName,numberOfUnits,pricePerUnit
         esql.executeUpdate(String.format("UPDATE Product SET numberOfUnits = %d, pricePerUnit = %d WHERE Product.storeID = %d AND Product.productName = '%s'", nUnits, ppu, storeId, prod_name));

         System.out.println(String.format("%s at store %d now has %d units priced at %d each", prod_name, storeId, nUnits, ppu));
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }

   //from a manager
   public static void updateProduct(Amazon esql) {
   try{
         String myId = esql.getUserId();
         int rows = esql.executeQuery(String.format("SELECT * FROM USERS U WHERE U.type = 'manager' AND U.userID = '%s'", myId));
         if(rows > 0){ 
            System.out.println("Please select a store among those you manage:");
            List<List<String>> stores = esql.executeQueryAndReturnResult(String.format("SELECT storeId, latitude, longitude FROM STORE WHERE managerId = %s", myId));
            if (stores.size() > 1){
               List<List<String>> myloc = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM USERS where userId = %s", myId));
               int storeId = SelectByDistance("Store ID", stores, Double.parseDouble(myloc.get(0).get(0)), Double.parseDouble(myloc.get(0).get(1)));

               updateProduct(esql, myId, storeId);
            } else if (stores.size() == 1)
               updateProduct(esql, myId, Integer.parseInt(stores.get(0).get(0)));
            else
               System.out.println("You do not manage any stores!");
            
         } else{
            System.out.println("Access denied: Manager access only!");
         }
         
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }



   //manager can check their own store
   public static void viewRecentUpdates(Amazon esql) {
    try{
         String c_id = esql.getUserId();
	      String q1 = String.format("SELECT * FROM USERS U WHERE U.type = 'manager' AND U.userID = '%s'", c_id);
         int rows = esql.executeQuery(q1);
         if(rows > 0){
            System.out.println("Please select a store among those you manage:");
            List<List<String>> stores = esql.executeQueryAndReturnResult(String.format("SELECT storeId, latitude, longitude FROM STORE WHERE managerId = %s", c_id));
            List<List<String>> myloc = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM USERS where userId = %s", c_id));
            int storeId = SelectByDistance("Store ID", stores, Double.parseDouble(myloc.get(0).get(0)), Double.parseDouble(myloc.get(0).get(1)));
            System.out.println("Last 5 updates at this store: ");
            String query = String.format("SELECT * FROM (SELECT * FROM ProductUpdates PU WHERE PU.managerID = '%s' AND PU.storeId = %d ORDER BY updatedOn DESC LIMIT 5) AS recent_updates ORDER BY updatedOn DESC", c_id, storeId);
            esql.executeQueryAndPrintResult(query);
         } else {
                 System.out.println("Access denied: manager access only.");
         }
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }



   //THIS IS THE ADMIN MENU
   public static void viewAndUpdateInfo(Amazon esql) {
    try {
        String c_id = esql.getUserId();
        String q1 = String.format("SELECT * FROM USERS U WHERE U.type = 'admin' AND U.userID = '%s'", c_id);
        int rows = esql.executeQuery(q1);
        if (rows > 0) {
            System.out.println("1. View and update user information");
            System.out.println("2. View and update product information");
            System.out.print("Enter your choice: ");
            String choice = in.readLine();
            switch (choice) {
                case "1":
                    viewAndUpdateUsers(esql);
                    break;
                case "2":
                    viewAndUpdateProducts(esql);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } else {
            System.out.println("Access denied: admin access only.");
        }
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
}

//ADMIN MENU SELECTION
public static void viewAndUpdateUsers(Amazon esql) {
    try {
        System.out.print("\tEnter 1 to view users or 2 to update users: ");
	String input = in.readLine();
	if("1".equals(input)){
		System.out.print("\tEnter user id to view: ");
		String input_id = in.readLine();
		String q1 = String.format("SELECT * FROM USERS U WHERE U.userID = '%s'", input_id);
		esql.executeQueryAndPrintResult(q1);
		esql.executeUpdate(q1);
	} else if ("2".equals(input)){
		System.out.print("\tEnter user id to update: ");
                String input_id2 = in.readLine();
		System.out.print("\tEnter updated name of user: ");
                String new_name = in.readLine();
		System.out.print("\tEnter updated password of user: ");
                String new_password = in.readLine();
		System.out.print("\tEnter updated latitude of user: ");
                String new_lat = in.readLine();
		System.out.print("\tEnter updated longitude of user: ");
                String new_long = in.readLine();
		System.out.print("\tEnter updated type of user: ");
                String new_type = in.readLine().toLowerCase();
                String q2 = String.format("UPDATE Users SET name = '%s', password = '%s', latitude = '%s', longitude = '%s', type = '%s' WHERE Users.userID = '%s'", new_name, new_password, new_lat, new_long, new_type, input_id2);
                esql.executeUpdate(q2);
	} else {
		System.out.println("Invalid choice.");
	}
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
}

//ADMIN MENU SELECTION
public static void viewAndUpdateProducts(Amazon esql) {
    try {
        System.out.print("\tEnter 1 to view products or 2 to update products: ");
        String input = in.readLine();
        if("1".equals(input)){
                System.out.print("\tEnter product name to view: ");
                String input_name = in.readLine();
                String q1 = String.format("SELECT * FROM Product P WHERE P.productName = '%s'", input_name);
                esql.executeQueryAndPrintResult(q1);
                esql.executeUpdate(q1);
        } else if ("2".equals(input)){
                System.out.print("\tEnter store id of product to update: ");
                String input_id = in.readLine();
		System.out.print("\tEnter product name of product to update: ");
                String input_name2 = in.readLine();
                System.out.print("\tEnter updated store id: ");
                String new_id = in.readLine();
                System.out.print("\tEnter updated product name: ");
                String new_name = in.readLine();
                System.out.print("\tEnter updated number of units: ");
                String new_units = in.readLine();
                System.out.print("\tEnter updated price per unit: ");
                String new_price = in.readLine();
                String q2 = String.format("UPDATE Product SET storeID = '%s', productName = '%s', numberOfUnits = '%s', pricePerUnit = '%s' WHERE Product.storeID = '%s' AND Product.productName = '%s'", new_id, new_name, new_units, new_price, input_id, input_name2);
                esql.executeUpdate(q2);
                List<List<String>> mgrId = esql.executeQueryAndReturnResult(String.format("SELECT managerID FROM STORE WHERE storeId = %s", new_id));
                esql.executeUpdate(String.format("INSERT INTO PRODUCTUPDATES (updateNumber, managerID, storeID, productName, updatedOn) VALUES (nextval('productupdates_updateNumber_seq'), %s, %s, '%s', TIMESTAMP '%s')", mgrId.get(0).get(0), new_id, new_name, new java.sql.Timestamp(System.currentTimeMillis()).toString().split("\\.")[0]));
        } else {
                System.out.println("Invalid choice.");
        }
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
}
   

   //customers see 5 recent orders
   //mangers are prompted if they would like to see their orders or all orders at their managed store. If they manage multiple, they are prompted to select 1.
   public static void viewRecentOrders(Amazon esql) {
      try {
         String c_id = esql.getUserId();
         String qC = String.format("SELECT * FROM USERS U WHERE U.type = 'customer' AND U.userID = '%s'", c_id);
         String qM = String.format("SELECT * FROM USERS U WHERE U.type = 'manager' AND U.userID = '%s'", c_id);
         if (esql.executeQuery(qC) > 0) { //customer access
            esql.executeQueryAndPrintResult(String.format("SELECT storeID, productName, unitsOrdered, orderTime FROM ORDERS O WHERE O.customerID = %s ORDER BY orderTime DESC LIMIT 5", c_id));
         } else if (esql.executeQuery(qM) > 0) { //manager access
            System.out.println("Would you like to see your personal orders, or order information for your store(s)?");
            System.out.println("1. See my Orders");
            System.out.println("2. See my Store(s) Orders");
            int sel = readChoice();
            switch(sel){
               case 1:
                  esql.executeQueryAndPrintResult(String.format("SELECT storeID, productName, unitsOrdered, orderTime FROM ORDERS O WHERE O.customerID = %s ORDER BY orderTime DESC LIMIT 5", c_id));
                  break;
               case 2:
               //get the storeId of this manager
               List<List<String>> storesManaged = esql.executeQueryAndReturnResult(String.format("SELECT storeID, latitude, longitude FROM STORE WHERE managerID = %s", c_id));
               List<List<String>> myloc = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM USERS WHERE userId = %s", c_id));
               //for each store managed
               if (storesManaged.size() > 1){ //choose a store
                  sel = SelectByDistance("Store ID", storesManaged, Double.parseDouble(myloc.get(0).get(0)), Double.parseDouble(myloc.get(0).get(1)));
                  esql.executeQueryAndPrintResult(String.format("SELECT O.orderNumber, U.name, O.productName, O.unitsOrdered, O.orderTime FROM ORDERS O, USERS U WHERE O.storeID = %s AND O.customerID = U.userID ORDER BY orderTime DESC LIMIT 5", sel));
               }
               else if (storesManaged.size() == 1){ //1 store
                  //print out the orderId, customerName, storeID, productName, date for each order
                  esql.executeQueryAndPrintResult(String.format("SELECT O.orderNumber, U.name, O.productName, O.unitsOrdered, O.orderTime FROM ORDERS O, USERS U WHERE O.storeID = %s AND O.customerID = U.userID ORDER BY orderTime DESC LIMIT 5", storesManaged.get(0).get(0)));
               } else
                  System.out.println("You are not the manager of any store. No orders to display.");
               break;
            }
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   //manager only, 5 popular products in their store(s) (based on order count of product)
   public static void viewPopularProducts(Amazon esql) {
      try {
         String c_id = esql.getUserId();
         String qM = String.format("SELECT * FROM USERS U WHERE U.type = 'manager' AND U.userID = '%s'", c_id);
         if (esql.executeQuery(qM) > 0){
            List<List<String>> storesManaged = esql.executeQueryAndReturnResult(String.format("SELECT storeID, latitude, longitude FROM STORE WHERE managerID = %s", c_id));
            //for each store managed
            if (storesManaged.size() > 1){ //choose a store
               List<List<String>> myloc = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM USERS WHERE userId = %s", c_id));
               int sel = SelectByDistance("Store ID", storesManaged, Double.parseDouble(myloc.get(0).get(0)), Double.parseDouble(myloc.get(0).get(1)));
               esql.executeQueryAndPrintResult(String.format("SELECT productName, SUM(unitsOrdered) as Sold FROM ORDERS WHERE storeId = %s GROUP BY productName ORDER BY Sold DESC LIMIT 5", sel));
            }
            else if (storesManaged.size() == 1){ //1 store
               //print out the orderId, customerName, storeID, productName, date for each order
               //display 5 most pop. products
               esql.executeQueryAndPrintResult(String.format("SELECT productName, SUM(unitsOrdered) as Sold FROM ORDERS WHERE storeId = %s GROUP BY productName ORDER BY Sold DESC LIMIT 5", storesManaged.get(0).get(0)));
            } else
               System.out.println("You are not the manager of any store. No orders to display.");
         } else{
            System.out.println("Access denied: manager access only.");
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   //manager only, view top 5 customers info (who placed most orders in their store)
   public static void viewPopularCustomers(Amazon esql) {
      try {
         String c_id = esql.getUserId();
         String qM = String.format("SELECT * FROM USERS U WHERE U.type = 'manager' AND U.userID = %s", c_id);
         if (esql.executeQuery(qM) > 0){
            List<List<String>> storesManaged = esql.executeQueryAndReturnResult(String.format("SELECT storeID, latitude, longitude FROM STORE WHERE managerID = %s", c_id));
            //for each store managed
            if (storesManaged.size() > 1){ //choose a store
               List<List<String>> myloc = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM USERS WHERE userId = %s", c_id));
               int sel = SelectByDistance("Store ID", storesManaged, Double.parseDouble(myloc.get(0).get(0)), Double.parseDouble(myloc.get(0).get(1)));
               esql.executeQueryAndPrintResult(String.format("SELECT U.name, SUM(O.unitsOrdered) AS Purchased FROM ORDERS O JOIN PRODUCT P ON O.productName = P.productName JOIN USERS U ON O.customerId = U.userId WHERE P.storeId = %s GROUP BY U.userId, U.name ORDER BY Purchased DESC LIMIT 5", sel));
            }
            else if (storesManaged.size() == 1){ //1 store
               esql.executeQueryAndPrintResult(String.format("SELECT U.name, SUM(O.unitsOrdered) AS Purchased FROM ORDERS O JOIN PRODUCT P ON O.productName = P.productName JOIN USERS U ON O.customerId = U.userId WHERE P.storeId = %s GROUP BY U.userId, U.name ORDER BY Purchased DESC LIMIT 5", storesManaged.get(0).get(0)));
            } else
               System.out.println("You are not the manager of any store. No orders to display.");
         } else{
            System.out.println("Access denied: manager access only.");
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

   //helper function, called once we determine a storeId
   private static void placeProductSupplyRequests(Amazon esql, int mgrId, int storeId, double latitude, double longitude){
      try{
         //productName (display sorted by lowest quantity), number of units needed, warehouseId (sort by distance)
         List<List<String>> products = esql.executeQueryAndReturnResult(String.format("SELECT productName, numberOfUnits FROM PRODUCT WHERE storeId = %s ORDER BY numberOfUnits ASC", storeId));
         String pName;
         int nUnits;
         int whId;
         System.out.println("Please select a product to order:");
         for (int i = 0; i < products.size(); i++){
            System.out.println(String.format("%2d. %s: %s Units on hand.", i+1, products.get(i).get(0), products.get(i).get(1)));
         }
         int sel = readChoice();
         while (sel < 1 || sel > products.size()){
            System.out.println("Invalid selection. Please enter a number in range.");
            sel = readChoice();
         }
         pName = products.get(sel-1).get(0);

         System.out.println("Choose a warehouse by ID");
         System.out.println("Warehouse ID | Distance estimate from your store");
         //wareHouseID,area,latitude,longitude
         List<List<String>> whFull = esql.executeQueryAndReturnResult(String.format("SELECT wareHouseID,latitude,longitude FROM WAREHOUSE"));
         //id, dist
         whId = SelectByDistance("Warehouse ID", whFull, latitude, longitude);
         System.out.println(String.format("Warehouse ID: %d", whId));

         System.out.println("How many units would you like?");
         nUnits = readChoice();

         //requestNumber,managerID,warehouseID,storeID,productName,unitsRequested
         //UPDATE the Product table
         esql.executeUpdate(String.format("UPDATE PRODUCT SET numberOfUnits = numberOfUnits + %d WHERE PRODUCT.storeId = %s AND PRODUCT.productName = '%s'", nUnits, storeId, pName));
         //UPDATE the product request table
         //use sequence productsupplyrequests_requestNumber_seq
         
         esql.executeUpdate(String.format("INSERT INTO PRODUCTSUPPLYREQUESTS (requestNumber,managerID,warehouseID,storeID,productName,unitsRequested) VALUES (nextval('productsupplyrequests_requestNumber_seq'), %s, %s, %s, '%s', %s)", mgrId, whId, storeId, pName, nUnits));

         System.out.println(String.format("Warehouse %d delivered %d units of %s to store %d!", whId, nUnits, pName.trim(), storeId));
      } catch (Exception e){
         System.err.println(e.getMessage());
      }
   }

   //manager only - input storeID, productName, number of units needed, warehouseID of supplier. assume all locations have enough
   public static void placeProductSupplyRequests(Amazon esql) {
      try {
         String c_id = esql.getUserId();
         String qM = String.format("SELECT * FROM USERS U WHERE U.type = 'manager' AND U.userID = %s", c_id);
         if (esql.executeQuery(qM) > 0){
            List<List<String>> storesManaged = esql.executeQueryAndReturnResult(String.format("SELECT storeID, latitude, longitude FROM STORE WHERE managerID = %s", c_id));
            List<List<String>> myloc = esql.executeQueryAndReturnResult(String.format("SELECT latitude, longitude FROM USERS WHERE userId = %s", c_id));
            //for each store managed
            if (storesManaged.size() > 1){ //choose a store
               System.out.println("Select a store among those you manage");
               int storeId = SelectByDistance("Store ID", storesManaged, Double.parseDouble(myloc.get(0).get(0)), Double.parseDouble(myloc.get(0).get(1)));
               int index;
               for (index = 0; index < storesManaged.size(); index++)
                  if(storeId == Integer.parseInt(storesManaged.get(index).get(0))) break;

               placeProductSupplyRequests(esql, Integer.parseInt(c_id), storeId, Double.parseDouble(storesManaged.get(index).get(1)), Double.parseDouble(storesManaged.get(index).get(2)));
            }
            else if (storesManaged.size() == 1){ //1 store
               placeProductSupplyRequests(esql, Integer.parseInt(c_id), Integer.parseInt(storesManaged.get(0).get(0)), Double.parseDouble(storesManaged.get(0).get(1)), Double.parseDouble(storesManaged.get(0).get(2)));
            } else
               System.out.println("You are not the manager of any store. No orders to display.");
         } else{
            System.out.println("Access denied: manager access only.");
         }
      } catch (Exception e) {
         System.err.println(e.getMessage());
      }
   }

}//end Amazon