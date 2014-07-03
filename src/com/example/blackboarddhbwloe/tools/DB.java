package com.example.blackboarddhbwloe.tools;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import android.util.Log;

import com.example.blackboarddhbwloe.MainActivity;

public class DB {

private static int timeout = 20000;	
private static final String url = "jdbc:mysql://193.196.4.96:3306/BBDB?connectTimeout="+timeout;
private static final String user = "BBUser";
private static final String pass = "Black12Board";
public static Connection con;



static public void setConnection (){
	Log.d("Dhbw", "DB-verbindung aufbauen");
    try {
		Class.forName("com.mysql.jdbc.Driver");
		double i = System.currentTimeMillis();
		DriverManager.setLoginTimeout(10);
	    con = DriverManager.getConnection(url, user, pass);
	    double ii = System.currentTimeMillis();
	    double iii = ii-i;
	    System.out.println("Database connection success in : "+iii);
	    MainActivity.verbindungHergestellt  = true;
	} catch (Exception e) {
		MainActivity.verbindungHergestellt  = false;
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    
}
static public void closeConnection(){
	try {
		con.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
static public ResultSet getRSFromDB(String sqlStatement) {
	
	    try {
	        
	        Statement st = con.createStatement();
	        ResultSet rs = st.executeQuery(sqlStatement);
	        
	        return rs;
	        
	    }
	    catch(Exception e) {
	        e.printStackTrace();
	        ResultSet rs = null;
	        Log.d("Dhbw","catch"+ e);
	        return rs;
	    } 
	    

	}

static public void addValueToDB(String sqlStatement) {
	
    try {
        Statement st = con.createStatement();
        st.executeUpdate(sqlStatement);
    }
    catch(Exception e) {
        e.printStackTrace();
    } 
    

}
public static void deleteAllAbosFromUser(String uSERID) {
	try {
        Statement st = con.createStatement();
        String sqlStatement = "DELETE FROM `BBDB`.`abos` WHERE userID = "+ uSERID;
        st.executeUpdate(sqlStatement);
    }
    catch(Exception e) {
        e.printStackTrace();
    } 
	
}

public static void aktualisiereLetzterAngebotsAufrufInUserTable() {
	new Thread(new Runnable() {
		@Override
		public void run() {
			if(!MainActivity.USERID.equals(""))
			{
				//���ndere das Datum des letzten Aktualisierungsvorgangs in der DB
				String sqlStatementUpdateDate = "UPDATE tblUser SET lastLogin = current_timestamp() WHERE id = "+ MainActivity.USERID;
				DB.addValueToDB(sqlStatementUpdateDate);
				ResultSet rs = DB.getRSFromDB("Select lastLogin From tblUser Where id = " + MainActivity.USERID+";");
				
				try {
					while(rs.next()){
							MainActivity.LASTLOGIN = rs.getDate("lastLogin");
							// TODO Auto-generated catch block
						}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
	}).start();
	
}

//���berladene methode um den notifications anzeigen zu lassen
public static void aktualisiereLetzterAngebotsAufrufInUserTable(final String string) {
	new Thread(new Runnable() {
		@Override
		public void run() {
			if(!MainActivity.USERID.equals(""))
			{
				//���ndere das Datum des letzten Aktualisierungsvorgangs in der DB
				String sqlStatementUpdateDate = "UPDATE tblUser SET lastLogin = '"+ string +"' WHERE id = "+ MainActivity.USERID;
				DB.addValueToDB(sqlStatementUpdateDate);
			}

		}
	}).start();
	
}
public static void entferneUserEintrag(final String inserstId) {
	new Thread(new Runnable() {
		@Override
		public void run() {
			if(!MainActivity.USERID.equals(""))
			{
				//���ndere das Datum des letzten Aktualisierungsvorgangs in der DB
				String sqlStatementUpdateDate = inserstId;
				DB.addValueToDB(sqlStatementUpdateDate);
			}

		}
	}).start();
	
}	

}
