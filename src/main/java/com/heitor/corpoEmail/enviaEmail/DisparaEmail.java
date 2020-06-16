package com.heitor.corpoEmail.enviaEmail;

import java.io.File;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.heitor.corpoEmail.leituraProperties.LeituraProperties;


/**
 * Classe realiza o envio do email
 *
 * @author Heitor Ramos
 */
public class DisparaEmail {

    private Properties props;
    private Session sessao;
    
    LeituraProperties load = new LeituraProperties();
	Properties properties = load.getProperties("configEmailEnvia");
    

    /**
     * Construtor carrega as Properties padrões
     *
     */
    public DisparaEmail() {
        getSession();
    }

    /**
     * Construtor recebe parametros de conexão EX: props = new Properties();
     * props.put("mail.smtp.host", "ssl://email-smtp.us-east-1.amazonaws.com");
     * props.put("mail.smtp.auth", "false");
     * props.put("mail.smtp.starttls.enable", "true");
     *
     * @param props
     */
    public DisparaEmail(Properties props) {
        sessao = null;
        this.props = props;
    }

    private void getSession() {
        sessao = null;
        props = new Properties();
        /**
         * Parâmetros de conexão com servidor Gmail
         */

        props.setProperty("mail.transport.protocol", properties.getProperty("mail.transport.protocol"));
        props.setProperty("mail.host", properties.getProperty("mail.host"));
        props.put("mail.smtp.socketFactory.port", properties.getProperty("mail.smtp.socketFactory.port"));
        props.put("mail.smtp.socketFactory.class", properties.getProperty("mail.smtp.socketFactory.class"));
        props.put("mail.session.mail.smtp.ssl.enable", properties.getProperty("mail.session.mail.smtp.ssl.enable"));
        props.put("mail.smtp.auth", properties.getProperty("mail.smtp.auth"));
        props.put("mail.smtp.port", properties.getProperty("mail.smtp.port"));

    }

    /**
     * Realiza o envio do e-mail
     *
     * @param assunto Preenchimento obrigatório
     * @param body Pode utlizar HTML ou texto normal
     * @param destinatarios Utlizar List<Destinatario>
     * @param anexo Utilizar List<File>
     * @param emailOrigem Null para e-mail padrão "noreply@metaslt.com.br"
     *
     * @return verdade para envio com sucesso e falso para erro no envio.
     *
     */
    public Boolean enviarEmail(String assunto, String body, List<Destinatario> destinatarios, List<File> anexo, String emailOrigem) {
        sessao = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        
                        return new PasswordAuthentication(properties.getProperty("config.email.origem"), properties.getProperty("config.email.senha"));
                    }
                });
        //debug
        sessao.setDebug(false);
        Message message = new MimeMessage(sessao);
        // Estipula quem esta enviando  
        InternetAddress from;
        try {
            //EMAIL ORIGEM
            if (emailOrigem != null) {
                from = new InternetAddress(emailOrigem);
            } else {
                from = new InternetAddress("noreply@email.com.br");
            }
            message.setFrom(from);
            for (Destinatario destino : destinatarios) {
                try {
                    message.addRecipient(destino.getRecipiente(), new InternetAddress((String) destino.getEmail(), (String) destino.getNome()));
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(DisparaEmail.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            message.setSentDate(new Date());
            message.setSubject(assunto);

            MimeMultipart mpRoot = new MimeMultipart("mixed");
            MimeMultipart mpContent = new MimeMultipart("alternative");
            MimeBodyPart contentPartRoot = new MimeBodyPart();
            contentPartRoot.setContent(mpContent);
            mpRoot.addBodyPart(contentPartRoot);

            //enviando texto  
            MimeBodyPart mbp1 = new MimeBodyPart();
            mbp1.setText("Mensagem automatica.");
            mpContent.addBodyPart(mbp1);

            //enviando html  
            MimeBodyPart mbp2 = new MimeBodyPart();
            mbp2.setContent(body, "text/html;charset=iso-8859-1");
            mpContent.addBodyPart(mbp2);

            //enviando anexo  
            if (anexo != null) {
                for (File f : anexo) {
                    MimeBodyPart mbp3 = new MimeBodyPart();
                    DataSource fds = new FileDataSource(f);
                    mbp3.setDisposition(Part.ATTACHMENT);
                    mbp3.setDataHandler(new DataHandler(fds));
                    mbp3.setFileName(f.getName());

                    mpRoot.addBodyPart(mbp3);
                }
            }
            message.setContent(mpRoot);
            message.saveChanges();

            Transport.send(message);
        } catch (MessagingException ex) {
            Logger.getLogger(DisparaEmail.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    
}
