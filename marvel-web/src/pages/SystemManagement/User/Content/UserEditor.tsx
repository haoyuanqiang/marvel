import React from 'react';
import { Col, Form, Input, InputNumber, Modal, Radio, Row, TreeSelect } from 'antd';
import type { FormInstance } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { CodeSandboxOutlined } from '@ant-design/icons';
import type { ConnectState, User, Department } from '../models/connect';

type UserEditorProps = {
  dispatch: Dispatch;
  departments: Department[];
  data: User;
  users: User[];
  type: 'create' | 'update';
  visible: boolean;
};

type UserEditorState = {
  formFields: any;
  timestamp: number;
};

class UserEditor extends React.PureComponent<UserEditorProps> {
  formRef = React.createRef<FormInstance>();

  state: UserEditorState = {
    formFields: {},
    timestamp: 0,
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    const { data, timestamp } = nextProps;
    const { timestamp: prevTimestamp } = prevState;
    if (timestamp !== prevTimestamp) {
      if (!_.isNumber(data.status)) {
        data.status = 1;
      }
      if (!_.isNumber(data.sortNumber)) {
        data.sortNumber = 0;
      }
      return {
        formFields: data,
        timestamp,
      };
    }
    return null;
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'roles/fetch' });
    dispatch({ type: 'positions/fetch' });
  }

  generateCode = () => {
    if (this.formRef.current) {
      this.formRef.current.setFieldsValue({
        code: `UU_${Date.now().toString(36).toUpperCase()}`,
      });
    }
  };

  onOk = () => {
    if (this.formRef.current && this.formRef.current.validateFields()) {
      const fieldsValue = this.formRef.current.getFieldsValue();
      const { dispatch, type, data } = this.props;
      _.merge(fieldsValue, _.pick(data, 'id'));
      dispatch({
        type: `userEditor/${type}`,
        payload: fieldsValue,
        callback: (code) => {
          if (code === 0) {
            this.onCancel();
          }
        },
      });
    }
  };

  onCancel = () => {
    const { dispatch } = this.props;
    dispatch({ type: 'userEditor/save', payload: { visible: false } });
  };

  render() {
    const { departments, roles, positions, type, visible } = this.props;
    const { formFields } = this.state;
    return (
      <Modal
        destroyOnClose
        maskClosable={false}
        title={`${type === 'create' ? '新建' : '修改'}用户`}
        visible={visible}
        width={660}
        onCancel={this.onCancel}
        onOk={this.onOk}
      >
        <Form
          initialValues={formFields}
          layout="horizontal"
          name="user"
          ref={this.formRef}
          onFieldsChange={this.onFormChange}
        >
          <Row>
            <Col span={12}>
              <Form.Item
                label="用户编码"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="code"
                rules={[{ required: true }, { type: 'string', max: 127 }]}
              >
                <Input suffix={<CodeSandboxOutlined onClick={this.generateCode} />} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="登录名称"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="loginName"
                rules={[{ required: true }, { type: 'string', max: 127 }]}
              >
                <Input />
              </Form.Item>
            </Col>

            <Col span={12}>
              <Form.Item
                label="用户名称"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="name"
                rules={[{ required: true }, { type: 'string', max: 127 }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="用户昵称"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="nickname"
                rules={[{ type: 'string', max: 127 }]}
              >
                <Input />
              </Form.Item>
            </Col>

            <Col span={12}>
              <Form.Item
                label="性别"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="sex"
                rules={[{ required: true }]}
              >
                <Radio.Group style={{ width: '100%' }}>
                  <Radio value={1}>男</Radio>
                  <Radio value={2}>女</Radio>
                  <Radio value={3}>保密</Radio>
                </Radio.Group>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="部门"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="departmentId"
                rules={[{ required: true }]}
              >
                <TreeSelect
                  allowClear
                  dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                  placeholder="请选择部门"
                  style={{ width: '100%' }}
                  treeDefaultExpandAll
                  treeData={departments}
                />
              </Form.Item>
            </Col>

            <Col span={12}>
              <Form.Item
                label="角色"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="roleIds"
              >
                <TreeSelect
                  allowClear
                  dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                  maxTagCount={3}
                  placeholder="请选择角色"
                  style={{ width: '100%' }}
                  showCheckedStrategy={TreeSelect.SHOW_PARENT}
                  treeCheckable
                  treeDefaultExpandAll
                  treeData={roles}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="岗位"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="positionIds"
                rules={[{ required: true }]}
              >
                <TreeSelect
                  allowClear
                  dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                  maxTagCount={3}
                  multiple
                  placeholder="请选择岗位"
                  style={{ width: '100%' }}
                  treeDefaultExpandAll
                  treeData={positions}
                />
              </Form.Item>
            </Col>

            <Col span={12}>
              <Form.Item
                label="显示排序"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="sortNumber"
                rules={[{ required: true }, { type: 'integer', message: '显示排序必须为整数' }]}
              >
                <InputNumber style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="用户状态"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="status"
                rules={[{ required: true }]}
              >
                <Radio.Group style={{ width: '100%' }}>
                  <Radio value={1}>正常</Radio>
                  <Radio value={2}>停用</Radio>
                </Radio.Group>
              </Form.Item>
            </Col>

            <Col span={12}>
              <Form.Item
                label="手机号码"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="telephone"
                rules={[
                  {
                    type: 'string',
                    pattern: '^((\\(\\d{2,3}\\))|(\\d{3}\\-))?1(3|5|8|9)\\d{9}$',
                    message: '手机号码或固定电话格式不正确',
                  },
                ]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="邮箱"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="email"
                rules={[{ type: 'email', message: '邮箱格式不正确' }]}
              >
                <Input />
              </Form.Item>
            </Col>

            <Col span={24}>
              <Form.Item
                labelCol={{ span: 4 }}
                wrapperCol={{ span: 20 }}
                label="备注"
                name="remarks"
              >
                <Input.TextArea rows={3} showCount maxLength={255} />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default connect(
  ({ userEditor, departmentTree, roles, positions, loading }: ConnectState) => ({
    loading: loading.effects['users/initialize'],
    roles: roles.list,
    positions: positions.list,
    departments: departmentTree.list,
    data: userEditor.entity,
    timestamp: userEditor.timestamp,
    type: userEditor.type,
    visible: userEditor.visible,
  }),
)(UserEditor);
