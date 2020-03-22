package br.com.tadeudeveloper.springboot.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import br.com.tadeudeveloper.springboot.model.Pessoa;

@Repository
@Transactional
public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
	
	@Query("select p from Pessoa p where p.nome like %?1%")
	List<Pessoa> findByName(String nome);	
	
	@Query("select p from Pessoa p where p.nome like %?1% and p.sexo = ?2")
	List<Pessoa> findByNameSexo(String nome, String sexo);	
	
	@Query("select p from Pessoa p where p.sexo = ?1")
	List<Pessoa> findBySexo(String sexo);
	
	// PAGINAÇÃO (Pesquisa de Pessoa - NOME)
	default Page<Pessoa> findByNamePage(String nome, Pageable pageable) {
		
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		
		// Configurando pesquisa para consulta por partes do nome no banco (análogo ao LIKE do SQL)
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		// Une objeto com valor e a configuração para consultar
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher);
		
		return findAll(example, pageable);		
	}
	
	// PAGINAÇÃO (Pesquisa de Pessoa - SEXO	)
	default Page<Pessoa> findBySexoPage(String sexo, Pageable pageable) {
		
		Pessoa pessoa = new Pessoa();
		pessoa.setSexo(sexo);
		
		// Configurando pesquisa para consulta por partes do nome no banco (análogo ao LIKE do SQL)
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("sexo", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		// Une objeto com valor e a configuração para consultar
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher);
		
		return findAll(example, pageable);		
	}
	
	// PAGINAÇÃO (Pesquisa de Pessoa - NOME e SEXO)
	default Page<Pessoa> findByNameSexoPage(String nome, String sexo, Pageable pageable) {
		
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		pessoa.setSexo(sexo);
		
		// Configurando pesquisa para consulta por partes do nome no banco (análogo ao LIKE do SQL)
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny()
				.withMatcher("nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
				.withMatcher("sexo", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		
		// Une objeto com valor e a configuração para consultar
		Example<Pessoa> example = Example.of(pessoa, exampleMatcher);
		
		return findAll(example, pageable);		
	}

}
