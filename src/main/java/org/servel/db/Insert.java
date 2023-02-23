/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.servel.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author svelescu
 */
public class Insert extends Thread{

    int thread_id;
    java.sql.Connection conn;
    public long count=0;
    int durationMin=0;

    public Insert(java.sql.Connection conn, int thread_id,int durationMin) {
        this.thread_id = thread_id;
        this.conn = conn;
        this.durationMin = durationMin;
    }

    @Override
    public void run() {
        try {
            System.out.println("Insert thread " + thread_id + " started");
            PreparedStatement statement = conn.prepareStatement("insert into product_items(product_item_id,product_id,thread_id,price,radom_str) values(?,?,?,?,?)");
            statement.setInt(3, thread_id);
            long startTime = System.currentTimeMillis();
            while (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) < durationMin*60) {                
                int randomInt = (int) (Math.random() * (1000000 - 0 + 1) + 0);
                statement.setString(1, java.util.UUID.randomUUID().toString());
                statement.setInt(2, randomInt);
                statement.setDouble(4, 12.45);
                statement.setString(5, java.util.UUID.randomUUID().toString());
                statement.executeUpdate();
                count++;
            }
            System.out.println("Insert th= " + thread_id + " End. Count="+count);
        } catch (SQLException ex) {
            Logger.getLogger(Insert.class.getName()).log(Level.SEVERE, null, ex);
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
    
    
    
    
    /*
    POSTGRES
    
drop table product;
create table product(product_id numeric(10,0) primary key,
                    product_name varchar(40)
                    );

do $$begin
    for i in 0..1000000 loop
    insert into product values(i,'31ffa838-f4c6-4911-b860-2e339b'||(i-1));
    end loop;
    commit;
end$$;
select * from product;


drop table product_items;
create table product_items(product_item_id varchar(40) primary key,
                          product_id numeric(10,0) not null,
                          thread_id numeric(3,0),
                          price numeric(10,2),
                          radom_str varchar(40),
                          inserted_on  timestamp not null,
                          updated_on timestamp not null,
                          constraint product_items_fk1 FOREIGN  KEY (product_id) REFERENCES  product(product_id )
                          );
create index product_items_prod_id_idx on product_items(product_id); 



CREATE OR REPLACE FUNCTION insert_trg1()  RETURNS trigger AS $emp_stamp$
DECLARE 
BEGIN
    NEW.inserted_on:=now();
    NEW.updated_on:=now();
    RETURN NEW;
END;
$emp_stamp$ LANGUAGE plpgsql;

 CREATE TRIGGER insert_trg1x BEFORE INSERT ON product_items FOR EACH ROW EXECUTE PROCEDURE insert_trg1();

CREATE OR REPLACE FUNCTION update_trg_proc()  RETURNS trigger AS $emp_stamp$
DECLARE 
-- v_tmp integer;
BEGIN
    NEW.updated_on:=now();
    RETURN NEW;
END;
$emp_stamp$ LANGUAGE plpgsql;

 CREATE TRIGGER update_trg BEFORE INSERT ON product_items FOR EACH ROW EXECUTE PROCEDURE update_trg_proc();

ANALYZE VERBOSE product;

ANALYZE VERBOSE product_items;

SELECT pg_size_pretty( pg_total_relation_size('product') ); 94
SELECT pg_size_pretty( pg_total_relation_size('product_items'));   392 MB
SELECT pg_size_pretty(pg_table_size('product_items'));  

select count(1),max(product_id),min(product_id) from product_items;  --1587585	1000000	1                        

select t1.product_name,t2.radom_str from product t1, product_items t2 where t1.product_id=t2.product_id and t2.product_id=123;

VACUUM full;
    */
}
