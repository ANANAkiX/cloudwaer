package com.cloudwaer.flowable.serve.action;

public interface ActionExecutor {

	String getType();

	void execute(ActionContext context, String config);

}
