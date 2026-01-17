package com.cloudwaer.flowable.serve.service.impl;

import com.cloudwaer.flowable.serve.entity.WfNodeAction;
import com.cloudwaer.flowable.serve.service.BpmnDeployService;
import com.cloudwaer.flowable.serve.service.WfNodeActionService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.DeploymentBuilder;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * BPMN部署服务实现类
 *
 * @author cloudwaer
 * @since 2026-01-15
 */
@Slf4j
@Service
public class BpmnDeployServiceImpl implements BpmnDeployService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private WfNodeActionService nodeActionService;

    @Override
    public Deployment deployBpmn(String bpmnXml, String deploymentName, String category) {
        try {
            log.info("部署BPMN流程: 名称={}, 分类={}", deploymentName, category);

            // 验证BPMN XML
            if (!validateBpmnXml(bpmnXml)) {
                throw new RuntimeException("BPMN XML格式验证失败");
            }

            // 创建部署构建器
            DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                    .name(deploymentName)
                    .category(category)
                    .addInputStream(deploymentName + ".bpmn20.xml",
                            new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));

            // 执行部署
            Deployment deployment = deploymentBuilder.deploy();

            log.info("BPMN流程部署成功: 部署ID={}, 名称={}", deployment.getId(), deployment.getName());

            return deployment;

        } catch (Exception e) {
            log.error("部署BPMN流程异常: {}", e.getMessage(), e);
            throw new RuntimeException("部署BPMN流程失败", e);
        }
    }

    @Override
    public List<String> extractNodeIds(String bpmnXml) {
        List<String> nodeIds = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));

            // 提取所有用户任务节点
            NodeList userTasks = document.getElementsByTagName("userTask");
            for (int i = 0; i < userTasks.getLength(); i++) {
                Element userTask = (Element) userTasks.item(i);
                String id = userTask.getAttribute("id");
                if (StringUtils.hasText(id)) {
                    nodeIds.add(id);
                }
            }

            // 提取服务任务节点
            NodeList serviceTasks = document.getElementsByTagName("serviceTask");
            for (int i = 0; i < serviceTasks.getLength(); i++) {
                Element serviceTask = (Element) serviceTasks.item(i);
                String id = serviceTask.getAttribute("id");
                if (StringUtils.hasText(id)) {
                    nodeIds.add(id);
                }
            }

            log.info("从BPMN XML中提取到 {} 个节点", nodeIds.size());

        } catch (Exception e) {
            log.error("提取节点ID异常: {}", e.getMessage(), e);
        }

        return nodeIds;
    }

    @Override
    public List<String> extractUserTaskIds(String bpmnXml) {
        List<String> userTaskIds = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));

            // 只提取用户任务节点
            NodeList userTasks = document.getElementsByTagName("userTask");
            for (int i = 0; i < userTasks.getLength(); i++) {
                Element userTask = (Element) userTasks.item(i);
                String id = userTask.getAttribute("id");
                String name = userTask.getAttribute("name");
                if (StringUtils.hasText(id)) {
                    userTaskIds.add(id);
                    log.debug("提取用户任务: ID={}, 名称={}", id, name);
                }
            }

            log.info("从BPMN XML中提取到 {} 个用户任务节点", userTaskIds.size());

        } catch (Exception e) {
            log.error("提取用户任务ID异常: {}", e.getMessage(), e);
        }

        return userTaskIds;
    }

    @Override
    public Deployment deployBpmnWithActions(String bpmnXml, String deploymentName,
                                          String category, List<WfNodeAction> nodeActions) {
        try {
            // 先部署BPMN
            Deployment deployment = deployBpmn(bpmnXml, deploymentName, category);

            // 获取流程定义信息
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .deploymentId(deployment.getId())
                    .latestVersion()
                    .singleResult();

            if (processDefinition != null && nodeActions != null && !nodeActions.isEmpty()) {
                // 设置模型信息到节点动作
                for (WfNodeAction action : nodeActions) {
                    action.setModelKey(processDefinition.getKey());
                    action.setModelVersion(processDefinition.getVersion());
                }

                // 批量保存节点动作配置
                boolean success = nodeActionService.batchSaveNodeActions(nodeActions);
                if (success) {
                    log.info("成功保存 {} 个节点动作配置", nodeActions.size());
                } else {
                    log.warn("保存节点动作配置失败");
                }
            }

            return deployment;

        } catch (Exception e) {
            log.error("部署带节点配置的BPMN流程异常: {}", e.getMessage(), e);
            throw new RuntimeException("部署BPMN流程失败", e);
        }
    }

    @Override
    public Deployment updateBpmn(String bpmnXml, String deploymentName, String category, String processKey) {
        try {
            log.info("更新BPMN流程: Key={}, 名称={}", processKey, deploymentName);

            // 查找现有部署
            List<Deployment> existingDeployments = repositoryService.createDeploymentQuery()
                    .processDefinitionKey(processKey)
                    .orderByDeploymentTime()
                    .desc()
                    .list();

            // 如果存在现有部署，先删除
            if (!existingDeployments.isEmpty()) {
                for (Deployment deployment : existingDeployments) {
                    repositoryService.deleteDeployment(deployment.getId(), true);
                    log.info("删除旧部署: {}", deployment.getId());
                }
            }

            // 部署新版本
            return deployBpmn(bpmnXml, deploymentName, category);

        } catch (Exception e) {
            log.error("更新BPMN流程异常: {}", e.getMessage(), e);
            throw new RuntimeException("更新BPMN流程失败", e);
        }
    }

    @Override
    public boolean deleteDeployment(String deploymentId) {
        try {
            repositoryService.deleteDeployment(deploymentId, true);
            log.info("删除部署成功: {}", deploymentId);
            return true;
        } catch (Exception e) {
            log.error("删除部署异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Deployment getDeployment(String deploymentId) {
        try {
            return repositoryService.createDeploymentQuery()
                    .deploymentId(deploymentId)
                    .singleResult();
        } catch (Exception e) {
            log.error("获取部署详情异常: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean validateBpmnXml(String bpmnXml) {
        try {
            if (!StringUtils.hasText(bpmnXml)) {
                return false;
            }

            // 基本XML格式验证
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(bpmnXml.getBytes(StandardCharsets.UTF_8)));

            // 检查是否包含definitions根元素
            Element root = document.getDocumentElement();
            if (!"definitions".equals(root.getTagName())) {
                log.warn("BPMN XML根元素不是definitions");
                return false;
            }

            // 检查是否包含process元素
            NodeList processes = root.getElementsByTagName("process");
            if (processes.getLength() == 0) {
                log.warn("BPMN XML未找到process元素");
                return false;
            }

            log.info("BPMN XML验证通过");
            return true;

        } catch (Exception e) {
            log.error("BPMN XML验证异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String generateProcessDiagram(String processDefinitionId) {
        try {
            // 生成流程图
            InputStream diagramStream = repositoryService.getProcessDiagram(processDefinitionId);

            if (diagramStream != null) {
                // 这里可以将流程图转换为SVG或Base64
                // 简化实现，返回SVG占位符
                return "<svg>Process Diagram</svg>";
            }

            return null;

        } catch (Exception e) {
            log.error("生成流程图异常: {}", e.getMessage(), e);
            return null;
        }
    }
}
