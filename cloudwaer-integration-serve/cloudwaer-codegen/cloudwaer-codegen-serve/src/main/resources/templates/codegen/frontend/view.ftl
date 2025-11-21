<template>
  <div class="${entityNameCamel}-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>${entityComment}管理</span>
          <div style="display: flex; gap: 10px; align-items: center">
<#if searchFields?size gt 0>
            <el-input
              v-model="searchKeyword"
              placeholder="搜索<#list searchFields as searchField>${searchField.columnComment}<#if searchField_has_next>、</#if></#list>"
              clearable
              style="width: 300px"
              @clear="handleSearch"
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
</#if>
            <el-button v-if="canAdd" type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增${entityComment}
            </el-button>
          </div>
        </div>
      </template>

      <el-table
        :data="tableData"
        border
        style="width: 100%"
        v-loading="loading"
      >
<#list formFields as formField>
<#if formField.showInList?? && formField.showInList>
        <el-table-column 
          prop="${formField.fieldName}" 
          label="${formField.label!formField.fieldName}" 
          align="center" 
          <#if formField.listWidth?? && formField.listWidth gt 0>width="${formField.listWidth}"</#if>
        />
</#if>
</#list>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="scope">
            <el-button v-if="canEdit" link type="primary" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button v-if="canDelete" link type="danger" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

<#if enablePagination>
      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
</#if>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="100px"
      >
<#list formFields as formField>
<#if formField.showInForm?? && formField.showInForm>
        <el-form-item label="${formField.label!formField.fieldName}" prop="${formField.fieldName}">
<#if formField.fieldType == "input">
          <el-input
            v-model="formData.${formField.fieldName}"
            placeholder="${formField.placeholder!('请输入' + (formField.label!formField.fieldName))}"
<#if formField.disabled?? && formField.disabled>
            :disabled="!!formData.id"
</#if>
          />
<#elseif formField.fieldType == "textarea">
          <el-input
            v-model="formData.${formField.fieldName}"
            type="textarea"
            :rows="3"
            placeholder="${formField.placeholder!('请输入' + (formField.label!formField.fieldName))}"
          />
<#elseif formField.fieldType == "number">
          <el-input-number
            v-model="formData.${formField.fieldName}"
            style="width: 100%"
            placeholder="${formField.placeholder!('请输入' + (formField.label!formField.fieldName))}"
          />
<#elseif formField.fieldType == "date">
          <el-date-picker
            v-model="formData.${formField.fieldName}"
            type="date"
            style="width: 100%"
            placeholder="${formField.placeholder!('请选择' + (formField.label!formField.fieldName))}"
            value-format="YYYY-MM-DD"
          />
<#elseif formField.fieldType == "datetime">
          <el-date-picker
            v-model="formData.${formField.fieldName}"
            type="datetime"
            style="width: 100%"
            placeholder="${formField.placeholder!('请选择' + (formField.label!formField.fieldName))}"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
<#elseif formField.fieldType == "select">
          <el-select
            v-model="formData.${formField.fieldName}"
            style="width: 100%"
            placeholder="${formField.placeholder!('请选择' + (formField.label!formField.fieldName))}"
          >
<#if formField.options??>
<#list formField.options as option>
            <el-option label="${option.label}" value="${option.value}" />
</#list>
<#else>
            <el-option label="选项1" value="1" />
            <el-option label="选项2" value="2" />
</#if>
          </el-select>
<#elseif formField.fieldType == "radio">
          <el-radio-group v-model="formData.${formField.fieldName}">
<#if formField.options??>
<#list formField.options as option>
            <el-radio label="${option.value}">${option.label}</el-radio>
</#list>
<#else>
            <el-radio label="1">选项1</el-radio>
            <el-radio label="2">选项2</el-radio>
</#if>
          </el-radio-group>
<#elseif formField.fieldType == "checkbox">
          <el-checkbox-group v-model="formData.${formField.fieldName}">
<#if formField.options??>
<#list formField.options as option>
            <el-checkbox label="${option.value}">${option.label}</el-checkbox>
</#list>
<#else>
            <el-checkbox label="1">选项1</el-checkbox>
            <el-checkbox label="2">选项2</el-checkbox>
</#if>
          </el-checkbox-group>
<#elseif formField.fieldType == "switch">
          <el-switch v-model="formData.${formField.fieldName}" />
<#else>
          <el-input
            v-model="formData.${formField.fieldName}"
            placeholder="${formField.placeholder!('请输入' + (formField.label!formField.fieldName))}"
          />
</#if>
        </el-form-item>
