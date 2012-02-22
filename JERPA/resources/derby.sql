CREATE TABLE EXPERIMENT (
	EXPERIMENT_ID INTEGER NOT NULL PRIMARY KEY,
	OWNER_ID INTEGER NOT NULL ,
	SUBJECT_PERSON_ID INTEGER NOT NULL ,
	SCENARIO_ID INTEGER NOT NULL ,
	WEATHER_ID INTEGER NOT NULL ,
	RESEARCH_GROUP_ID INTEGER NOT NULL ,
	START_TIME DATE,
	END_TIME DATE,
	TEMPERATURE SMALLINT,
	WEATHERNOTE VARCHAR (255),
	IS_PRIVATE INTEGER DEFAULT 0 NOT NULL,
	VERSION BIGINT DEFAULT 0 NOT NULL,
	ADDED integer,
	CHANGED integer
);

CREATE TABLE PERSON (
	PERSON_ID INTEGER NOT NULL PRIMARY KEY,
	DEFAULT_GROUP_ID INTEGER,
	NAME VARCHAR (50),
	SURNAME VARCHAR (50) NOT NULL ,
	DATE_OF_BIRTH DATE,
	GENDER CHAR (1) NOT NULL CONSTRAINT FILLED_GENDER CHECK (GENDER = 'M' OR GENDER='F' ),
	NOTE VARCHAR (255),
	USERNAME VARCHAR (50) UNIQUE,
	PASSWORD VARCHAR (50),
	AUTHORITY VARCHAR (50),
	CONFIRMED INTEGER DEFAULT 0,
	AUTHENTICATION VARCHAR (50),
	REGISTRATION_DATE DATE,
	VERSION BIGINT DEFAULT 0 NOT NULL,
	ADDED integer,
	CHANGED integer
);

CREATE TABLE COEXPERIMENTER_REL (
	PERSON_ID INTEGER NOT NULL ,
	EXPERIMENT_ID INTEGER NOT NULL ,
PRIMARY KEY (PERSON_ID,EXPERIMENT_ID)
);

CREATE TABLE SCENARIO (
	SCENARIO_ID INTEGER NOT NULL PRIMARY KEY,
	OWNER_ID INTEGER NOT NULL ,
	RESEARCH_GROUP_ID INTEGER NOT NULL ,
	TITLE VARCHAR (40) CONSTRAINT UNIQUE_SCENARIO_TITLE UNIQUE ,
	SCENARIO_LENGTH SMALLINT,
	SCENARIO_XML CLOB,
	DESCRIPTION VARCHAR (255),
	IS_PRIVATE INTEGER DEFAULT 0 NOT NULL ,
	SCENARIO_NAME VARCHAR (100),
	MIMETYPE VARCHAR (30),
	VERSION BIGINT DEFAULT 0 NOT NULL,
	ADDED integer,
	CHANGED integer
);

CREATE TABLE HARDWARE (
	HARDWARE_ID INTEGER NOT NULL PRIMARY KEY,
	TITLE VARCHAR (50) NOT NULL,
	TYPE VARCHAR (30) NOT NULL,
	DESCRIPTION VARCHAR (30),
	VERSION BIGINT DEFAULT 0 NOT NULL,
	ADDED integer,
	CHANGED integer
);

CREATE TABLE HARDWARE_USAGE_REL (
	HARDWARE_ID INTEGER NOT NULL ,
	EXPERIMENT_ID INTEGER NOT NULL ,
PRIMARY KEY (HARDWARE_ID,EXPERIMENT_ID)
);

CREATE TABLE DATA_FILE (
	DATA_FILE_ID INTEGER NOT NULL PRIMARY KEY,
	SAMPLING_RATE FLOAT NOT NULL ,
	FILE_CONTENT BLOB,
	EXPERIMENT_ID INTEGER NOT NULL ,
	MIMETYPE VARCHAR (40) NOT NULL ,
	FILENAME VARCHAR (80) NOT NULL,
	FILE_LENGTH BIGINT,
	VERSION BIGINT DEFAULT 0 NOT NULL,
	ADDED integer,
	CHANGED integer
);

CREATE TABLE FILE_METADATA_PARAM_VAL (
	FILE_METADATA_PARAM_DEF_ID INTEGER NOT NULL ,
	DATA_FILE_ID INTEGER NOT NULL ,
	METADATA_VALUE VARCHAR(30) NOT NULL ,
PRIMARY KEY (FILE_METADATA_PARAM_DEF_ID,DATA_FILE_ID)
)
;

CREATE TABLE WEATHER (
	WEATHER_ID INTEGER NOT NULL PRIMARY KEY,
	DESCRIPTION VARCHAR (30),
	TITLE VARCHAR (30),
	VERSION BIGINT DEFAULT 0 NOT NULL,
	ADDED integer,
	CHANGED integer
)
;

