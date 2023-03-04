package org.servel.orapostgrmysql;

import java.io.File;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import org.json.JSONObject;
import java.util.Scanner;
import org.servel.db.DB;

public class Main {

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, SQLException {
        // java -jar C:\Users\svelescu\.m2\repository\org\servel\OraPostgrMySql\1.0\OraPostgrMySql-1.0-jar-with-dependencies.jar C:\work\NetBeansProjects\OraPostgrMySql\dbconfig.txt
        for (String arg : args) {
            System.out.println("Config File=" + args[0]);
        }
        String jsonText = new Scanner(new File(args[0])).useDelimiter("\\Z").next();
        JSONObject json = new JSONObject(jsonText);
        DB db = new DB(json);
        db.start();
        
        System.out.println("THE END!!!======");
    }

}
