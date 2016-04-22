package net.bons.comptes.cqrs;

import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.collection.Seq;
import net.bons.comptes.cqrs.command.OutgoingCommand;
import net.bons.comptes.cqrs.utils.CommandExtractor;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.Outgoing;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 *
 */
public class OutgoingHandler implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(OutgoingHandler.class);
    private CommandExtractor commandExtractor;
    private ProjectStore projectStore;

    @Inject
    public OutgoingHandler(CommandExtractor commandExtractor, ProjectStore projectStore) {
        this.commandExtractor = commandExtractor;
        this.projectStore = projectStore;
    }
    @Override
    public void handle(RoutingContext context) {
        final String projectId = context.request().getParam("projectId");
        final String adminPass = context.request().getParam("adminPass");

        Function<Tuple2<RawProject, OutgoingCommand>, RawProject> delete = context.normalisedPath().endsWith("/del") ? this::remove : this::add;

        commandExtractor.readQuery(context, OutgoingCommand::new)
                        .flatMap(cmd -> projectStore.loadProject(projectId, adminPass)
                                                    .map(projectJson -> Tuple.of(projectJson, cmd)))
                        .map(delete::apply)
                        .map(project -> {
                            projectStore.updateProject(project);
                            return project;
                        })
                        .subscribe(project -> {
                            context.response()
                                   .putHeader("Content-Type", context.getAcceptableContentType())
                                   .end(project.toJson().toString());
                        }, Utils.manageError(context));
    }

    private RawProject add(Tuple2<RawProject, OutgoingCommand> tuple) {
        Outgoing outgoing = new Outgoing(tuple._2.getAuthor(), tuple._2.getAmount(), tuple._2.getDescription());
        RawProject rawProject = tuple._1;

        Seq<Outgoing> outgoings = rawProject.getOutgoings().append(outgoing);

        return RawProject.builder(rawProject)
                         .outgoings(outgoings)
                         .createRawProject();
    }

    private RawProject remove(Tuple2<RawProject, OutgoingCommand> tuple) {
        Outgoing outgoing = new Outgoing(tuple._2.getAuthor(), tuple._2.getAmount(), tuple._2.getDescription());
        RawProject rawProject = tuple._1;

        Seq<Outgoing> collect = rawProject.getOutgoings()
                                          .filter(out -> !out.equals(outgoing));

        return RawProject.builder(rawProject)
                         .outgoings(collect)
                         .createRawProject();
    }
}
