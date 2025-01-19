package com.hvs.musicreleasenotifierprocessmanager.newreleasesprocess.delegate;

import com.hvs.musicreleasenotifierprocessmanager.user.dto.UserDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@SuppressWarnings("unused")
public class SendFetchingErrorMessageDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        RuntimeService runtimeService = execution.getProcessEngineServices().getRuntimeService();

        UserDto user = (UserDto) execution.getVariable("user");
        Map<String, Object> variables = new HashMap<>();
        variables.put("artistId", execution.getVariable("artistId"));
        variables.put("username", user.getUsername());

        runtimeService.createMessageCorrelation("FetchingErrorMessage")
                .processInstanceId(execution.getProcessInstanceId())
                .setVariables(variables)
                .correlate();
    }
}