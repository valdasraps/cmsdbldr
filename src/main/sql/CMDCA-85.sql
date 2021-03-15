ALTER TABLE REQUESTS DROP CONSTRAINT REQUESTS_CHK1;
ALTER TABLE REQUESTS ADD CONSTRAINT REQUESTS_CHK1 CHECK (REQ_STATUS in ('OPEN','CLOSED','CANCELED','FULFILLED')) ENABLE;

ALTER TABLE SHIPMENT_ITEMS ADD (SHI_IS_PAID CHAR DEFAULT 'F' NOT NULL);
ALTER TABLE SHIPMENT_ITEMS ADD CONSTRAINT SHIPMENT_ITEMS_CHK1 CHECK (SHI_IS_PAID in ('F','T')) ENABLE;

ALTER TABLE REQUESTS DROP COLUMN PAID_BY_INSTITUTION;

ALTER TABLE SHIPMENT_ITEMS_HST ADD (SHI_IS_PAID CHAR);
ALTER TABLE REQUESTS_HST DROP COLUMN PAID_BY_INSTITUTION;

create or replace TRIGGER TR_UPD_REQUESTS
    BEFORE UPDATE OR DELETE
    ON REQUESTS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
    DECLARE
        seqPkId number(38,0);
        recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
        recordChangeUser VARCHAR2(50 );
    BEGIN
        SELECT ANY_REQUESTS_HISTORY_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

        if :old.RECORD_LASTUPDATE_TIME is null then
            recordChangeTime:= :OLD.RECORD_INSERTION_TIME;
        else
            recordChangeTime:= :old.RECORD_LASTUPDATE_TIME;
    end if;

    if :old.RECORD_LASTUPDATE_USER is null then
        recordChangeUser:= :OLD.RECORD_INSERTION_USER;
    else
        recordChangeUser:= :old.RECORD_LASTUPDATE_USER;
    end if;

    if UPDATING then

      -- protect the original record data
      :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
      :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
      :new.REQ_ID :=  :OLD.REQ_ID;

     -- update last change record info, if needed
      :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

      if :new.RECORD_LASTUPDATE_USER is null then
        :new.RECORD_LASTUPDATE_USER := USER;
      end if;

    end if;

    insert into REQUESTS_HST
    (
        HISTORY_RECORD_ID,
        REQ_ID,
        REQ_NAME,
        REQ_LOCATION_ID,
        REQ_STATUS,
        COMMENT_DESCRIPTION,
        REQ_DATE,
        REQ_PERSON,
        RECORD_INSERTION_TIME,
        RECORD_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER
    )
    values
    (
        seqPkId,
        :OLD.REQ_ID,
        :OLD.REQ_NAME,
        :OLD.REQ_LOCATION_ID,
        :OLD.REQ_STATUS,
        :OLD.COMMENT_DESCRIPTION,
        :OLD.REQ_DATE,
        :OLD.REQ_PERSON,
        sysdate,
        :OLD.RECORD_INSERTION_USER,
        recordChangeTime,
        recordChangeUser
    );

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END ;
/

create or replace TRIGGER TR_UPD_SHIPMENT_ITM
    BEFORE UPDATE OR DELETE
    ON SHIPMENT_ITEMS
    REFERENCING OLD AS OLD NEW AS NEW
    FOR EACH ROW
DECLARE

    seqPkId number(38,0);
    recordChangeTime TIMESTAMP(6) WITH TIME ZONE;
    recordChangeUser VARCHAR2(50 );
BEGIN
      SELECT ANY_SHIPMENT_ITM_HST_ID_SEQ.NEXTVAL INTO seqPkId FROM dual;

   if :old.RECORD_LASTUPDATE_TIME is null then
       recordChangeTime:= :OLD.RECORD_INSERTION_TIME;
   else
       recordChangeTime:= :old.RECORD_LASTUPDATE_TIME;

end if;


   if :old.RECORD_LASTUPDATE_USER is null then
       recordChangeUser:= :OLD.RECORD_INSERTION_USER;
   else
       recordChangeUser:= :old.RECORD_LASTUPDATE_USER;
   end if;

   if UPDATING then

       -- protect the original record data
       :new.RECORD_INSERTION_TIME := :OLD.RECORD_INSERTION_TIME;
       :new.RECORD_INSERTION_USER := :OLD.RECORD_INSERTION_USER;
       :new.SHI_ID :=  :OLD.SHI_ID;

       -- update last change record info, if needed
           :new.RECORD_LASTUPDATE_TIME  := SYSDATE;

       if :new.RECORD_LASTUPDATE_USER is null then
           :new.RECORD_LASTUPDATE_USER := USER;
       end if;

    end if;

    insert into SHIPMENT_ITEMS_HST
    (
        HISTORY_RECORD_ID,
        SHI_ID,
        SHI_SHP_ID,
        SHI_PART_ID,
        SHI_RQI_ID,
        SHI_IS_PAID,
        COMMENT_DESCRIPTION,
        RECORD_INSERTION_TIME,
        RECORD_INSERTION_USER,
        RECORD_LASTUPDATE_TIME,
        RECORD_LASTUPDATE_USER
    )
    values
    (
        seqPkId,
        :OLD.SHI_ID,
        :OLD.SHI_SHP_ID,
        :OLD.SHI_PART_ID,
        :OLD.SHI_RQI_ID,
        :OLD.SHI_IS_PAID,
        :OLD.COMMENT_DESCRIPTION,
        sysdate,
        :OLD.RECORD_INSERTION_USER,
        recordChangeTime,
        recordChangeUser
    );

   EXCEPTION
     WHEN OTHERS THEN
       -- Consider logging the error and then re-raise
       RAISE;
