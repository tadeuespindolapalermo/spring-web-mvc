package br.com.tadeudeveloper.springboot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebConfigSecurity extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ImplementacaoUserDetailsService implementacaoUserDetailsService;
	
	@Override // Configura as solicitações de acesso por Http
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf()
		  .disable() // Desativa as configurações padrão de memória
		  .authorizeRequests() // Permite restringir acesso
		  .antMatchers(HttpMethod.GET, "/").permitAll() // Qualquer usuário acessa a página inicial
		  .antMatchers(HttpMethod.GET, "/cadastroPessoa").hasAnyRole("ADMIN")
		  //.antMatchers("/materialize/").permitAll()
		  .anyRequest().authenticated()
		  .and().formLogin().permitAll() // permite qualquer usuário
		  .loginPage("/login") // página de login
		  .defaultSuccessUrl("/cadastroPessoa") // página padrão se efetuou o login
		  .failureUrl("/login?error=true") // página padrão se falhou o login
		  .and().logout() // Mapeia URL de Logout e invalida usuário autenticado
		  .logoutSuccessUrl("/login") // página padrão após fazer o logout
		  .logoutRequestMatcher(new AntPathRequestMatcher("/logout"));
	}
	
	@Override // Cria autenticação do usuário com banco de dados ou em memória
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		
		// AUTENTICAÇÃO VIA BANCO DE DADOS
		auth.userDetailsService(implementacaoUserDetailsService)
		.passwordEncoder(new BCryptPasswordEncoder());
		
		// AUTENTICAÇÃO EM MEMÓRIA
		/*auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
		.withUser("tadeu")
		.password("$2a$10$Q/AyB6h3cIT2QVaTvejBruAV7xoQLFdi7tRm2aAA0URqDxVJj6Lvm") // 123
		.roles("ADMIN");*/
	}
	
	@Override // Ignora URL's específicas
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/materialize/**");
	}

}
