CREATE OR REPLACE PACKAGE am_allmetric_mngr IS

  -- Author  : TOMASZ.SIKORA
  -- Created : 2009-07-01 20:22:12
  -- Purpose : -- allmon allmetric schema (by Tomasz Sikora)
  -- Public type declarations
  
  --TYPE < typename > IS < datatype >;

  -- Public constant declarations
  --< constantname > CONSTANT < datatype > := < VALUE >;

  -- Public variable declarations
  --< variablename > < datatype >;

  -- Public function and procedure declarations
  --FUNCTION < functionname > (< parameter > < datatype >) RETURN < datatype >;

  PROCEDURE meta_add_artifact(pi_artifactname am_artifact.artifactname%TYPE, 
                              pi_artifactcode am_artifact.artifactcode%TYPE);
  
  PROCEDURE meta_add_metrictype(pi_artifactcode am_artifact.artifactcode%TYPE, 
                                pi_metricname am_metrictype.metricname%TYPE, 
                                pi_metriccode am_metrictype.metriccode%TYPE,
                                pi_unit am_metrictype.unit%TYPE DEFAULT NULL);

  PROCEDURE meta_add_instance(pi_artifactcode am_artifact.artifactcode%TYPE, 
                              pi_instancename am_instance.instancename%TYPE, 
                              pi_instancecode am_instance.instancecode%TYPE);

  PROCEDURE meta_add_host(pi_hostname am_host.hostname%TYPE, 
                          pi_hostcode am_host.hostcode%TYPE,
                          pi_hostip am_host.hostip%TYPE);

  PROCEDURE data_add_resource(pi_metriccode am_metrictype.metriccode%TYPE, 
                              pi_resourcename am_resource.resourcename%TYPE,
                              pi_resourcecode am_resource.resourcecode%TYPE DEFAULT NULL,
                              pi_unit am_resource.unit%TYPE DEFAULT NULL);

  PROCEDURE data_add_source(pi_metriccode am_metrictype.metriccode%TYPE, 
                            pi_sourcename am_source.sourcename%TYPE,
                            pi_sourcecode am_source.sourcecode%TYPE DEFAULT NULL);

  PROCEDURE data_add_metricsdata(pi_instancecode am_instance.instancecode%TYPE,
                                 pi_hostcode am_host.hostcode%TYPE,
                                 pi_metriccode am_metrictype.metriccode%TYPE,
                                 pi_resourcename am_resource.resourcename%TYPE,
                                 pi_sourcename am_source.sourcename%TYPE,
                                 pi_datetime DATE,
                                 pi_metricvalue am_metricsdata.metricvalue%TYPE);
  PROCEDURE data_add_metricsdata(pi_instancecode am_instance.instancecode%TYPE,
                                 pi_hostcode am_host.hostcode%TYPE,
                                 pi_metriccode am_metrictype.metriccode%TYPE,
                                 pi_resourcecode am_resource.resourcecode%TYPE,
                                 pi_datetime DATE,
                                 pi_metricvalue am_metricsdata.metricvalue%TYPE);
                                                                
  PROCEDURE data_delete_metricsdata(pi_instancecode am_instance.instancecode%TYPE,
                                    pi_hostcode am_host.hostcode%TYPE,
                                    pi_metriccode am_metrictype.metriccode%TYPE,
                                    pi_metricdata_ts_start DATE DEFAULT to_date('1900-01-01', 'YYYY-MM-DD'),
                                    pi_metricdata_ts_end DATE DEFAULT to_date('2100-12-31', 'YYYY-MM-DD'));

  PROCEDURE admin_rebuilt_indexes;
                                      