</#if>
</#list>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessageBox, FormInstance } from 'element-plus'
import { message } from '@/api/request'
import { Plus, Delete, Search } from '@element-plus/icons-vue'
<#if enablePagination>
import {
  get${entityName}Page,
<#else>
import {
  get${entityName}List,
</#if>
  save${entityName},
  update${entityName},
  delete${entityName},
  type ${entityName}Info
} from '@/api/${entityNameCamel}'
import { checkButtonPermission } from '@/utils/permission'

const loading = ref<boolean>(false)
const submitLoading = ref<boolean>(false)
const dialogVisible = ref<boolean>(false)
const dialogTitle = ref<string>('新增${entityComment}')
const formRef = ref<FormInstance | null>(null)
const tableData = ref<${entityName}Info[]>([])
const searchKeyword = ref<string>('')

<#if enablePagination>
// 分页参数
const pagination = ref({
  current: 1,
  size: 10,
  total: 0
})
</#if>

const formData = ref<Partial<${entityName}Info>>({
  id: undefined,
<#list formFields as formField>
<#if formField.showInForm?? && formField.showInForm>
  ${formField.fieldName}: <#if formField.fieldType == "number">0<#elseif formField.fieldType == "switch">false<#elseif formField.fieldType == "checkbox">[]<#else>''</#if>,
</#if>
</#list>
})

// 权限检查
const canAdd = computed(() => checkButtonPermission('${entityNameCamel}', '${moduleName}:${entityNameCamel}:add'))
const canEdit = computed(() => checkButtonPermission('${entityNameCamel}', '${moduleName}:${entityNameCamel}:edit'))
const canDelete = computed(() => checkButtonPermission('${entityNameCamel}', '${moduleName}:${entityNameCamel}:delete'))

const rules = {
<#list formFields as formField>
<#if formField.showInForm?? && formField.showInForm && formField.required?? && formField.required>
  ${formField.fieldName}: [
    { required: true, message: '请输入${formField.label!formField.fieldName}', trigger: 'blur' }<#if formField.validationRules??><#list formField.validationRules as rule>
    <#if rule.type == "email">,
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }</#if>
    <#if rule.type == "pattern">,
    { pattern: ${rule.pattern}, message: '${rule.message}', trigger: 'blur' }</#if>
    <#if rule.min??>,
    { min: ${rule.min}, message: '${rule.message}', trigger: 'blur' }</#if>
    <#if rule.max??>,
    { max: ${rule.max}, message: '${rule.message}', trigger: 'blur' }</#if>
</#list></#if>
  ],
</#if>
</#list>
}

<#if enablePagination>
// 加载${entityComment}列表（分页）
const load${entityName}List = async () => {
  loading.value = true
  try {
    const result = await get${entityName}Page({
      current: pagination.value.current,
      size: pagination.value.size,
      keyword: searchKeyword.value || undefined
    })
    if (result && result.records) {
      tableData.value = result.records || []
      pagination.value.total = result.total || 0
    } else {
      tableData.value = []
      pagination.value.total = 0
    }
  } catch (error) {
    console.error('加载${entityComment}列表失败:', error)
    // 错误消息已在 request.ts 中统一处理
    tableData.value = []
    pagination.value.total = 0
  } finally {
    loading.value = false
  }
}
<#else>
// 加载${entityComment}列表
const load${entityName}List = async () => {
  loading.value = true
  try {
    const result = await get${entityName}List()
    tableData.value = result || []
  } catch (error) {
    console.error('加载${entityComment}列表失败:', error)
    // 错误消息已在 request.ts 中统一处理
    tableData.value = []
  } finally {
    loading.value = false
  }
}
</#if>

// 搜索
const handleSearch = () => {
<#if enablePagination>
  pagination.value.current = 1 // 重置到第一页
</#if>
  load${entityName}List()
}

<#if enablePagination>
// 分页大小改变
const handleSizeChange = (size) => {
  pagination.value.size = size
  pagination.value.current = 1
  load${entityName}List()
}

// 当前页改变
const handleCurrentChange = (current) => {
  pagination.value.current = current
  load${entityName}List()
}
</#if>

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增${entityComment}'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row: ${entityName}Info) => {
  dialogTitle.value = '编辑${entityComment}'
  formData.value = { ...row }
  dialogVisible.value = true
}

// 删除
const handleDelete = (row: ${entityName}Info) => {
  if (!row.id) return
  ElMessageBox.confirm('确定要删除这条${entityComment}记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await delete${entityName}(row.id)
      message.success('删除成功')
      load${entityName}List()
    } catch (error) {
      console.error('删除失败:', error)
      // 错误消息已在 request.ts 中统一处理
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        if (formData.value.id) {
          await update${entityName}(formData.value as ${entityName}Info)
          message.success('更新成功')
        } else {
          await save${entityName}(formData.value as ${entityName}Info)
          message.success('新增成功')
        }
        dialogVisible.value = false
        load${entityName}List()
      } catch (error) {
        console.error('保存失败:', error)
        // 错误消息已在 request.ts 中统一处理
      } finally {
        submitLoading.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  formData.value = {
    id: undefined,
<#list formFields as formField>
<#if formField.showInForm?? && formField.showInForm>
    ${formField.fieldName}: <#if formField.fieldType == "number">0<#elseif formField.fieldType == "switch">false<#elseif formField.fieldType == "checkbox">[]<#else>''</#if>,
</#if>
</#list>
  }
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 关闭对话框
const handleDialogClose = () => {
  resetForm()
}

onMounted(() => {
  load${entityName}List()
})
</script>

<style scoped>
.${entityNameCamel}-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>

