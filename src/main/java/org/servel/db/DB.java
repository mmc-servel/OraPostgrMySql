package org.servel.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

public class DB {

    int durationMin = 0;
    String connectionString;
    String connectionUsername;
    String connectionPassword;
    String rdbms;
    Connection[] insertCon;
    Connection[] updateCon;
    Connection[] deleteCon;
    Connection[] selectCon;

    public DB(JSONObject json) throws ClassNotFoundException, SQLException {
        rdbms = json.getString("rdbms");
        System.out.println("---===" + rdbms + "===---");
        connectionString = json.getJSONObject(rdbms).getString("conn_str");
        connectionUsername = json.getJSONObject(rdbms).getString("user");
        connectionPassword = json.getJSONObject(rdbms).getString("pass");
        durationMin = Integer.parseInt(json.getString("duration_min"));
        System.out.println("---===" + connectionUsername + "===---");
        System.out.println("---===" + connectionString + "===---");
        System.out.println("---===" + durationMin + "===---");
        switch (rdbms) {
            case "postgres":
                Class.forName("org.postgresql.Driver");
                break;
            case "mysql":
                Class.forName("com.mysql.cj.jdbc.Driver"); //~45+ mins                   
                break;
            case "oracle":
                Class.forName("oracle.jdbc.OracleDriver");
                break;
            default:
                System.out.println("Wrong RDBMS value.");
                System.exit(0);
        }

        selectCon = new Connection[Integer.parseInt(json.getString("parrallel_select"))];
        for (int i = 0; i < selectCon.length; i++) {
            selectCon[i] = DriverManager.getConnection(connectionString, connectionUsername, connectionPassword);
        }        
        
        insertCon = new Connection[Integer.parseInt(json.getString("parrallel_insert"))];
        for (int i = 0; i < insertCon.length; i++) {
            insertCon[i] = DriverManager.getConnection(connectionString, connectionUsername, connectionPassword);
        }

        updateCon = new Connection[Integer.parseInt(json.getString("parrallel_update"))];
        for (int i = 0; i < updateCon.length; i++) {
            updateCon[i] = DriverManager.getConnection(connectionString, connectionUsername, connectionPassword);
        }

        deleteCon = new Connection[Integer.parseInt(json.getString("parrallel_delete"))];
        for (int i = 0; i < deleteCon.length; i++) {
            deleteCon[i] = DriverManager.getConnection(connectionString, connectionUsername, connectionPassword);
        }

    }

    public void start() {
        Select[] selectTgreads = new Select[insertCon.length];
        for (int i = 0; i < selectCon.length; i++) {
            selectTgreads[i] = new  Select(selectCon[i], i, durationMin);
            selectTgreads[i].start();
        }
        
        Insert[] insertTgreads = new Insert[insertCon.length];
        for (int i = 0; i < insertCon.length; i++) {
            insertTgreads[i] = new  Insert(insertCon[i], i, durationMin);
            insertTgreads[i].start();
        }

        Update[] updateTgreads = new Update[updateCon.length];
        for (int i = 0; i < updateCon.length; i++) {
            updateTgreads[i] = new Update(updateCon[i], i, durationMin);
            updateTgreads[i].start();
        }

        Delete[] deleteTgreads = new Delete[deleteCon.length];
        for (int i = 0; i < deleteCon.length; i++) {
            deleteTgreads[i] = new Delete(deleteCon[i], i, durationMin);
            deleteTgreads[i].start();
        }
        try {
            for (Thread th : selectTgreads) {
                th.join();
            }            
            for (Thread th : insertTgreads) {
                th.join();
            }
            for (Thread th : deleteTgreads) {
                th.join();
            }
            for (Thread th : updateTgreads) {
                th.join();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("---===TOTAL===---");
        long totalSelect = 0;       
        for (Select th : selectTgreads) {
            totalSelect = totalSelect+th.count;
        }
        System.out.println("TotalSelect="+totalSelect);
        
        long totalInsert = 0;       
        for (Insert th : insertTgreads) {
            totalInsert = totalInsert+th.count;
        }
        System.out.println("TotalInsert="+totalInsert);
        
        long totalUpdate = 0;       
        for (Update th : updateTgreads) {
            totalUpdate = totalUpdate+th.count;
        }
        System.out.println("TotalUpdate="+totalUpdate);        
        
        long totalDelete = 0;
        for (Delete th : deleteTgreads) {
            totalDelete = totalDelete+th.count;
        }
        System.out.println("TotalDelete="+totalDelete);

    }
}
