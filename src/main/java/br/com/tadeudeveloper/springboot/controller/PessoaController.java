package br.com.tadeudeveloper.springboot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import br.com.tadeudeveloper.springboot.model.Pessoa;
import br.com.tadeudeveloper.springboot.model.Telefone;
import br.com.tadeudeveloper.springboot.repository.PessoaRepository;
import br.com.tadeudeveloper.springboot.repository.ProfissaoRepository;
import br.com.tadeudeveloper.springboot.repository.TelefoneRepository;
import br.com.tadeudeveloper.springboot.util.ReportUtil;

@Controller
public class PessoaController {
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private ReportUtil<Pessoa> reportUtil;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;

	@RequestMapping(method = RequestMethod.GET, value = "/cadastroPessoa")
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");		
		modelAndView.addObject("pessoaObj", new Pessoa());
		Iterable<Pessoa> pessoaIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoaIterable);	
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "**/salvaPessoa")
	public ModelAndView salvar(@Valid Pessoa pessoa, BindingResult bindingResult) {
		
		// @Valid para ativar a validação e BindingResult para captar os resultados
		
		// carrega os telefone da pessoa
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		if(bindingResult.hasErrors()) {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
			Iterable<Pessoa> pessoaIterable = pessoaRepository.findAll();
			modelAndView.addObject("pessoas", pessoaIterable);		
			modelAndView.addObject("pessoaObj", pessoa);	
			
			List<String> msg = new ArrayList<>();
			for(ObjectError objError : bindingResult.getAllErrors()) {
				msg.add(objError.getDefaultMessage()); // vem das anotações de validação
			}
			
			modelAndView.addObject("msg", msg);
			modelAndView.addObject("profissoes", profissaoRepository.findAll());
			return modelAndView;
		}
		
		pessoaRepository.save(pessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		Iterable<Pessoa> pessoaIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoaIterable);		
		modelAndView.addObject("pessoaObj", new Pessoa());
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/listaPessoas")
	public ModelAndView pessoas() {
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		Iterable<Pessoa> pessoaIterable = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoaIterable);
		modelAndView.addObject("pessoaObj", new Pessoa());
		return modelAndView;		
	}
	
	@GetMapping("/editarPessoa/{idPessoa}")
	public ModelAndView editar(@PathVariable("idPessoa") Long idPessoa) {
		
		Optional<Pessoa> pessoaOptional = pessoaRepository.findById(idPessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		Pessoa pessoa = pessoaOptional.get();
		modelAndView.addObject("pessoaObj", pessoa);
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	@GetMapping("/telefones/{idPessoa}")
	public ModelAndView exibirTelefones(@PathVariable("idPessoa") Long idPessoa) {
		
		Optional<Pessoa> pessoaOptional = pessoaRepository.findById(idPessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		Pessoa pessoa = pessoaOptional.get();
		modelAndView.addObject("pessoaObj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(idPessoa));		
		return modelAndView;
	}
	
	@GetMapping("/removerPessoa/{idPessoa}")
	public ModelAndView excluir(@PathVariable("idPessoa") Long idPessoa) {
		
		pessoaRepository.deleteById(idPessoa);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");		
		modelAndView.addObject("pessoas", pessoaRepository.findAll());
		modelAndView.addObject("pessoaObj", new Pessoa());
		return modelAndView;
	}
	
	@GetMapping("/removerTelefone/{idTelefone}")
	public ModelAndView excluirTelefone(@PathVariable("idTelefone") Long idTelefone) {
		
		Pessoa pessoa = telefoneRepository.findById(idTelefone).get().getPessoa();
		
		telefoneRepository.deleteById(idTelefone);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");		
		modelAndView.addObject("pessoaObj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoa.getId()));
		return modelAndView;
	}
	
	@PostMapping("**/pesquisarPessoa")
	public ModelAndView pesquisar(
			@RequestParam("nomePesquisa") String nomePesquisa,
			@RequestParam("sexoPesquisa") String sexoPesquisa) {
		
		List<Pessoa> pessoas = new ArrayList<>();
		
		if (sexoPesquisa != null && !sexoPesquisa.isEmpty()) {
			pessoas = pessoaRepository.findByNameSexo(nomePesquisa, sexoPesquisa);
		} else {
			pessoas = pessoaRepository.findByName(nomePesquisa);
		}
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastroPessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("pessoaObj", new Pessoa());
		return modelAndView;
	}
	
	@GetMapping("**/pesquisarPessoa")
	public void imprimePDF(
			@RequestParam("nomePesquisa") String nomePesquisa,
			@RequestParam("sexoPesquisa") String sexoPesquisa,
			HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		List<Pessoa> pessoas = new ArrayList<>();
		
		if (sexoPesquisa != null && !sexoPesquisa.isEmpty()
				&& nomePesquisa != null && !nomePesquisa.isEmpty()) {			
			pessoas = pessoaRepository.findByNameSexo(nomePesquisa, sexoPesquisa);			
		} else if (nomePesquisa != null && !nomePesquisa.isEmpty()) {
			pessoas = pessoaRepository.findByName(nomePesquisa);
		} else if (sexoPesquisa != null && !sexoPesquisa.isEmpty()) {
			pessoas = pessoaRepository.findBySexo(sexoPesquisa);
		} else {
			pessoas = (List<Pessoa>) pessoaRepository.findAll();
//			Iterable<Pessoa> iterator = pessoaRepository.findAll();
//			for (Pessoa pessoa : iterator) {
//				pessoas.add(pessoa);
//			}
		}
		
		// Chama serviço que faz a geração do relatório
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		
		// Tamanho da resposta
		response.setContentLength(pdf.length);
		
		// Definir na resposta o tipo de arquivo
		response.setContentType("application/octet-stream");
		
		// Define o cabeçalho da resposta
		String headerKey = "Content-Disposition";		
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		
		// finaliza a resposta para o navegador
		response.getOutputStream().write(pdf);		
	}
	
	@PostMapping("**/addFonePessoa/{pessoaId}")
	public ModelAndView addFonePessoa(Telefone telefone, @PathVariable("pessoaId") Long pessoaId) {
		
		Pessoa pessoa = pessoaRepository.findById(pessoaId).get();
		
		if(telefone != null && telefone.getNumero().isEmpty() || telefone.getTipo().isEmpty()) {
			
			ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
			modelAndView.addObject("pessoaObj", pessoa);
			modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaId));
			
			List<String> msg = new ArrayList<>();
			
			if (telefone.getNumero().isEmpty()) {
				msg.add("NÃºmero deve ser informado!");
			}
			
			if (telefone.getTipo().isEmpty()) {
				msg.add("Tipo deve ser informado!");
			}
						
			modelAndView.addObject("msg", msg);			
			
			return modelAndView;			
		}
		
		ModelAndView modelAndView = new ModelAndView("cadastro/telefones");
		
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);		
		
		modelAndView.addObject("pessoaObj", pessoa);
		modelAndView.addObject("telefones", telefoneRepository.getTelefones(pessoaId));
		return modelAndView;
	}
	
}
