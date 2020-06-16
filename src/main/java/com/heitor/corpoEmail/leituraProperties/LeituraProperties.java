package com.heitor.corpoEmail.leituraProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LeituraProperties {
	
	public Properties getProperties(String nomeArquivo) {

		Properties props = new Properties();

		try {
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(nomeArquivo + ".properties");

			props.load(inputStream);
		} catch (IOException ex) {
			System.out.println("getProperties() - " + ex.getMessage());
		}

		return props;
	}

}
