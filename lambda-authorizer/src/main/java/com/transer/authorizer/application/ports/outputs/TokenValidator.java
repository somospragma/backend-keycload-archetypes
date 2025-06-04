package com.transer.authorizer.application.ports.outputs;

import java.util.List;

public interface TokenValidator {

  List<String> validate(String token, String clientId);
}
