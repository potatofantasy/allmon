-- allmon allmetric schema (by Tomasz Sikora)

-- TODO :
-- * 

/*
Example of stored metrics data in denormalized fashion (1NF).

(meta-data) 			(meta-data) 	(dynamic-dim) 	(dynamic-dim) 			
Artifact 	Host 	Instance 	Metric Type 	Resource 	Source 	Metric 	Time Stamp 	Time
OS 	Host1 	- 	CPU 	CPUn 	- 	NV 	TS 	T
OS 	Host1 	- 	Mem 	Usr 	- 	NV 	TS 	T
OS 	Host1 	- 	Mem 	Sys 	- 	NV 	TS 	T
OS 	Host1 	- 	IO 	- 	? 	NV 	TS 	T
OS 	Host1 	- 	Page 	- 	- 	NV 	TS 	T
OS 	Host1 	- 	Net 	- 	? 	NV 	TS 	T
AppMet 	Host1 	AppInst1 	Action 	ActionClass1 	User 	ExecTime 	TS 	T
AppMet 	Host1 	AppInst1 	POJO 	Class1.method 	Class.method 	ExecTime 	TS 	T
AppMet 	Host1 	AppInst1 	EJB 	EJB1.method 	? 	ExecTime 	TS 	T
AppMet 	Host1 	AppInst1 	MDBExec 	MDB1 	? 	ExecTime 	TS 	T
AppMet 	Host1 	AppInst1 	MDBWait 	MDB1 	? 	ExecTime 	TS 	T
Rep 	Host1 	RepServ1 	QueueLength 	Rep1 	? 	NV 	TS 	T
Rep 	Host1 	RepServ1 	RepExec 	Rep1 	? 	ExecTime 	TS 	T
Rep 	Host1 	RepServ1 	RepWait 	Rep1 	? 	ExecTime 	TS 	T
JVM 	Host1 	JVMInst1 	Mem 	Heap 	- 	NV 	TS 	T
JVM 	Host1 	JVMInst1 	Mem 	Tenured 	- 	NV 	TS 	T
JVM 	Host1 	JVMInst1 	Mem 	Eden 	- 	NV 	TS 	T
JVM 	Host1 	JVMInst1 	Threads 	- 	- 	NV 	TS 	T
DB 	Host1 	DBInst1 	Sessions 	- 	- 	NV 	TS 	T
HW 	Machine1 	- 	Temp 	CPUTemp1 	- 	NV 	TS 	T 


  Artifact	     -- a part of infrastructure under monitoring: OS, AppMet, Rep, JVM, DB, HW, etc.
  Instance	     -- related to Artifact: CPU, MEM, IO, AppInstance, RepServInst, JVMInst
  Metric Type	   -- related to Artifact - represents type of collected metric: CPU1, CPU2, Mem Usr, ...
  Host	
  Resource	
  Source	       -- source of a call to monitored resource 
  Metric	
  Time Nature	
  
*/

-------------------------------------------------------------------------------------------------------------------------
-- drop schema
DROP SEQUENCE am_arf_seq;
DROP SEQUENCE am_mty_seq;
DROP SEQUENCE am_hst_seq;
DROP SEQUENCE am_ins_seq;
DROP SEQUENCE am_met_seq;
DROP SEQUENCE am_rsc_seq;
DROP SEQUENCE am_src_seq;

-- drop fact table
DROP TABLE am_metricsdata;

-- drop dimensions
DROP TABLE am_metrictype;
DROP TABLE am_instance;
DROP TABLE am_artifact;
DROP TABLE am_host;
DROP TABLE am_resource;
DROP TABLE am_source;

DROP TABLE am_pivot;

-------------------------------------------------------------------------------------------------------------------------
-- creating schema
-- -- create dimension tables
CREATE TABLE am_artifact (
  am_arf_id NUMBER(10) NOT NULL,
  artifactname VARCHAR(20) NOT NULL,
  artifactcode VARCHAR(6) NOT NULL,
  CONSTRAINT am_arf_pk PRIMARY KEY (am_arf_id) USING INDEX
);
CREATE SEQUENCE am_arf_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1;
CREATE UNIQUE INDEX am_arf_uk1 ON am_artifact(artifactcode);

