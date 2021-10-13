import React from 'react';
import { Col, Form, Input, InputNumber, Modal, Radio, Row, TreeSelect } from 'antd';
import type { FormInstance } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { CodeSandboxOutlined } from '@ant-design/icons';
import type { ConnectState, Department } from './models/connect';

type DepartmentEditorProps = {
  dispatch: Dispatch;
  data: Department;
  departments: Department[];
  type: 'create' | 'update';
  visible: boolean;
};

type DepartmentEditorState = {
  formFields: any;
  timestamp: number;
};

function ignoreSelf(departments: Department[], departmentId: string): Department[] {
  if (!_.isArray(departments)) {
    return [];
  }
  return departments
    .map((value) => {
      if (value.id === departmentId) {
        return null;
      }
      if (_.isArray(value.children) && !_.isEmpty(value.children)) {
        return {
          ...value,
          children: ignoreSelf(value.children, departmentId),
        };
      }
      return value;
    })
    .filter((v) => !!v);
}

class DepartmentEditor extends React.PureComponent<DepartmentEditorProps> {
  formRef = React.createRef<FormInstance>();

  state: DepartmentEditorState = {
    formFields: {},
    timestamp: 0,
  };

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
        timestamp,
      };
    }
    return null;
  }

  generateCode = () => {
    if (this.formRef.current) {
      this.formRef.current.setFieldsValue({
        code: `DP_${Date.now().toString(36).toUpperCase()}`,
      });
    }
  };

  onOk = () => {
    if (this.formRef.current && this.formRef.current.validateFields()) {
      const fieldsValue = this.formRef.current.getFieldsValue();
      if (_.isObject(fieldsValue) && fieldsValue.parentId === 'root') {
        fieldsValue.parentId = null;
      }
      const { dispatch, type, data } = this.props;

      _.merge(fieldsValue, _.pick(data, 'id'));
      dispatch({
        type: `departmentEditor/${type}`,
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
    dispatch({ type: 'departmentEditor/save', payload: { visible: false } });
  };

  render() {
    const { departments, type, visible } = this.props;
    const { formFields } = this.state;
    let treeData = [];
    if (visible) {
      treeData = [
        { key: 'root', title: '根目录', children: ignoreSelf(departments, formFields.id) },
      ];
    }
    return (
      <Modal
        destroyOnClose
        maskClosable={false}
        title={`${type === 'create' ? '新建' : '修改'}部门`}
        visible={visible}
        onCancel={this.onCancel}
        onOk={this.onOk}
      >
        <Form
          labelCol={4}
          wrapperCol={20}
          initialValues={formFields}
          layout="horizontal"
          name="department"
          ref={this.formRef}
        >
          <Row gutter={16}>
            <Col span={24}>
              <Form.Item label="上级部门" name="parentId" rules={[{ required: true }]}>
                <TreeSelect allowClear treeData={treeData} treeDefaultExpandedKeys={['root']} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="部门编码"
                labelCol={2}
                wrapperCol={10}
                name="code"
                rules={[{ required: true }, { type: 'string', max: 127 }]}
              >
                <Input suffix={<CodeSandboxOutlined onClick={this.generateCode} />} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="部门名称"
                labelCol={2}
                wrapperCol={10}
                name="name"
                rules={[{ required: true }, { type: 'string', max: 127 }]}
              >
                <Input />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="显示排序"
                labelCol={2}
                wrapperCol={10}
                name="sortNumber"
                rules={[{ required: true }, { type: 'integer', message: '显示排序必须为整数' }]}
              >
                <InputNumber defaultValue={0} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="部门状态"
                labelCol={2}
                wrapperCol={10}
                name="status"
                rules={[{ required: true }]}
              >
                <Radio.Group defaultValue={1} style={{ width: '100%' }}>
                  <Radio value={1}>正常</Radio>
                  <Radio value={2}>停用</Radio>
                </Radio.Group>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default connect(({ departmentEditor, departments, loading }: ConnectState) => ({
  loading: loading.effects['departments/fetch'],
  data: departmentEditor.entity,
  departments: departments.list,
  timestamp: departmentEditor.timestamp,
  type: departmentEditor.type,
  visible: departmentEditor.visible,
}))(DepartmentEditor);
