########################MYSQL
create table product(product_id numeric(10,0) primary key, product_name varchar(40));

DROP PROCEDURE IF EXISTS Allstud;
DELIMITER //
CREATE PROCEDURE Allstud()
BEGIN
        DECLARE crs INT DEFAULT 0;
		START TRANSACTION;
        WHILE crs < 1000000 DO
            insert into product values(crs,concat('31ffa838-f4c6-4911-b860-2e339b',crs) );
            SET crs = crs + 1;
        END WHILE;
		commit;
END
//
DELIMITER ;
call Allstud();

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


DELIMITER //  
CREATE TRIGGER insert_trg1 BEFORE INSERT ON product_items FOR EACH ROW
 BEGIN
           SET NEW.inserted_on = now();
           SET NEW.updated_on = now();
END;
//
DELIMITER ;


DELIMITER //  
CREATE TRIGGER update_trg1 BEFORE UPDATE ON product_items FOR EACH ROW
BEGIN
           SET NEW.updated_on = now();
END;
//
DELIMITER ;

################################ORACLE
  
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


##################POSTGRES
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