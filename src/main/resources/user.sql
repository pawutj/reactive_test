CREATE SCHEMA "app";
CREATE TABLE "app"."user" (
    "id" UUID NOT NULL,
    "username" varchar(50) NOT NULL,
    "first_name" varchar(50) NOT NULL,
    "last_name" varchar(50) NOT NULL,
    PRIMARY KEY ("id")
);

INSERT INTO app."user" (id,username,first_name,last_name) VALUES
	 ('01283a67-126f-453c-953d-81019b813db4','test','test','test'),
	 ('001f2b99-64ed-4ca5-90a0-8acdb115eac2','test1','test1','te1st');
