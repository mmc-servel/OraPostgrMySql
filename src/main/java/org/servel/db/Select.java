/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.servel.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author svelescu
 */
public class Select extends Thread{

    int thread_id;
    java.sql.Connection conn;
    public long count=0;
    int durationMin=0;

    public Select(java.sql.Connection conn, int thread_id,int durationMin) {
        this.thread_id = thread_id;
        this.conn = conn;
        this.durationMin = durationMin;
    }

    @Override
    public void run() {
        try {
            System.out.println("Select thread " + thread_id + " started");
            PreparedStatement statement = conn.prepareStatement("select t1.product_name,t2.radom_str from product t1, product_items t2 where t1.product_id=t2.product_id and t2.product_id=?");
            long startTime = System.currentTimeMillis();
            String theStr;
            while (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) < durationMin*60) {                
                int randomInt = (int) (Math.random() * (1000000 - 0 + 1) + 0);
                statement.setInt(1, randomInt);
                ResultSet rs = statement.executeQuery();
                if (rs.next()){
                theStr=rs.getString(2);
                if(count>20000000000L){//just for the optimizer not to ignore execution
                    System.out.println("DUMMY="+theStr);
                }
                }
                //rs.close();
                count++;
            }
            System.out.println("Select th= " + thread_id + " End. Count="+count);
        } catch (SQLException ex) {
            Logger.getLogger(Select.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /*
    Oracle
    drop table product;
create table product(product_id number(10,0) primary key,
                    product_name varchar2(40)
                    );

begin
    for i in 0..1000000 loop
    insert into product values(i,'31ffa838-f4c6-4911-b860-2e339b'||(i-1));
    end loop;
    commit;
end;
/

drop table product_items;
create table product_items(product_item_id varchar2(40) primary key,
                          product_id number(10,0) not null,
                          thread_id number(3,0),
                          price number(10,2),
                          radom_str varchar2(40),
                          inserted_on date not null,
                          updated_on date not null,
                          constraint product_items_fk1 FOREIGN  KEY (product_id) REFERENCES  product(product_id )
                          );
create index product_items_prod_id_idx on product_items(product_id);             
create or replace trigger trigger1 
before insert on product_items FOR EACH ROW   
begin
  :new.inserted_on:=sysdate;
  :new.updated_on:=sysdate;
end; 

create or replace trigger trigger2 
before update on product_items FOR EACH ROW   
begin
  :new.updated_on:=sysdate;
end;           
       
     */
}
