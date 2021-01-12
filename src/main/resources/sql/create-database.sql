-- noinspection SqlNoDataSourceInspectionForFile

create Table example(
    some_id int,
    some_text varchar(64)
);

insert into example (some_id, some_text) values ('1', 'normal text');
insert into example (some_id, some_text) values ('2', 'end with backslash\');
insert into example (some_id, some_text) values ('3', 'some other normal text');
