import React from 'react';
import { Col, Form, Input, InputNumber, Modal, Radio, Row, TreeSelect } from 'antd';
import type { FormInstance } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { CodeSandboxOutlined } from '@ant-design/icons';
import type { ConnectState, Position } from './models/connect';

type PositionEditorProps = {
  dispatch: Dispatch;
  data: Position;
  positions: Position[];
  type: 'create' | 'update';
  visible: boolean;
};

type PositionEditorState = {
  formFields: any;
  timestamp: number;
};

function ignoreSelf(positions: Position[], positionId: string): Position[] {
  if (!_.isArray(positions)) {
    return [];
  }
  return positions
    .map((value) => {
      if (value.id === positionId) {
        return null;
      }
      if (_.isArray(value.children) && !_.isEmpty(value.children)) {
        return {
          ...value,
          children: ignoreSelf(value.children, positionId),
        };
      }
      return value;
    })
    .filter((v) => !!v);
}

class PositionEditor extends React.PureComponent<PositionEditorProps> {
  formRef = React.createRef<FormInstance>();

  state: PositionEditorState = {
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
        code: `PN_${Date.now().toString(36).toUpperCase()}`,
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
        type: `positionEditor/${type}`,
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
    dispatch({ type: 'positionEditor/save', payload: { visible: false } });
  };

  render() {
    const { positions, type, visible } = this.props;
    const { formFields } = this.state;
    let treeData = [];
    if (visible) {
      treeData = [{ key: 'root', title: '?????????', children: ignoreSelf(positions, formFields.id) }];
    }
    return (
      <Modal
        destroyOnClose
        maskClosable={false}
        title={`${type === 'create' ? '??????' : '??????'}??????`}
        visible={visible}
        onCancel={this.onCancel}
        onOk={this.onOk}
      >
        <Form
          labelCol={4}
          wrapperCol={20}
          initialValues={formFields}
          layout="horizontal"
          name="position"
          ref={this.formRef}
          onFieldsChange={this.onFormChange}
        >
          <Row>
            <Col span={24}>
              <Form.Item label="????????????" name="parentId" rules={[{ required: true }]}>
                <TreeSelect allowClear treeData={treeData} treeDefaultExpandedKeys={['root']} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="????????????"
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
                label="????????????"
                labelCol={2}
                wrapperCol={10}
                name="name"
                rules={[{ required: true }, { type: 'string', max: 127 }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                label="????????????"
                labelCol={2}
                wrapperCol={10}
                name="sortNumber"
                rules={[{ required: true }, { type: 'integer', message: '???????????????????????????' }]}
              >
                <InputNumber defaultValue={0} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="????????????"
                labelCol={2}
                wrapperCol={10}
                name="status"
                rules={[{ required: true }]}
              >
                <Radio.Group defaultValue={1} style={{ width: '100%' }}>
                  <Radio value={1}>??????</Radio>
                  <Radio value={2}>??????</Radio>
                </Radio.Group>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}

export default connect(({ positionEditor, positions, loading }: ConnectState) => ({
  loading: loading.effects['positions/fetch'],
  data: positionEditor.entity,
  positions: positions.list,
  timestamp: positionEditor.timestamp,
  type: positionEditor.type,
  visible: positionEditor.visible,
}))(PositionEditor);
