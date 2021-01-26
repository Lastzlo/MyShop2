delete from linked_directory;
delete from product;

insert into linked_directory(id, directory_type, name) values
(1, 'PARAMETER', 'memory_size');
insert into linked_directory(id, directory_type, name) values
(2, 'PARAMETER', 'color');
insert into linked_directory(id, directory_type, name) values
(4, 'PARAMETER_LIST', 'color');

insert into product(id, product_name) values
(3, 'Apple iPhone 10');

alter sequence hibernate_sequence restart with 10;