# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table exercise (
  id                        bigint not null,
  name                      varchar(255),
  minutes                   integer,
  constraint pk_exercise primary key (id))
;

create sequence exercise_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists exercise;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists exercise_seq;