CREATE TABLE HEARING_IMPAIRMENT (
	HEARING_IMPAIRMENT_ID INTEGER NOT NULL PRIMARY KEY,
	DESCRIPTION VARCHAR (30) NOT NULL  CONSTRAINT UNIQUE_HARING_DEFECT_DESCRIPTI UNIQUE,
	ADDED integer,
	CHANGED integer
)
;

CREATE TABLE VISUAL_IMPAIRMENT (
	VISUAL_IMPAIRMENT_ID INTEGER NOT NULL PRIMARY KEY,
	DESCRIPTION VARCHAR (30) NOT NULL  CONSTRAINT UNIQUE_EYES_DEFECT_DESCRIPTION UNIQUE,
	ADDED integer,
	CHANGED integer
)
;

CREATE TABLE HEARING_IMPAIRMENT_REL (
	PERSON_ID INTEGER NOT NULL ,
	HEARING_IMPAIRMENT_ID INTEGER NOT NULL ,
PRIMARY KEY (PERSON_ID,HEARING_IMPAIRMENT_ID)
)
;

CREATE TABLE VISUAL_IMPAIRMENT_REL (
	VISUAL_IMPAIRMENT_ID INTEGER NOT NULL ,
	PERSON_ID INTEGER NOT NULL ,
PRIMARY KEY (VISUAL_IMPAIRMENT_ID,PERSON_ID)
)
;

CREATE TABLE PERSON_OPT_PARAM_DEF (
	PERSON_OPT_PARAM_DEF_ID INTEGER NOT NULL PRIMARY KEY,
	PARAM_NAME VARCHAR (30) NOT NULL ,
	PARAM_DATA_TYPE VARCHAR (20) NOT NULL	
)
;

CREATE TABLE PERSON_OPT_PARAM_VAL (
	PARAM_VALUE VARCHAR (30) NOT NULL ,
	PERSON_ID INTEGER NOT NULL ,
	PERSON_OPT_PARAM_DEF_ID INTEGER NOT NULL ,
PRIMARY KEY (PERSON_ID,PERSON_OPT_PARAM_DEF_ID)
)
;

CREATE TABLE EXPERIMENT_OPT_PARAM_DEF (
	EXPERIMENT_OPT_PARAM_DEF_ID INTEGER NOT NULL PRIMARY KEY,
	PARAM_NAME VARCHAR (30) NOT NULL ,
	PARAM_DATA_TYPE VARCHAR (20) NOT NULL
)
;

CREATE TABLE EXPERIMENT_OPT_PARAM_VAL (
	PARAM_VALUE VARCHAR (30) NOT NULL ,
	EXPERIMENT_ID INTEGER NOT NULL ,
	EXPERIMENT_OPT_PARAM_DEF_ID INTEGER NOT NULL ,
PRIMARY KEY (EXPERIMENT_ID,EXPERIMENT_OPT_PARAM_DEF_ID)
)
;

CREATE TABLE FILE_METADATA_PARAM_DEF (
	PARAM_NAME VARCHAR (30) NOT NULL ,
	FILE_METADATA_PARAM_DEF_ID INTEGER NOT NULL PRIMARY KEY,
	PARAM_DATA_TYPE VARCHAR (20) NOT NULL
)
;

CREATE TABLE RESEARCH_GROUP (
	RESEARCH_GROUP_ID INTEGER NOT NULL PRIMARY KEY,
	OWNER_ID INTEGER NOT NULL ,
	TITLE VARCHAR (100) NOT NULL ,
	DESCRIPTION VARCHAR (250) NOT NULL,
	VERSION BIGINT DEFAULT 0 NOT NULL,
	ADDED integer,
	CHANGED integer
)
;

CREATE TABLE RESEARCH_GROUP_MEMBERSHIP (
	PERSON_ID INTEGER NOT NULL ,
	RESEARCH_GROUP_ID INTEGER NOT NULL ,
	AUTHORITY VARCHAR (30) NOT NULL ,
PRIMARY KEY (PERSON_ID,RESEARCH_GROUP_ID)
)
;

CREATE TABLE HISTORY (
	HISTORY_ID INTEGER NOT NULL PRIMARY KEY,
	EXPERIMENT_ID INTEGER,
	SCENARIO_ID INTEGER,
	PERSON_ID INTEGER NOT NULL ,
	DATA_FILE_ID INTEGER,
	DATE_OF_DOWNLOAD DATE DEFAULT CURRENT_DATE NOT NULL,
	ADDED integer,
	CHANGED integer
)
;

