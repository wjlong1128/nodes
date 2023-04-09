create table emp (
    empno int primary key auto_increment,
    ename varchar(50) not null,
    job varchar(50),
    sal DECIMAL(10,2)
);

insert into emp value(null,'张三','经理','10000.00');
insert into emp value(null,'李四','经副理','90000.00');