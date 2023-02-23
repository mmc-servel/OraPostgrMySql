
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
public class Update  extends Thread {

    int thread_id;
    java.sql.Connection conn;
    public long count=0;
    int durationMin=0;

    public Update(java.sql.Connection conn, int thread_id,int durationMin) {
        this.thread_id = thread_id;
        this.conn = conn;
        this.durationMin = durationMin;
    }

    @Override
    public void run() {
        try {
            System.out.println("Update thread " + thread_id + " started");
            PreparedStatement statement = conn.prepareStatement("update product_items set price=?, radom_str=? where product_id=?");
            long startTime = System.currentTimeMillis();
            while (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime) < durationMin*60) {                
                int randomInt = (int) (Math.random() * (1000000 - 0 + 1) + 0);
                statement.setDouble(1, 22.22);
                statement.setString(2, java.util.UUID.randomUUID().toString());
                statement.setInt(3, randomInt);
                statement.executeUpdate();
                count++;
            }
            System.out.println("Update th= " + thread_id + " End. Count="+count);
        } catch (SQLException ex) {
            Logger.getLogger(Update.class.getName()).log(Level.SEVERE, null, ex);
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
                          constraint product_items_fk1 FOREIGN  KEY (product_id) REFERENCES  product(product_id )
                          );
create index product_items_prod_id_idx on product_items(product_id);             
       
     */
}
