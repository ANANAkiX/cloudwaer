package com.cloudwaer.flowable.serve.action;

import com.cloudwaer.flowable.serve.entity.WfNodeAction;

import java.util.List;

public interface ActionDispatcher {

    void dispatch(ActionContext context, List<WfNodeAction> actions);
}
