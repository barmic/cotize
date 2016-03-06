package net.bons.comptes.service;

import com.google.inject.Inject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailMessage;
import io.vertx.rxjava.ext.mail.MailClient;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class MailService {
    private MailClient mailClient;
    private JsonObject configuration;
    private final Configuration cfg;

    @Inject
    public MailService(MailClient mailClient, JsonObject configuration) {
        this.mailClient = mailClient;
        this.configuration = configuration;

        cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/mail-template");
        cfg.setDefaultEncoding("UTF-8");

        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void sendCreatedProject(RawProject rawProject) {
        MailMessage message = new MailMessage();
        message.setFrom(configuration.getJsonObject("mail").getString("user"));
        message.setTo(rawProject.getMail());

        Map<String, Object> root = new HashMap<>();
        root.put("project", rawProject);
        root.put("base_url", configuration.getString("base_url"));

        try {
            Template temp = cfg.getTemplate("new_project.ftl");
            StringWriter out = new StringWriter();
            temp.process(root, out);

            Template object = new Template("subject", "Création du projet : ${project.name} !", cfg);
            StringWriter outSuject = new StringWriter();
            object.process(root, outSuject);

            message.setSubject(outSuject.toString());
            message.setText(out.toString());

            mailClient.sendMail(message, result -> {
                if (result.succeeded()) {
                    System.out.println(result.result());
                } else {
                    result.cause().printStackTrace();
                }
            });
        } catch (IOException|TemplateException e) {
            e.printStackTrace();
        }
    }

    public void sendNewContribution(RawProject rawProject, Contribution contribution) {
        MailMessage message = new MailMessage();
        message.setFrom(configuration.getJsonObject("mail").getString("user"));
        message.setTo(rawProject.getMail());

        Map<String, Object> root = new HashMap<>();
        root.put("project", rawProject);
        root.put("contribution", contribution);
        root.put("base_url", configuration.getString("base_url"));

        try {
            Template temp = cfg.getTemplate("new_contrib.ftl");
            StringWriter out = new StringWriter();
            temp.process(root, out);

            Template object = new Template("subject", "Merci de contribuer au projet : ${project.name} !", cfg);
            StringWriter outSuject = new StringWriter();
            object.process(root, outSuject);

            message.setSubject(outSuject.toString());
            message.setText(out.toString());

            mailClient.sendMail(message, result -> {
                if (result.succeeded()) {
                    System.out.println(result.result());
                } else {
                    result.cause().printStackTrace();
                }
            });
        } catch (IOException|TemplateException e) {
            e.printStackTrace();
        }
    }

    public void sendRelance(RawProject rawProject, Contribution contribution) {
        MailMessage message = new MailMessage();
        message.setFrom(configuration.getJsonObject("mail").getString("user"));
        message.setTo(rawProject.getMail());

        Map<String, Object> root = new HashMap<>();
        root.put("project", rawProject);
        root.put("contribution", contribution);
        root.put("base_url", configuration.getString("base_url"));

        try {
            Template temp = cfg.getTemplate("remind.ftl");
            StringWriter out = new StringWriter();
            temp.process(root, out);

            Template object = new Template("subject", "Relance du projet : ${project.name}", cfg);
            StringWriter outSuject = new StringWriter();
            object.process(root, outSuject);

            message.setSubject(outSuject.toString());
            message.setText(out.toString());

            mailClient.sendMail(message, result -> {
                if (result.succeeded()) {
                    System.out.println(result.result());
                } else {
                    result.cause().printStackTrace();
                }
            });
        } catch (IOException|TemplateException e) {
            e.printStackTrace();
        }
    }
}
