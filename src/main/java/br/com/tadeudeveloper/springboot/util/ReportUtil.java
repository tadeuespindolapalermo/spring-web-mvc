package br.com.tadeudeveloper.springboot.util;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Component
public class ReportUtil<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// Retorna PDF em Byte para download no navegador
	public byte[] gerarRelatorio(List<T> listDados, String relatorio,
			ServletContext servletContext) throws Exception {
		
		// Cria a lista de dados para o relatório com a lista de objetos para imprimir
		JRBeanCollectionDataSource jrbcds = new JRBeanCollectionDataSource(listDados);
		
		// Carrega o caminho do arquivo jaspet compilado
		String caminhoJasper = servletContext.getRealPath("relatorios") 
				+ File.separator + relatorio + ".jasper" ;
		
		// Carrega o arquivo Jasper passando os dados
		JasperPrint impressoraJasper = JasperFillManager.fillReport(caminhoJasper, new HashMap<>(), jrbcds);
		
		// Esporta para byte[] para fazer o download do pdf
		return JasperExportManager.exportReportToPdf(impressoraJasper);		
	}
	
}
