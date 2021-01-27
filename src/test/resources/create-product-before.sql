delete from linked_directory;
delete from product;

insert into linked_directory(id, directory_type, name) values
(1, 'PARAMETER', 'memory_size');
insert into linked_directory(id, directory_type, name) values
(2, 'PARAMETER', 'color');
insert into linked_directory(id, directory_type, name) values
(3, 'PARAMETER_LIST', 'color');
insert into linked_directory(id, directory_type, name) values
(4, 'CATEGORY', 'color');

insert into product(id, product_name) values
(20, 'Apple iPhone 10');

alter sequence hibernate_sequence restart with 50;