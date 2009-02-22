-- allmon schema (by Tomasz Sikora)

-- USE CASES :
-- * add fact
-- * add fact dimension 
-- * add fact measure


-- TODO :
-- * add hierarchical dimensions (for snowflake schemas - not needed for star schemas)
-- * add data type column to dimension and measures ??

-- ** add procedures which checking data integrity in values tables (the same rows in all dimensions values and measures!!)
--       whole fact row is not valid if any of dimensions or measures does not exist

-------------------------------------------------------------------------------------------------------------------------
-- dropping schema
DROP SEQUENCE fc_dim_seq;
DROP SEQUENCE fc_div_seq;
DROP SEQUENCE fc_msr_seq;
DROP SEQUENCE fc_vld_seq;
DROP SEQUENCE fc_vlm_seq;

DROP TABLE fc_valuesmsr;
DROP TABLE fc_measures;

DROP TABLE fc_valuesdim;
DROP TABLE fc_dimvalues;
DROP TABLE fc_dimensions;

-------------------------------------------------------------------------------------------------------------------------
-- creating schema
-- -- -- multidimensional schema metadata
CREATE TABLE fc_dimensions (
  fc_dim_id NUMBER(10) NOT NULL,
  dimname VARCHAR(20) NOT NULL,
  code VARCHAR(8) NOT NULL,
  dimdesc VARCHAR(512),
  CONSTRAINT fc_dimensions__pk PRIMARY KEY (fc_dim_id) USING INDEX
);
CREATE SEQUENCE fc_dim_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
CREATE UNIQUE INDEX fc_dim_uk1 ON fc_dimensions(code);

CREATE TABLE fc_dimvalues (
  fc_div_id NUMBER(10) NOT NULL,
  fc_dim_id NUMBER(10) NOT NULL,
  val VARCHAR(1000) NOT NULL,
  CONSTRAINT fc_dimvalues__pk PRIMARY KEY (fc_div_id) USING INDEX,
  CONSTRAINT fc_div_fc_dim__fk1 FOREIGN KEY (fc_dim_id) REFERENCES fc_dimensions(fc_dim_id)
);
CREATE SEQUENCE fc_div_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
CREATE UNIQUE INDEX fc_div_uk1 ON fc_dimvalues(fc_dim_id, val);
CREATE INDEX fc_div_idx1 ON fc_dimvalues(fc_dim_id); -- REVIEW necessity of this index
CREATE INDEX fc_div_idx2 ON fc_dimvalues(val); -- REVIEW necessity of this index

CREATE TABLE fc_measures (
  fc_msr_id NUMBER(10) NOT NULL,
  msrname VARCHAR(20) NOT NULL,
  code VARCHAR(8) NOT NULL,
  CONSTRAINT fc_measures__pk PRIMARY KEY (fc_msr_id) USING INDEX
);
CREATE SEQUENCE fc_msr_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
CREATE UNIQUE INDEX fc_msr_uk1 ON fc_measures(code);

-- -- -- multidimensional data values
CREATE TABLE fc_valuesdim (
  fc_vld_id NUMBER(10) NOT NULL, -- TODO remove - possibly not necessary 
  fc_div_id NUMBER(10) NOT NULL,
  rownumber NUMBER(10) NOT NULL, -- REVIEW potentially fk to separate table 
  CONSTRAINT fc_valuesdim__pk PRIMARY KEY (fc_vld_id) USING INDEX,
  CONSTRAINT fc_vld_fc_div__fk1 FOREIGN KEY (fc_div_id) REFERENCES fc_dimvalues(fc_div_id)
);
CREATE SEQUENCE fc_vld_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
CREATE UNIQUE INDEX fc_vld_uk1 ON fc_valuesdim(fc_div_id, rownumber);
CREATE INDEX fc_vld_idx1 ON fc_valuesdim(fc_div_id); -- REVIEW necessity of this index
CREATE INDEX fc_vld_idx2 ON fc_valuesdim(rownumber); -- REVIEW necessity of this index

CREATE TABLE fc_valuesmsr (
  fc_vlm_id NUMBER(10) NOT NULL, -- TODO remove - possibly not necessary 
  fc_msr_id NUMBER(10) NOT NULL,
  rownumber NUMBER(10) NOT NULL, -- REVIEW potentially fk to separate table 
  val NUMBER(16, 6) NOT NULL,
  CONSTRAINT fc_valuesmsr_pk PRIMARY KEY (fc_vlm_id) USING INDEX,
  CONSTRAINT fc_vlm_fc_vld__fk1 FOREIGN KEY (fc_msr_id) REFERENCES fc_measures(fc_msr_id)
);
CREATE SEQUENCE fc_vlm_seq MINVALUE 1 MAXVALUE 999999999999999 INCREMENT BY 1 CACHE 25 CYCLE;
CREATE UNIQUE INDEX fc_vlm_uk1 ON fc_valuesmsr(fc_msr_id, rownumber);
CREATE INDEX fc_vlm_idx1 ON fc_valuesmsr(fc_msr_id); -- REVIEW necessity of this index
CREATE INDEX fc_vlm_idx2 ON fc_valuesmsr(rownumber); -- REVIEW necessity of this index
CREATE INDEX fc_vlm_idx3 ON fc_valuesmsr(val); -- REVIEW necessity of this index

