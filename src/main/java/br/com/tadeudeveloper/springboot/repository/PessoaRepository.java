package br.com.tadeudeveloper.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.tadeudeveloper.springboot.model.Pessoa;

@Repository
@Transactional
public interface PessoaRepository extends CrudRepository<Pessoa, Long> {
	
	@Query("select p from Pessoa p where p.nome like %?1%")
	List<Pessoa> findByName(String nome);	
	
	@Query("select p from Pessoa p where p.nome like %?1% and p.sexo = ?2")
	List<Pessoa> findByNameSexo(String nome, String sexo);	
	
	@Query("select p from Pessoa p where p.sexo = ?1")
	List<Pessoa> findBySexo(String sexo);	

}
