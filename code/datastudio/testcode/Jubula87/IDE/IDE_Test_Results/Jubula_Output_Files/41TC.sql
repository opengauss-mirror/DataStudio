create table if not exists public.test (id int,name varchar2(200));
select * from public.test a 
where a.id > 1
limit 100;