END am_allmetric_mngr;
/
CREATE OR REPLACE PACKAGE BODY am_allmetric_mngr IS

  -- Private type declarations
  --TYPE < typename > IS < datatype >;

  -- Private constant declarations
  --< constantname > CONSTANT < datatype > := < VALUE >;

  -- Private variable declarations
  --< variablename > < datatype >;

  -- Function and procedure implementations
  --FUNCTION < functionname > (< parameter > < datatype >) RETURN < datatype > IS
  --  < localvariable > < datatype >;
  --BEGIN
  --  < STATEMENT >;
  --  RETURN(< RESULT >);
  --END;
  
  -------------------------------------------------------------------------------------------------
  PROCEDURE meta_add_artifact(pi_artifactname am_artifact.artifactname%TYPE, 
                              pi_artifactcode am_artifact.artifactcode%TYPE) IS
  BEGIN
    INSERT INTO am_artifact(am_arf_id, artifactname, artifactcode) 
    VALUES(am_arf_seq.NEXTVAL, pi_artifactname, pi_artifactcode);
  END;
  
  -------------------------------------------------------------------------------------------------
  PROCEDURE meta_add_metrictype(pi_artifactcode am_artifact.artifactcode%TYPE, 
                                pi_metricname am_metrictype.metricname%TYPE, 
                                pi_metriccode am_metrictype.metriccode%TYPE,
                                pi_unit am_metrictype.unit%TYPE DEFAULT NULL) IS
  BEGIN
    INSERT INTO am_metrictype(am_mty_id, am_arf_id, metricname, metriccode, unit) 
    VALUES(am_mty_seq.NEXTVAL, (SELECT aa.am_arf_id FROM am_artifact aa WHERE aa.artifactcode = pi_artifactcode), 
           pi_metricname, pi_metriccode, pi_unit); 
  END;

  -------------------------------------------------------------------------------------------------
  PROCEDURE meta_add_instance(pi_artifactcode am_artifact.artifactcode%TYPE, 
                              pi_instancename am_instance.instancename%TYPE, 
                              pi_instancecode am_instance.instancecode%TYPE) IS
  BEGIN
    INSERT INTO am_instance(am_ins_id, am_arf_id, instancename, instancecode) 
    VALUES(am_ins_seq.NEXTVAL, (SELECT aa.am_arf_id FROM am_artifact aa WHERE aa.artifactcode = pi_artifactcode), 
           pi_instancename, pi_instancecode); 
  END;

  -------------------------------------------------------------------------------------------------
  PROCEDURE meta_add_host(pi_hostname am_host.hostname%TYPE, 
                          pi_hostcode am_host.hostcode%TYPE,
                          pi_hostip am_host.hostip%TYPE) IS
  BEGIN
    INSERT INTO am_host(am_hst_id, hostname, hostcode, hostip) 
    VALUES(am_hst_seq.NEXTVAL, pi_hostname, pi_hostcode, pi_hostip); 
  END;

  -------------------------------------------------------------------------------------------------
  PROCEDURE data_add_resource(pi_metriccode am_metrictype.metriccode%TYPE, 
                              pi_resourcename am_resource.resourcename%TYPE,
                              pi_resourcecode am_resource.resourcecode%TYPE DEFAULT NULL,
                              pi_unit am_resource.unit%TYPE DEFAULT NULL) IS
  BEGIN
    INSERT INTO am_resource(am_rsc_id, am_mty_id, resourcename, resourcecode, unit)
    VALUES(am_rsc_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = pi_metriccode),
           pi_resourcename, pi_resourcecode, pi_unit);
  END;

  -------------------------------------------------------------------------------------------------
  PROCEDURE data_add_source(pi_metriccode am_metrictype.metriccode%TYPE, 
                            pi_sourcename am_source.sourcename%TYPE,
                            pi_sourcecode am_source.sourcecode%TYPE DEFAULT NULL) IS
  BEGIN
    INSERT INTO am_source(am_src_id, am_mty_id, sourcename, sourcecode)
    VALUES(am_src_seq.NEXTVAL, (SELECT am.am_mty_id FROM am_metrictype am WHERE am.metriccode = pi_metriccode),
           pi_sourcename, pi_sourcecode);
  END;

  -------------------------------------------------------------------------------------------------
  PROCEDURE data_add_metricsdata(pi_instancecode am_instance.instancecode%TYPE,
                                 pi_hostcode am_host.hostcode%TYPE,
                                 pi_metriccode am_metrictype.metriccode%TYPE,
                                 pi_resourcename am_resource.resourcename%TYPE,
                                 pi_sourcename am_source.sourcename%TYPE,
                                 pi_datetime DATE,
                                 pi_metricvalue am_metricsdata.metricvalue%TYPE) IS
  BEGIN
    INSERT INTO am_metricsdata(am_met_id, am_ins_id, am_hst_id, am_rsc_id, am_src_id, am_cal_id, am_tim_id, metricvalue, ts)
    VALUES(am_met_seq.NEXTVAL, 
           (SELECT ai.am_ins_id FROM am_instance ai WHERE ai.instancecode = pi_instancecode),
           (SELECT ah.am_hst_id FROM am_host ah WHERE ah.hostcode = pi_hostcode),
           (SELECT ar.am_rsc_id FROM am_resource ar, am_metrictype am WHERE ar.am_mty_id = am.am_mty_id AND ar.resourcename = pi_resourcename AND am.metriccode = pi_metriccode),
           (SELECT asr.am_src_id FROM am_source asr, am_metrictype am WHERE asr.am_mty_id = am.am_mty_id AND asr.sourcename = pi_sourcename AND am.metriccode = pi_metriccode),
           (SELECT ac.am_cal_id FROM am_calendar ac WHERE ac.caldate = trunc(pi_datetime, 'DD')),
           (SELECT t.am_tim_id FROM am_time t WHERE t.hour = to_char(pi_datetime, 'HH24') AND t.minute = to_char(pi_datetime, 'MI')),
           pi_metricvalue,
           pi_datetime);
  END;

  PROCEDURE data_add_metricsdata(pi_instancecode am_instance.instancecode%TYPE,
                                 pi_hostcode am_host.hostcode%TYPE,
                                 pi_metriccode am_metrictype.metriccode%TYPE,
                                 pi_resourcecode am_resource.resourcecode%TYPE,
                                 pi_datetime DATE,
                                 pi_metricvalue am_metricsdata.metricvalue%TYPE) IS
  BEGIN
    INSERT INTO am_metricsdata(am_met_id, am_ins_id, am_hst_id, am_rsc_id, am_src_id, am_cal_id, am_tim_id, metricvalue, ts)
    VALUES(am_met_seq.NEXTVAL, 
           (SELECT ai.am_ins_id FROM am_instance ai WHERE ai.instancecode = pi_instancecode),
           (SELECT ah.am_hst_id FROM am_host ah WHERE ah.hostcode = pi_hostcode),
           (SELECT ar.am_rsc_id FROM am_resource ar, am_metrictype am WHERE ar.am_mty_id = am.am_mty_id AND ar.resourcecode = pi_resourcecode AND am.metriccode = pi_metriccode),
           NULL,
           (SELECT ac.am_cal_id FROM am_calendar ac WHERE ac.caldate = trunc(pi_datetime, 'DD')),
           (SELECT t.am_tim_id FROM am_time t WHERE t.hour = to_char(pi_datetime, 'HH24') AND t.minute = to_char(pi_datetime, 'MI')),
           pi_metricvalue,
           pi_datetime);
  END;

  -------------------------------------------------------------------------------------------------
  PROCEDURE data_delete_metricsdata(pi_instancecode am_instance.instancecode%TYPE,
                                    pi_hostcode am_host.hostcode%TYPE,
                                    pi_metriccode am_metrictype.metriccode%TYPE,
                                    pi_metricdata_ts_start DATE DEFAULT to_date('1900-01-01', 'YYYY-MM-DD'),
                                    pi_metricdata_ts_end DATE DEFAULT to_date('2100-12-31', 'YYYY-MM-DD')) IS
  BEGIN
    DELETE FROM am_metricsdata am 
    WHERE am.ts BETWEEN pi_metricdata_ts_start AND pi_metricdata_ts_end
    AND   
    am.am_ins_id = (SELECT i.am_ins_id FROM am_instance i WHERE i.instancecode = pi_instancecode)
    AND   am.am_hst_id = (SELECT h.am_hst_id FROM am_host h WHERE h.hostcode = pi_hostcode)
    AND   am.am_rsc_id IN (SELECT r.am_rsc_id FROM am_metrictype m, am_resource r WHERE m.am_mty_id = r.am_mty_id AND m.metriccode = pi_metriccode)
    AND   am.am_src_id IN (SELECT sr.am_src_id FROM am_metrictype m, am_source sr WHERE m.am_mty_id = sr.am_mty_id AND m.metriccode = pi_metriccode);
  END;

  -------------------------------------------------------------------------------------------------
  /*
  FUNCTION data_get_metricsdata (< parameter > < datatype >) RETURN < datatype > IS
    < localvariable > < datatype >;
  BEGIN
    SELECT vam.resourcename, vam.sourcename, vam.metricvalue, vam.ts
    FROM   vam_metricsdata vam
    WHERE  vam.artifactcode = 'APP'
    AND    vam.hostcode = 'EXPHST'
    AND    vam.instancecode = 'PETSTR'
    AND    vam.metriccode = 'ACTCLS';
    RETURN(< RESULT >);
  END;
  */

  -------------------------------------------------------------------------------------------------
  -- rebuild indexes - advisable after heavy loading
  PROCEDURE admin_rebuilt_indexes IS
    TYPE t IS RECORD(alter_index_code VARCHAR2(1000));
    CURSOR c RETURN t IS 
      SELECT 'ALTER INDEX '||ai.Index_Name||' REBUILD UNRECOVERABLE' FROM All_Indexes ai WHERE ai.Index_Name LIKE 'AM_%';
    v t;
  BEGIN
    OPEN c;
    LOOP
      FETCH c INTO v;
      EXIT WHEN c%NOTFOUND;
      EXECUTE IMMEDIATE v.alter_index_code;--'ALTER INDEX AM_ARF_PK REBUILD UNRECOVERABLE';
      dbms_output.put_line(v.alter_index_code);
    END LOOP;
  END;

--BEGIN
  -- Initialization
  --< STATEMENT >;
END am_allmetric_mngr;
/
