import React from 'react';
import { Col, Form, Input, InputNumber, Modal, Radio, Row, TreeSelect } from 'antd';
import type { FormInstance } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { CodeSandboxOutlined } from '@ant-design/icons';
import type { ConnectState, Role } from './models/connect';


type RoleEditorProps = {
  dispatch: Dispatch;
  data: Role;
  roles: Role[];
  type: 'create' | 'update';
  visible: boolean;
}

type RoleEditorState = {
  formFields: any;
  timestamp: number;
}

class RoleEditor extends React.PureComponent<RoleEditorProps> {
  formRef = React.createRef<FormInstance>();

  state: RoleEditorState = {
    formFields: {},
    timestamp: 0
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { data, timestamp } = nextProps;
    const { timestamp: prevTimestamp } = prevState;
    if (timestamp !== prevTimestamp) {
      if (!data.parentId) {
        data.parentId = 'root';
      }
      if (!_.isNumber(data.status)) {
        data.status = 1;
      }
      if (!_.isNumber(data.sortNumber)) {
        data.sortNumber = 0;
      }
      return {
        formFields: data,
        timestamp
      }
    }
    return null;
  }

  generateCode = () => {
    if (this.formRef.current) {
      this.formRef.current.setFieldsValue({
        code: `DP_${Date.now().toString(36).toUpperCase()}`
      })
    }
  }

  onOk = () => {
    if (this.formRef.current && this.formRef.current.validateFields()) {
      const fieldsValue = this.formRef.current.getFieldsValue();
      if (_.isObject(fieldsValue) && fieldsValue.parentId === 'root') {
        fieldsValue.parentId = null;
      }
      const { dispatch, type, data } = this.props;

      _.merge(fieldsValue, _.pick(data, 'id'));
      dispatch({
        type: `roleEditor/${type}`,
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
    dispatch({ type: 'roleEditor/save', payload: { visible: false } })
  }

  render() {
    const { type, visible } = this.props;
    const { formFields } = this.state;
    return (
      <Modal
        destroyOnClose
        maskClosable={false}
        title={`${type === 'create' ? '新建' : '修改'}角色`}
        visible={visible}
        onCancel={this.onCancel}
        onOk={this.onOk}
      >
        <Form
          initialValues={formFields}
          layout="horizontal"
          name="role"
          ref={this.formRef}
          onFieldsChange={this.onFormChange}
        >
          <Row>
            <Col span={12}>
              <Form.Item
                label="角色编码"
                labelCol={{ span: 8}}
                wrapperCol={{ span: 16}}
                name="code"
                rules={[{ required: true }, { type: 'string', max: 127 }]}
              >
                <Input suffix={<CodeSandboxOutlined onClick={this.generateCode} />} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="角色名称"
                labelCol={{ span: 8}}
                wrapperCol={{ span: 16}}
                name="name"
                rules={[{ required: true }, { type: 'string', max: 127 }]}
              >
                <Input />
              </Form.Item>
            </Col>

            <Col span={12}>
              <Form.Item
                label="显示排序"
                labelCol={{ span: 8}}
                wrapperCol={{ span: 16}}
                name="sortNumber"
                rules={[
                  { required: true },
                  { type: 'integer', message: '显示排序必须为整数' }
                ]}
              >
                <InputNumber style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="角色状态"
                labelCol={{ span: 8}}
                wrapperCol={{ span: 16}}
                name="status"
                rules={[{ required: true }]}
              >
                <Radio.Group defaultValue={1} style={{ width: '100%' }}>
                  <Radio value={1}>正常</Radio>
                  <Radio value={2}>停用</Radio>
                </Radio.Group>
              </Form.Item>
            </Col>

            <Col span={24}>
              <Form.Item
                labelCol={{ span: 4}}
                wrapperCol={{ span: 20}}
                label="备注"
                name="remarks"
              >
                <Input.TextArea rows={4} showCount maxLength={255} />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    )
  }
}

export default connect(({ roleEditor, roles, loading }: ConnectState) => ({
  loading: loading.effects['roles/fetch'],
  data: roleEditor.entity,
  roles: roles.list,
  timestamp: roleEditor.timestamp,
  type: roleEditor.type,
  visible: roleEditor.visible
}))(RoleEditor);
