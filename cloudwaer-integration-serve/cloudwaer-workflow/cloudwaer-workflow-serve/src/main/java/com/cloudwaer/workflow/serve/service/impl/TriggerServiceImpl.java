package com.cloudwaer.workflow.serve.service.impl;

import com.cloudwaer.workflow.serve.engine.ProcessEngine;
import com.cloudwaer.workflow.serve.service.TriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TriggerServiceImpl implements TriggerService {

	@Autowired
	private ProcessEngine processEngine;

	@Override
	public void fire(String instanceId, String nodeId) {
		log.info("触发器唤醒: instanceId={}, nodeId={}", instanceId, nodeId);
		processEngine.resumeFromNode(instanceId, nodeId);
	}

}
