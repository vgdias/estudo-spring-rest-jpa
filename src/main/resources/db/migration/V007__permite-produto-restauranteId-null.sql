alter table produto
drop foreign key fk_produto_restaurante;

alter table produto
modify column restaurante_id bigint(20);

alter table produto
add constraint fk_produto_restaurante
foreign key (restaurante_id)
references restaurante (id);