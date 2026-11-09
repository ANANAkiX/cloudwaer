package com.cloudwaer.workflow.serve.engine.listener;

/**
 * 节点级监听器标记：仅在节点 listenerJson 显式配置时执行， 不参与全局事件自动分发（全局分发见 {@link EventPublisher}）。
 */
public interface NodeScopedListener extends WorkflowEventListener {

}