CREATE TABLE am_metrictype (
  am_mty_id NUMBER(10) NOT NULL,
  am_arf_id NUMBER(10) NOT NULL,
  metricname VARCHAR(20) NOT NULL,
  metriccode VARCHAR(6) NOT NULL,
  CONSTRAINT am_mty_pk PRIMARY KEY (am_mty_id) USING INDEX,
  CONSTRAINT am_mty_am_arf_fk1 FOREIGN KEY (am_arf_id) REFERENCES am_artifact(am_arf_id)
);
CREATE SEQUENCE am_mty_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1;

CREATE TABLE am_instance (
  am_ins_id NUMBER(10) NOT NULL,
  am_arf_id NUMBER(10) NOT NULL,
  instancename VARCHAR(20) NOT NULL,
  instancecode VARCHAR(6) NOT NULL,
  url VARCHAR(100),
  CONSTRAINT am_ins_pk PRIMARY KEY (am_ins_id) USING INDEX,
  CONSTRAINT am_ins_am_arf_fk1 FOREIGN KEY (am_arf_id) REFERENCES am_artifact(am_arf_id)
);
CREATE SEQUENCE am_ins_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1;

CREATE TABLE am_host (
  am_hst_id NUMBER(10) NOT NULL,
  hostname VARCHAR(20) NOT NULL,
  hostcode VARCHAR(6) NOT NULL,
  hostip VARCHAR(15) NOT NULL,
  CONSTRAINT am_hst_pk PRIMARY KEY (am_hst_id) USING INDEX
);
CREATE SEQUENCE am_hst_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1;

CREATE TABLE am_resource (
  am_rsc_id NUMBER(10) NOT NULL,
  resourcename VARCHAR(200) NOT NULL,
  resourcecode VARCHAR(10),
  CONSTRAINT am_rsc_pk PRIMARY KEY (am_rsc_id) USING INDEX
);
CREATE SEQUENCE am_rsc_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1;
CREATE UNIQUE INDEX am_rsc_uk1 ON am_resource(resourcename);
CREATE UNIQUE INDEX am_rsc_uk2 ON am_resource(resourcecode);

CREATE TABLE am_source (
  am_src_id NUMBER(10) NOT NULL,
  sourcename VARCHAR(200) NOT NULL,
  sourcecode VARCHAR(10),
  CONSTRAINT am_src_pk PRIMARY KEY (am_src_id) USING INDEX
);
CREATE SEQUENCE am_src_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1;
CREATE UNIQUE INDEX am_src_uk1 ON am_source(sourcename);
CREATE UNIQUE INDEX am_src_uk2 ON am_source(sourcecode);

