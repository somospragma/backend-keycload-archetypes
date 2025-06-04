package com.transer.authorizer.domain.models.policies;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Statement.Builder.class)
public class Statement {

  @JsonProperty("Action")
  public String action;

  @JsonProperty("Effect")
  public String effect;

  @JsonProperty("Resource")
  public String resource;

  private Statement(Builder builder) {
    this.action = builder.action;
    this.effect = builder.effect;
    this.resource = builder.resource;
  }

  public static Builder builder() {
    return new Builder();
  }

  @JsonPOJOBuilder(withPrefix = "")
  public static final class Builder {

    private final String action;
    private String effect;
    private String resource;

    private Builder() {
      action = "execute-api:Invoke";
    }

    public Builder effect(String effect) {
      this.effect = effect;
      return this;
    }

    public Builder resource(String resource) {
      this.resource = resource;
      return this;
    }

    public Statement build() {
      return new Statement(this);
    }
  }
}