CREATE TABLE ARTICLES (
	ARTICLE_ID INTEGER NOT NULL PRIMARY KEY,
	RESEARCH_GROUP_ID INTEGER,
	TEXT CLOB NOT NULL ,
	TIME DATE DEFAULT CURRENT_DATE NOT NULL ,
	TITLE VARCHAR (150) NOT NULL ,
	PERSON_ID INTEGER NOT NULL,
	VERSION BIGINT DEFAULT 0 NOT NULL,
	ADDED integer,
	CHANGED integer
)
;

CREATE TABLE ARTICLES_COMMENTS (
	COMMENT_ID INTEGER NOT NULL PRIMARY KEY,
	ARTICLE_ID INTEGER NOT NULL ,
	PARENT_ID INTEGER NOT NULL ,
	PERSON_ID INTEGER NOT NULL ,
	TEXT CLOB NOT NULL ,
	TIME DATE DEFAULT CURRENT_DATE NOT NULL,
	ADDED integer,
	CHANGED integer
)
;

CREATE TABLE GROUP_PERMISSION_REQUEST (
	REQUEST_ID INTEGER NOT NULL PRIMARY KEY,
	REQUESTED_PERMISSION VARCHAR (20) NOT NULL ,
	GRANTED BIGINT NOT NULL ,
	RESEARCH_GROUP_ID INTEGER NOT NULL ,
	PERSON_ID INTEGER NOT NULL,
	VERSION BIGINT DEFAULT 0 NOT NULL,
	ADDED integer,
	CHANGED integer
)
;

CREATE TABLE ARTICLES_SUBSCRIBTIONS (
	ARTICLE_ID INTEGER NOT NULL ,
	PERSON_ID INTEGER NOT NULL ,
PRIMARY KEY (ARTICLE_ID,PERSON_ID)
)
;


ALTER TABLE COEXPERIMENTER_REL ADD CONSTRAINT COEXP_EXPERIMENT FOREIGN KEY (EXPERIMENT_ID) REFERENCES EXPERIMENT (EXPERIMENT_ID)
;

ALTER TABLE HARDWARE_USAGE_REL ADD CONSTRAINT HW_USAGE_EXPERIMENT FOREIGN KEY (EXPERIMENT_ID) REFERENCES EXPERIMENT (EXPERIMENT_ID)
;

ALTER TABLE DATA_FILE ADD CONSTRAINT FILE_EXPERIMENT FOREIGN KEY (EXPERIMENT_ID) REFERENCES EXPERIMENT (EXPERIMENT_ID)
;

ALTER TABLE EXPERIMENT_OPT_PARAM_VAL ADD CONSTRAINT EXP_OPT_EXPERIMENT FOREIGN KEY (EXPERIMENT_ID) REFERENCES EXPERIMENT (EXPERIMENT_ID)
;

ALTER TABLE HISTORY ADD CONSTRAINT HISTORY_EXPERIMENT FOREIGN KEY (EXPERIMENT_ID) REFERENCES EXPERIMENT (EXPERIMENT_ID)
;