-- create fact table 
CREATE TABLE am_metricsdata (
  am_met_id NUMBER(10) NOT NULL,
  --am_arf_id NUMBER(10) NOT NULL, -- Artifact
  am_mty_id NUMBER(10) NOT NULL, -- Metric Type
  am_ins_id NUMBER(10) NOT NULL, -- Instance	
  am_hst_id NUMBER(10) NOT NULL, -- Host
  am_rsc_id NUMBER(10) NOT NULL, -- Resource	
  am_src_id NUMBER(10) NOT NULL, -- Source	
  metricvalue NUMBER(13,3) NOT NULL, -- Metric	
  ts DATE NOT NULL, -- Time Stamp
  loadts DATE DEFAULT SYSDATE NOT NULL, -- Time Stamp
  CONSTRAINT am_met_pk PRIMARY KEY (am_met_id) USING INDEX,
  CONSTRAINT am_met_am_mty_fk1 FOREIGN KEY (am_mty_id) REFERENCES am_metrictype(am_mty_id),
  CONSTRAINT am_met_am_ins_fk1 FOREIGN KEY (am_ins_id) REFERENCES am_instance(am_ins_id),
  CONSTRAINT am_met_am_hst_fk1 FOREIGN KEY (am_hst_id) REFERENCES am_host(am_hst_id),
  CONSTRAINT am_met_am_rsc_fk1 FOREIGN KEY (am_rsc_id) REFERENCES am_resource(am_rsc_id),
  CONSTRAINT am_met_am_src_fk1 FOREIGN KEY (am_src_id) REFERENCES am_source(am_src_id)
);
CREATE SEQUENCE am_met_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 100;
CREATE INDEX am_met_am_mty_idx1 ON am_metricsdata(am_mty_id);
CREATE INDEX am_met_am_ins_idx1 ON am_metricsdata(am_ins_id);
CREATE INDEX am_met_am_hst_idx1 ON am_metricsdata(am_hst_id);
CREATE INDEX am_met_am_rsc_idx1 ON am_metricsdata(am_rsc_id);
CREATE INDEX am_met_am_src_idx1 ON am_metricsdata(am_src_id);


-------------------------------------------------------------------------------------------------------------------------
-- miscelaneous
CREATE TABLE am_pivot AS
WITH ones AS
   (SELECT 0 x FROM dual
    UNION SELECT 1 FROM dual
    UNION SELECT 2 FROM dual
    UNION SELECT 3 FROM dual
    UNION SELECT 4 FROM dual
    UNION SELECT 5 FROM dual
    UNION SELECT 6 FROM dual
    UNION SELECT 7 FROM dual
    UNION SELECT 8 FROM dual
    UNION SELECT 9 FROM dual)
    SELECT 100000*o100000.x + 10000*o10000.x + 1000*o1000.x + 100*o100.x + 10*o10.x + o1.x x
    FROM ones o1, ones o10, ones o100, ones o1000, ones o10000, ones o100000;


-------------------------------------------------------------------------------------------------------------------------
-- create views




-------------------------------------------------------------------------------------------------------------------------
-- fill up default data for not dynamic dimensions
-- OS, AppMet, Rep, JVM, DB, HW
INSERT INTO am_artifact(am_arf_id, artifactname, artifactcode) VALUES(am_arf_seq.NEXTVAL, 'Operating System', 'OS'); 
INSERT INTO am_artifact(am_arf_id, artifactname, artifactcode) VALUES(am_arf_seq.NEXTVAL, 'Application', 'APP'); 
INSERT INTO am_artifact(am_arf_id, artifactname, artifactcode) VALUES(am_arf_seq.NEXTVAL, 'Report', 'REP'); 
INSERT INTO am_artifact(am_arf_id, artifactname, artifactcode) VALUES(am_arf_seq.NEXTVAL, 'Java Virtual Machine', 'JVM'); 
INSERT INTO am_artifact(am_arf_id, artifactname, artifactcode) VALUES(am_arf_seq.NEXTVAL, 'Database', 'DB'); 
INSERT INTO am_artifact(am_arf_id, artifactname, artifactcode) VALUES(am_arf_seq.NEXTVAL, 'Hardware', 'HW'); 
COMMIT;

INSERT INTO am_instance(am_ins_id, am_arf_id, instancename, instancecode) VALUES(am_ins_seq.NEXTVAL, (SELECT aa.am_arf_id FROM am_artifact aa WHERE aa.artifactcode = 'APP'), 'Petstore', 'PETSTR'); 
COMMIT;

INSERT INTO am_metrictype(am_mty_id, am_arf_id, metricname, metriccode) VALUES(am_mty_seq.NEXTVAL, (SELECT aa.am_arf_id FROM am_artifact aa WHERE aa.artifactcode = 'APP'), 'Struts Action Class', 'ACTCLS'); 
COMMIT;


-------------------------------------------------------------------------------------------------------------------------
-- check data

SELECT * FROM am_metricsdata;





