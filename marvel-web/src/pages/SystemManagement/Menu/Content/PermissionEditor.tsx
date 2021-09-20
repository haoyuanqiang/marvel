import React from 'react';
import { Col, Form, Input, InputNumber, Modal, Row, Select } from 'antd';
import type { FormInstance } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { CodeSandboxOutlined } from '@ant-design/icons';
import type { ConnectState, Permission } from '../models/connect';

type PermissionEditorProps = {
  dispatch: Dispatch;
  loading: boolean;
  entity: Permission;
  menuId: string;
  timestamp: number;
  type: 'create' | 'update';
  visible: boolean;
}

type PermissionEditorState = {
  formFields: any;
  timestamp: number;
}

class PermissionEditor extends React.PureComponent<PermissionEditorProps, PermissionEditorState> {
  formRef = React.createRef<FormInstance>();

  state: PermissionEditorState = {
    formFields: {},
    timestamp: 0
  }

  static getDerivedStateFromProps(nextProps: PermissionEditorProps, prevState: PermissionEditorState) {
    const { entity, timestamp } = nextProps;
    const { timestamp: prevTimestamp } = prevState;
    if (timestamp !== prevTimestamp) {
      if (_.isObject(entity) && _.isEmpty(entity.method)) {
        entity.method = 'GET';
      }
      if (!_.isNumber(entity.sortNumber)) {
        entity.sortNumber = 0;
      }
      return {
        formFields: entity,
        timestamp
      }
    }
    return null;
  }

  generateCode = () => {
    if (this.formRef.current) {
      this.formRef.current.setFieldsValue({
        code: `MP_${Date.now().toString(36).toUpperCase()}`
      })
    }
  }

  onOk = () => {
    if (this.formRef.current && this.formRef.current.validateFields()) {
      const fieldsValue = this.formRef.current.getFieldsValue();
      const { dispatch, entity, menuId } = this.props;
      _.merge(fieldsValue, _.pick(entity, 'id'));
      fieldsValue.menuId = menuId;
      dispatch({
        type: `permissionEditor/submit`,
        payload: fieldsValue,
        callback: (code) => {
          if (code === 0) {
            this.onCancel();
          }
        }
      })
    }
  }
  
  onCancel = () => {
    const { dispatch } = this.props;
    dispatch({ type: 'permissionEditor/save', payload: { visible: false } })
  }

  render() {
    const { type, visible } = this.props;
    const { formFields } = this.state;
    return (
      <Modal
        destroyOnClose
        maskClosable={false}
        title={`${type === 'create' ? '新建' : '修改'}权限`}
        visible={visible}
        onCancel={this.onCancel}
        onOk={this.onOk}
      >
        <Form
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 20 }}
          initialValues={formFields}
          layout="horizontal"
          name="permission"
          ref={this.formRef}
        >
          <Row gutter={8}>
            <Col span={24}>
              <Form.Item
                label="权限编码"
                name="code"
                rules={[
                  { required: true }, 
                  { type: 'string', max: 127 },
                  { type: 'regexp', pattern: /^\w+$/g, message: '只允许英文字母 、数字和下划线' }
                ]}
              >
                <Input suffix={<CodeSandboxOutlined onClick={this.generateCode} />} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                label="权限名称"
                name="name"
                rules={[
                  { required: true }, 
                  { type: 'string', max: 127 },
                  { type: 'regexp', pattern: /^[一-鿿\w]+$/g, message: '只允许中文字、英文字母、数字和下划线' }
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="请求类型"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="method"
                rules={[{ required: true }]}
              >
                <Select>
                  <Select.Option value="GET">GET</Select.Option>
                  <Select.Option value="POST">POST</Select.Option>
                  <Select.Option value="PUT">PUT</Select.Option>
                  <Select.Option value="DELETE">DELETE</Select.Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="显示排序"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="sortNumber"
                rules={[{ required: true }, { type: 'integer', message: '必须为整数' }]}
              >
                <InputNumber style={{ width: '100%' }}  />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                label="权限路径"
                labelCol={{ span: 4 }}
                wrapperCol={{ span: 20 }}
                name="route"
                rules={[{ required: true }]}
              >
                <Input />
              </Form.Item>
            </Col>
            
          </Row>
        </Form>
      </Modal>
    )
  }
}

export default connect(({ permissionEditor, loading }: ConnectState) => ({
  loading: loading.effects['permissionEditor/initialize'],
  entity: permissionEditor.permission,
  menuId: permissionEditor.menuId,
  timestamp: permissionEditor.timestamp,
  type: permissionEditor.type,
  visible: permissionEditor.visible
}))(PermissionEditor);