ALTER TABLE COEXPERIMENTER_REL ADD CONSTRAINT COEXP_PERSON FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE EXPERIMENT ADD CONSTRAINT EXP_OWNER FOREIGN KEY (OWNER_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE HEARING_IMPAIRMENT_REL ADD CONSTRAINT HEAR_REL_PERSON FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE VISUAL_IMPAIRMENT_REL ADD CONSTRAINT VISUAL_REL_PERSON FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE PERSON_OPT_PARAM_VAL ADD CONSTRAINT PERSON_OPT_PERSON FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE RESEARCH_GROUP ADD CONSTRAINT GROUP_OWNER FOREIGN KEY (OWNER_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE RESEARCH_GROUP_MEMBERSHIP ADD CONSTRAINT GROUP_MEMBER_PERSON FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE EXPERIMENT ADD CONSTRAINT EXP_SUBJECT FOREIGN KEY (SUBJECT_PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE SCENARIO ADD CONSTRAINT SCENARIO_OWNER FOREIGN KEY (OWNER_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE HISTORY ADD CONSTRAINT HISTORY_PERSON FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE ARTICLES_COMMENTS ADD CONSTRAINT ARTICLE_COMM_PERSON FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE GROUP_PERMISSION_REQUEST ADD CONSTRAINT GROUP_PERM_PERSON FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE ARTICLES ADD CONSTRAINT ARTICLE_PERSON FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE ARTICLES_SUBSCRIBTIONS ADD CONSTRAINT ARTICLE_SUBSCR_PERSON FOREIGN KEY (PERSON_ID) REFERENCES PERSON (PERSON_ID)
;

ALTER TABLE EXPERIMENT ADD CONSTRAINT EXP_SCENARIO FOREIGN KEY (SCENARIO_ID) REFERENCES SCENARIO (SCENARIO_ID)
;

ALTER TABLE HISTORY ADD CONSTRAINT HISTORY_SCENARIO FOREIGN KEY (SCENARIO_ID) REFERENCES SCENARIO (SCENARIO_ID)
;

ALTER TABLE HARDWARE_USAGE_REL ADD CONSTRAINT HW_USAGE_HARDWARE FOREIGN KEY (HARDWARE_ID) REFERENCES HARDWARE (HARDWARE_ID)
;

ALTER TABLE FILE_METADATA_PARAM_VAL ADD CONSTRAINT FILE_META_FILE FOREIGN KEY (DATA_FILE_ID) REFERENCES DATA_FILE (DATA_FILE_ID)
;

ALTER TABLE HISTORY ADD CONSTRAINT HISTORY_FILE FOREIGN KEY (DATA_FILE_ID) REFERENCES DATA_FILE (DATA_FILE_ID)
;

ALTER TABLE EXPERIMENT ADD CONSTRAINT EXP_WEATHER FOREIGN KEY (WEATHER_ID) REFERENCES WEATHER (WEATHER_ID)
;

ALTER TABLE HEARING_IMPAIRMENT_REL ADD CONSTRAINT HEAR_REL_HEAR FOREIGN KEY (HEARING_IMPAIRMENT_ID) REFERENCES HEARING_IMPAIRMENT (HEARING_IMPAIRMENT_ID)
;

ALTER TABLE VISUAL_IMPAIRMENT_REL ADD CONSTRAINT VISUAL_REL_VISUAL FOREIGN KEY (VISUAL_IMPAIRMENT_ID) REFERENCES VISUAL_IMPAIRMENT (VISUAL_IMPAIRMENT_ID)
;

ALTER TABLE PERSON_OPT_PARAM_VAL ADD CONSTRAINT PERSON_OPT_PARAM FOREIGN KEY (PERSON_OPT_PARAM_DEF_ID) REFERENCES PERSON_OPT_PARAM_DEF (PERSON_OPT_PARAM_DEF_ID)
;

ALTER TABLE EXPERIMENT_OPT_PARAM_VAL ADD CONSTRAINT EXP_OPT_PARAM FOREIGN KEY (EXPERIMENT_OPT_PARAM_DEF_ID) REFERENCES EXPERIMENT_OPT_PARAM_DEF (EXPERIMENT_OPT_PARAM_DEF_ID)
;

ALTER TABLE FILE_METADATA_PARAM_VAL ADD CONSTRAINT FILE_META_PARAM FOREIGN KEY (FILE_METADATA_PARAM_DEF_ID) REFERENCES FILE_METADATA_PARAM_DEF (FILE_METADATA_PARAM_DEF_ID)
;

ALTER TABLE RESEARCH_GROUP_MEMBERSHIP ADD CONSTRAINT GROUP_MEMBER_GROUP FOREIGN KEY (RESEARCH_GROUP_ID) REFERENCES RESEARCH_GROUP (RESEARCH_GROUP_ID)
;

ALTER TABLE SCENARIO ADD CONSTRAINT SCENARIO_GROUP FOREIGN KEY (RESEARCH_GROUP_ID) REFERENCES RESEARCH_GROUP (RESEARCH_GROUP_ID)
;

ALTER TABLE EXPERIMENT ADD CONSTRAINT EXP_GROUP FOREIGN KEY (RESEARCH_GROUP_ID) REFERENCES RESEARCH_GROUP (RESEARCH_GROUP_ID)
;

ALTER TABLE ARTICLES ADD CONSTRAINT ARTICLE_GROUP FOREIGN KEY (RESEARCH_GROUP_ID) REFERENCES RESEARCH_GROUP (RESEARCH_GROUP_ID)
;

ALTER TABLE PERSON ADD CONSTRAINT DEFAULT_GROUP FOREIGN KEY (DEFAULT_GROUP_ID) REFERENCES RESEARCH_GROUP (RESEARCH_GROUP_ID)
;

ALTER TABLE GROUP_PERMISSION_REQUEST ADD CONSTRAINT GROUP_PERM_GROUP FOREIGN KEY (RESEARCH_GROUP_ID) REFERENCES RESEARCH_GROUP (RESEARCH_GROUP_ID)
;

ALTER TABLE ARTICLES_COMMENTS ADD CONSTRAINT ARTICLE_COMM_ARTICLE FOREIGN KEY (ARTICLE_ID) REFERENCES ARTICLES (ARTICLE_ID)
;

ALTER TABLE ARTICLES_SUBSCRIBTIONS ADD CONSTRAINT ARTICLE_SUBSCR_ARTICLE FOREIGN KEY (ARTICLE_ID) REFERENCES ARTICLES (ARTICLE_ID)
;

ALTER TABLE ARTICLES_COMMENTS ADD CONSTRAINT ARTICLE_COMM_PARENT FOREIGN KEY (PARENT_ID) REFERENCES ARTICLES_COMMENTS (COMMENT_ID)
;