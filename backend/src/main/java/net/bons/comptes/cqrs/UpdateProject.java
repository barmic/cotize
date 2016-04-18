package net.bons.comptes.cqrs;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import javaslang.Tuple;
import javaslang.Tuple2;
import net.bons.comptes.cqrs.command.UpdateProjectCommand;
import net.bons.comptes.cqrs.utils.CommandExtractor;
import net.bons.comptes.cqrs.utils.Utils;
import net.bons.comptes.service.ProjectStore;
import net.bons.comptes.service.model.RawProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 *
 */
public class UpdateProject implements Handler<RoutingContext> {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateProject.class);
    private CommandExtractor commandExtractor;
    private ProjectStore projectStore;

    @Inject
    public UpdateProject(CommandExtractor commandExtractor, ProjectStore projectStore) {
        this.projectStore = projectStore;
        this.commandExtractor = commandExtractor;
    }

    @Override
    public void handle(RoutingContext context) {
        final String adminPass = context.request().getParam("adminPass");
        final String projectId = context.request().getParam("projectId");

        commandExtractor.readQuery(context, UpdateProjectCommand.class)
                        .flatMap(cmd -> projectStore.loadProject(projectId, adminPass)
                                                    .map(projectJson -> Tuple.of(projectJson, cmd)))
                        .map(this::updateproject)
                        .flatMap(project -> projectStore.updateProject(project))
                        .subscribe(project -> {
                            context.response().end();
                        }, Utils.manageError(context));
    }

    private RawProject updateproject(Tuple2<RawProject, UpdateProjectCommand> params) {
        RawProject.Builder builder = RawProject.builder(params._1);
        switch (params._2.getFieldName()) {
            case "name":
                Preconditions.checkState(Objects.equals(params._1.getName(), params._2.getOldValue()));
                builder.name(params._2.getNewValue());
                break;
            case "description":
                Preconditions.checkState(Objects.equals(params._1.getDescription(), params._2.getOldValue()));
                builder.description(params._2.getNewValue());
                break;
        }
        return builder.createRawProject();
    }
}
