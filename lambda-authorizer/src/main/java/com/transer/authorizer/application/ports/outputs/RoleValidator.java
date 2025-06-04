package com.transer.authorizer.application.ports.outputs;

import java.util.List;

public interface RoleValidator {

    boolean validate(List<String> roles, String methodArn);
}
