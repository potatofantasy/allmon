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

  FUNCTION data_get_metricsdata_sql(pi_artifactcode am_artifact.artifactcode%TYPE, 
                                    pi_instancecode am_instance.instancecode%TYPE,
                                    pi_hostcode am_host.hostcode%TYPE,
                                    pi_metriccode am_metrictype.metriccode%TYPE) RETURN VARCHAR2;
  FUNCTION data_get_metricsdata_sql(pi_artifactcode am_artifact.artifactcode%TYPE, 
                                    pi_instancecode am_instance.instancecode%TYPE,
                                    pi_hostcode am_host.hostcode%TYPE,
                                    pi_metriccode am_metrictype.metriccode%TYPE,
                                    pi_resourcecode am_resource.resourcecode%TYPE) RETURN VARCHAR2;

  PROCEDURE admin_rebuilt_views;
  
  PROCEDURE admin_rebuilt_indexes;
                                      
END am_allmetric_mngr;
/
CREATE OR REPLACE PACKAGE BODY am_allmetric_mngr IS

  -- Private type declarations
  --TYPE < typename > IS < datatype >;

  -- Private constant declarations
  c_metricdata_views_name CONSTANT VARCHAR2(20) := 'vam_metricsdata_dyn_';
  
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
  FUNCTION data_get_metricsdata_sql(pi_artifactcode am_artifact.artifactcode%TYPE, 
                                    pi_instancecode am_instance.instancecode%TYPE,
                                    pi_hostcode am_host.hostcode%TYPE,
                                    pi_metriccode am_metrictype.metriccode%TYPE) RETURN VARCHAR2 IS
    sql_code VARCHAR2(255);
  BEGIN
    sql_code := sql_code || ' SELECT vam.resourcename, vam.sourcename, vam.metricvalue, vam.ts';
    sql_code := sql_code || ' FROM   vam_metricsdata vam';
    sql_code := sql_code || ' WHERE  vam.artifactcode = ' || pi_artifactcode;
    sql_code := sql_code || ' AND    vam.instancecode = ' || pi_instancecode;
    sql_code := sql_code || ' AND    vam.hostcode = ' || pi_hostcode;
    sql_code := sql_code || ' AND    vam.metriccode = ' || pi_metriccode;
    RETURN(sql_code);
  END;

  -------------------------------------------------------------------------------------------------
  FUNCTION data_get_metricsdata_sql(pi_artifactcode am_artifact.artifactcode%TYPE, 
                                    pi_instancecode am_instance.instancecode%TYPE,
                                    pi_hostcode am_host.hostcode%TYPE,
                                    pi_metriccode am_metrictype.metriccode%TYPE,
                                    pi_resourcecode am_resource.resourcecode%TYPE) RETURN VARCHAR2 IS
    sql_code VARCHAR2(255);
  BEGIN
    sql_code := sql_code || ' SELECT vam.sourcename, vam.metricvalue, vam.ts';
    sql_code := sql_code || ' FROM   vam_metricsdata vam';
    sql_code := sql_code || ' WHERE  vam.artifactcode = ' || pi_artifactcode;
    sql_code := sql_code || ' AND    vam.instancecode = ' || pi_instancecode;
    sql_code := sql_code || ' AND    vam.hostcode = ' || pi_hostcode;
    sql_code := sql_code || ' AND    vam.metriccode = ' || pi_metriccode;
    sql_code := sql_code || ' AND    vam.resourcename = ' || pi_resourcecode;
    RETURN(sql_code);
  END;

  -------------------------------------------------------------------------------------------------
  -- generate views to the allmetric schema
  PROCEDURE admin_rebuilt_views IS
    -- existing views and materialized views
    TYPE t_ex IS RECORD(viewname VARCHAR2(100));
    CURSOR c_exv RETURN t_ex IS 
      SELECT av.owner || '.' || av.view_name AS viewname FROM all_views av WHERE av.view_name LIKE upper('%' || c_metricdata_views_name || '%');
    CURSOR c_ext RETURN t_ex IS 
      SELECT av.owner || '.' || av.table_name AS viewname FROM all_tables av WHERE av.table_name LIKE upper('%' || c_metricdata_views_name || '%');
    v_ex t_ex;
    
    -- recreating views
    TYPE t_met IS RECORD(metricname am_metrictype.metricname%TYPE, metriccode am_metrictype.metriccode%TYPE);
    CURSOR c_met RETURN t_met IS 
      SELECT DISTINCT mt.metricname, mt.metriccode
      FROM   am_metrictype mt, am_resource rs
      WHERE  mt.am_mty_id = rs.am_mty_id
      AND    rs.resourcecode IS NOT NULL
      ORDER  BY mt.metricname, mt.metriccode;
    v_met t_met;
    TYPE t_amm IS RECORD(metricname am_metrictype.metricname%TYPE, metriccode am_metrictype.metriccode%TYPE, resourcecode am_resource.resourcecode%TYPE);
    CURSOR c_amm(metriccode VARCHAR2) RETURN t_amm IS 
      SELECT DISTINCT mt.metricname, mt.metriccode, rs.resourcecode
      FROM   am_metrictype mt, am_resource rs
      WHERE  mt.am_mty_id = rs.am_mty_id
      AND    mt.metriccode = metriccode
      AND    rs.resourcecode IS NOT NULL
      ORDER  BY mt.metricname, mt.metriccode, rs.resourcecode;
    v_amm t_amm;
    --
    drop_view_code VARCHAR2(32767);
    drop_table_code VARCHAR2(32767);
    create_view_code_cv VARCHAR2(32767);
    create_view_code_cmv VARCHAR2(32767);
    create_view_code_s VARCHAR2(32767);
    create_view_code_f VARCHAR2(32767);
    create_view_code_w VARCHAR2(32767);
    cnt NUMBER; first_resource VARCHAR2(100);
  BEGIN
    dbms_output.put_line('start');
    
    -- drop existing views
    OPEN c_exv;
    LOOP
      FETCH c_exv INTO v_ex;
      EXIT WHEN c_exv%NOTFOUND;
      drop_view_code := 'DROP VIEW ' || v_ex.viewname;
      EXECUTE IMMEDIATE drop_view_code;
      dbms_output.put_line(drop_view_code);
    END LOOP;
    CLOSE c_exv;
    OPEN c_ext;
    LOOP
      FETCH c_ext INTO v_ex;
      EXIT WHEN c_ext%NOTFOUND;
      drop_table_code := 'DROP MATERIALIZED VIEW ' || v_ex.viewname;
      EXECUTE IMMEDIATE drop_table_code;
      dbms_output.put_line(drop_table_code);
    END LOOP;
    CLOSE c_ext;
    
    -- recreate views
    OPEN c_met;
    LOOP
      FETCH c_met INTO v_met;
      EXIT WHEN c_met%NOTFOUND;
      
      --dbms_output.put_line(v_met.metriccode);
      create_view_code_cv := 'CREATE VIEW ' || c_metricdata_views_name || v_met.metriccode || ' AS ';
      create_view_code_cmv := 'CREATE MATERIALIZED VIEW m' || c_metricdata_views_name || v_met.metriccode || ' AS ';
      
      -- select phrase      
      cnt := 0;
      OPEN c_amm(v_met.metriccode);
      LOOP
        FETCH c_amm INTO v_amm;
        EXIT WHEN c_amm%NOTFOUND;
        IF cnt = 0 THEN 
          first_resource := v_amm.resourcecode;
        END IF;
        IF cnt <> 0 THEN 
          create_view_code_s := create_view_code_s || ',';
        END IF;
        create_view_code_s := create_view_code_s || ' sel_' || v_amm.resourcecode || '.metricvalue AS metval_' || v_amm.resourcecode;
        cnt := cnt + 1;
      END LOOP;
      CLOSE c_amm;
      create_view_code_s := ' SELECT sel_' || first_resource || '.am_ins_id, sel_' || first_resource || '.instancename, sel_' || first_resource || '.instancecode, sel_' || first_resource || '.hostname, sel_' || first_resource || '.hostcode, '
                                 || 'sel_' || first_resource || '.year, sel_' || first_resource || '.month, sel_' || first_resource || '.day, sel_' || first_resource || '.minute, sel_' || first_resource || '.ts, ' 
                                 || create_view_code_s;
      
      -- from phrase      
      create_view_code_f := ' FROM ';
      cnt := 0;
      OPEN c_amm(v_met.metriccode);
      LOOP
        FETCH c_amm INTO v_amm;
        EXIT WHEN c_amm%NOTFOUND;
        IF cnt <> 0 THEN 
          create_view_code_f := create_view_code_f || ',';
        END IF;
        IF cnt = 0 THEN 
          create_view_code_f := create_view_code_f || '(SELECT vmc.* FROM vam_metricsdata_cal vmc WHERE vmc.metriccode = ''' || v_amm.metriccode || ''' AND vmc.resourcecode = ''' || v_amm.resourcecode || ''') sel_' || v_amm.resourcecode;
        ELSE
          create_view_code_f := create_view_code_f || '(SELECT md.metricvalue,md.ts FROM am_metricsdata md, am_resource rs, am_metrictype mt WHERE  md.am_rsc_id = rs.am_rsc_id AND rs.am_mty_id = mt.am_mty_id AND mt.metriccode = ''' || v_amm.metriccode || ''' AND rs.resourcecode = ''' || v_amm.resourcecode || ''') sel_' || v_amm.resourcecode;
        END IF;
        cnt := cnt + 1;
      END LOOP;
      CLOSE c_amm;
      
      -- where phrase
      create_view_code_w := create_view_code_w || ' WHERE ';
      cnt := 0;
      OPEN c_amm(v_met.metriccode);
      LOOP
        FETCH c_amm INTO v_amm;
        EXIT WHEN c_amm%NOTFOUND;
        IF cnt > 1 THEN 
          create_view_code_w := create_view_code_w || ' AND ';
        END IF;        IF cnt > 0 THEN 
          create_view_code_w := create_view_code_w || ' sel_' || first_resource || '.ts = sel_' || v_amm.resourcecode  || '.ts(+) ';
        END IF;
        cnt := cnt + 1;
      END LOOP;
      CLOSE c_amm;
      
      dbms_output.put_line(create_view_code_cmv);
      dbms_output.put_line(create_view_code_s);
      dbms_output.put_line(create_view_code_f);
      dbms_output.put_line(create_view_code_w);
      
      EXECUTE IMMEDIATE create_view_code_cv || ' ' || create_view_code_s  || ' ' || create_view_code_f  || ' ' || create_view_code_w;
      EXECUTE IMMEDIATE create_view_code_cmv || ' ' || create_view_code_s  || ' ' || create_view_code_f  || ' ' || create_view_code_w;
    END LOOP;
    CLOSE c_met;
    
    /*
    CREATE MATERIALIZED VIEW mvam_metricsdata_dyn_appslc AS 
    SELECT sel_logusr.am_ins_id, sel_logusr.instancename, sel_logusr.instancecode, sel_logusr.hostname, sel_logusr.hostcode, sel_logusr.year, sel_logusr.month, sel_logusr.day, sel_logusr.minute, sel_logusr.ts,
           sel_logusr.metricvalue AS metval_logusr,
           sel_dbcmpl.metricvalue AS metval_dbcmpl,
           sel_sfejbs.metricvalue AS metval_sfejbs
    FROM 
        --(SELECT to_date(to_char(c.caldate, 'YYYY-MM-DD') || ' ' || to_char(t.t, 'HH24-MI'), 'YYYY-MM-DD HH24-MI') AS ts
        -- FROM   am_calendar c, am_time t) sel_caltime,
        (SELECT vmc.*
         FROM   vam_metricsdata_cal vmc
         WHERE  vmc.metriccode = 'APPSLC'
         AND    vmc.resourcecode = 'LOGUSR') sel_logusr,
        (SELECT md.metricvalue, md.ts
         FROM   am_metricsdata md, am_resource rs, am_metrictype mt
         WHERE  md.am_rsc_id = rs.am_rsc_id 
         AND    rs.am_mty_id = mt.am_mty_id
         AND    mt.metriccode = 'APPSLC'
         AND    rs.resourcecode = 'DBCMPL') sel_dbcmpl,
        (SELECT md.metricvalue, md.ts
         FROM   am_metricsdata md, am_resource rs, am_metrictype mt
         WHERE  md.am_rsc_id = rs.am_rsc_id 
         AND    rs.am_mty_id = mt.am_mty_id
         AND    mt.metriccode = 'APPSLC'
         AND    rs.resourcecode = 'SFEJBS') sel_sfejbs
    WHERE -- TODO replace ts with observation_id !!
         sel_logusr.ts = sel_dbcmpl.ts
    AND  sel_logusr.ts = sel_sfejbs.ts;
    */
    
  END;


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

