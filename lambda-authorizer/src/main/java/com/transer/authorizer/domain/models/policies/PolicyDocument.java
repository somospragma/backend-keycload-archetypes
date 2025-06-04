package com.transer.authorizer.domain.models.policies;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.ArrayList;
import java.util.List;

@JsonDeserialize(builder = PolicyDocument.Builder.class)
public class PolicyDocument {

  @JsonProperty("Version")
  public final String version;

  @JsonProperty("Statement")
  public List<Statement> statement;

  private PolicyDocument(Builder builder) {
    this.version = builder.version;
    this.statement = builder.statements;
  }

  public static Builder builder() {
    return new Builder();
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final String version;
    private List<Statement> statements;

    private Builder() {
      this.version = "2012-10-17";
      this.statements = new ArrayList<>();
    }

    public Builder statements(List<Statement> statements) {
      this.statements = statements;
      return this;
    }

    public PolicyDocument build() {
      return new PolicyDocument(this);
    }
  }
}
