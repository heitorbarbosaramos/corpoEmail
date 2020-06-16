package com.heitor.corpoEmail.modeloMain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Message;

import com.heitor.corpoEmail.CorpoEmail1;
import com.heitor.corpoEmail.enviaEmail.Destinatario;
import com.heitor.corpoEmail.enviaEmail.DisparaEmail;

public class ModeloMain {

	public static void main(String[] args) {

		DisparaEmail email = new DisparaEmail();

		List<Destinatario> destinatarios = new ArrayList<Destinatario>();

		destinatarios.add(new Destinatario("heitorhfbr@gmail.com", Message.RecipientType.TO, "Heitor"));

		List<File> anexo = new ArrayList<File>();
		anexo.add(new File("/home/heitor/Downloads/06-bonus-git-github (1).pdf"));
		
		try {
			email.enviarEmail("Teste Email", CorpoEmail1.corpoEmail("AQUI DENTRO VAIO CORPO DO EMAIL<br/><li>Teste</li>", "TESTE ASSUNTO SUBTITULO"),destinatarios, null, "noreply@email.com.br");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