END;
/

create or replace PACKAGE BODY PCK_TRACKING AS

  PROCEDURE do_shp_status_update(shp_id NUMBER) AS
  BEGIN
    
    
    /**
    Move request to FULFILLED state if all requested items have been received by the recipient.
    **/
    update 
        requests r
    set 
        req_status = 'FULFILLED'
    where
        r.req_id in (
            select
                rqi_req_id
            from 
                request_items 
            where 
                rqi_id in (
                    select
                        shi_rqi_id 
                    from 
                        shipment_items 
                    where 
                        shi_shp_id = shp_id and 
                        shi_rqi_id is not null)
        ) and
        r.req_id in (
            select req_id from request_stats where requested = received
        ) and
        r.req_status = 'OPEN';
    
END do_shp_status_update;
/

PROCEDURE do_shp_item_payment_update(req_id NUMBER) AS
BEGIN

    /**
    Move Request to FULFILLED state if state is not CLOSED anymore
    **/
    update requests r
    set req_status = 'FULFILLED'
    where
        r.req_id = req_id and
        r.req_id in (
            select req_id from request_stats where requested = received and requested > is_paid
        ) and
        r.req_status = 'CLOSED';
    
    if SQL%rowcount = 0 then
    
        /**
        Move Request to CLOSED if all items are shipped and paid
        **/
        update requests r
        set req_status = 'CLOSED'
        where
            r.req_id = req_id and
            r.req_id in (
                select req_id from request_stats where requested = received and requested = is_paid
            ) and
            r.req_status = 'FULFILLED';
    
    end if;
    
  END do_shp_item_payment_update;

END PCK_TRACKING;
/


CREATE OR REPLACE FORCE VIEW "CMS_TST_TEST_CONSTRUCT"."REQUEST_STATS" ("REQ_ID", "REQUESTED", "IS_PAID", "NOT_IS_PAID", "PACKAGING", "SHIPPED", "RECEIVED", "CANCELED") AS 
SELECT
    A.RQI_REQ_ID REQ_ID,
    A.REQUESTED,
    A.IS_PAID,
    A.NOT_IS_PAID,
    NVL(B.PACKAGING, 0) PACKAGING,
    NVL(B.SHIPPED, 0) SHIPPED,
    NVL(B.RECEIVED, 0) RECEIVED,
    NVL(B.CANCELED, 0) CANCELED
FROM
    (SELECT 
        RQI_REQ_ID, 
        SUM(RQI_QUANTITY) AS REQUESTED,
        NVL(SUM(IS_PAID), 0) AS IS_PAID,
        NVL(SUM(NOT_IS_PAID), 0) AS NOT_IS_PAID
    FROM 
        REQUEST_ITEMS 
        LEFT JOIN (
            select 
                SHI_RQI_ID, 
                SUM(case when SHI_IS_PAID = 'T' then 1 else 0 end) IS_PAID,
                SUM(case when SHI_IS_PAID = 'T' then 0 else 1 end) NOT_IS_PAID 
            from 
                SHIPMENT_ITEMS 
            where 
                SHI_RQI_ID is not null
            group by 
                SHI_RQI_ID)
            ON SHI_RQI_ID = RQI_ID
    GROUP BY 
        RQI_REQ_ID) A
    left join
        (SELECT * FROM (
            SELECT
                REQUESTS.REQ_ID,
                SHIPMENTS.SHP_STATUS
            FROM
                SHIPMENTS
                JOIN SHIPMENT_ITEMS
                    ON SHIPMENT_ITEMS.SHI_SHP_ID = SHIPMENTS.SHP_ID
                JOIN REQUEST_ITEMS
                    ON SHIPMENT_ITEMS.SHI_RQI_ID = REQUEST_ITEMS.RQI_ID
                JOIN REQUESTS
                    ON REQUESTS.REQ_ID = REQUEST_ITEMS.RQI_REQ_ID
                ) PIVOT (COUNT(*) FOR (SHP_STATUS) IN ('PACKAGING' AS PACKAGING, 'SHIPPED' AS SHIPPED, 'RECEIVED' AS RECEIVED, 'CANCELED' AS CANCELED))) B
        on A.RQI_REQ_ID = B.REQ_ID;