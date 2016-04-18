package net.bons.comptes.cqrs.command;

import io.vertx.core.json.JsonObject;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 *
 */
public class UpdateProjectCommand implements Command {
    @NotNull
    @NotBlank(message = "L'autheur ne peut être vide")
    private String fieldName;
    @NotNull @NotBlank
    private String oldValue;
    @NotNull @NotBlank(message = "La nouvelle valeur ne peut être vide")
    private String newValue;

    public UpdateProjectCommand(String fieldName, String oldValue, String newValue) {
        this.fieldName = fieldName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public UpdateProjectCommand(JsonObject json) {
        this.fieldName = json.getString("field");
        this.oldValue = json.getString("oldValue");
        this.newValue = json.getString("newValue");
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateProjectCommand that = (UpdateProjectCommand) o;
        return Objects.equals(fieldName, that.fieldName) &&
                Objects.equals(oldValue, that.oldValue) &&
                Objects.equals(newValue, that.newValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName, oldValue, newValue);
    }
}
