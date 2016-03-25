package net.bons.comptes.service;

import com.google.inject.Inject;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.MailResult;
import io.vertx.rxjava.ext.mail.MailClient;
import net.bons.comptes.service.model.Contribution;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class MailService {
    private static final Logger LOG = LoggerFactory.getLogger(MailService.class);
    private MailClient mailClient;
    private JsonObject configuration;
    private final Configuration cfg;
    private String fromUser;
    private static final Handler<AsyncResult<MailResult>> defaultResult = result -> {
        if (result.succeeded()) {
            LOG.info("Send mail : {}", result.result());
        } else {
            LOG.error("Error during sending mail", result.cause());
        }
    };

    @Inject
    public MailService(MailClient mailClient, JsonObject configuration) {
        this.mailClient = mailClient;
        this.configuration = configuration;
        this.fromUser = configuration.getJsonObject("mail").getString("user");

        cfg = new Configuration(Configuration.VERSION_2_3_23);
        cfg.setClassLoaderForTemplateLoading(this.getClass().getClassLoader(), "/mail-template");
        cfg.setDefaultEncoding("UTF-8");

        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

    public void sendCreatedProject(RawProject rawProject) {
        MailMessage message = new MailMessage();
        message.setFrom(fromUser);
        message.setTo(rawProject.getMail());

        Map<String, Object> root = new HashMap<>();
        root.put("project", rawProject);
        root.put("base_url", configuration.getString("base_url"));

        String subjectTemplate = "Création du projet : ${project.name} !";
        String templateName = "new_project.ftl";
        sendMail(root, message, subjectTemplate, templateName, defaultResult);
    }

    public void sendNewContribution(RawProject rawProject, Contribution contribution) {
        MailMessage message = new MailMessage();
        message.setFrom(fromUser);
        message.setTo(contribution.getMail());

        Map<String, Object> root = new HashMap<>();
        root.put("project", rawProject);
        root.put("contribution", contribution);
        root.put("base_url", configuration.getString("base_url"));

        sendMail(root, message, "Merci de contribuer au projet : ${project.name} !", "new_contrib.ftl", defaultResult);

        MailMessage notification = new MailMessage();
        notification.setFrom(configuration.getJsonObject("mail").getString("user"));
        notification.setTo(rawProject.getMail());

        LOG.info("Send notification");
        sendMail(root, notification, "Nouvelle contribution au projet ${project.name}", "notification_new_contrib.ftl",
                 defaultResult);
    }

    public void sendRelance(RawProject rawProject, Contribution contribution, Handler<AsyncResult<MailResult>> result) {
        MailMessage message = new MailMessage();
        message.setFrom(fromUser);
        message.setTo(contribution.getMail());

        Map<String, Object> root = new HashMap<>();
        root.put("project", rawProject);
        root.put("contribution", contribution);
        root.put("base_url", configuration.getString("base_url"));

//        sendMail(root, message, "Relance du projet : ${project.name}", "remind.ftl", result);
        result.handle(new AsyncResult<MailResult>() {
            @Override
            public MailResult result() {
                return null;
            }

            @Override
            public Throwable cause() {
                return new RuntimeException("J'ai niqué ta mère !");
            }

            @Override
            public boolean succeeded() {
                return false;
            }

            @Override
            public boolean failed() {
                return false;
            }
        });
    }

    private void sendMail(Map<String, Object> root, MailMessage message, String subjectTemplate, String templateName,
                          Handler<AsyncResult<MailResult>> result) {
        try {
            Template temp = cfg.getTemplate(templateName);
            StringWriter out = new StringWriter();
            temp.process(root, out);

            Template object = new Template("subject", subjectTemplate, cfg);
            StringWriter outSuject = new StringWriter();
            object.process(root, outSuject);

            message.setSubject(outSuject.toString());
            message.setText(out.toString());

            mailClient.sendMail(message, result);
        } catch (IOException |TemplateException e) {
            LOG.error("Error during sending mail", e);
        }
    }
}
