import React from 'react';
import { Col, Form, Input, InputNumber, Modal, Radio, Row, TreeSelect } from 'antd';
import type { FormInstance } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { CodeSandboxOutlined } from '@ant-design/icons';
import IconSelect from '@/components/IconSelect';
import type { ConnectState, Menu } from '../models/connect';

type MenuEditorProps = {
  dispatch: Dispatch;
  loading: boolean;
  entity: Menu;
  timestamp: number;
  tree: Menu[];
  type: 'create' | 'update';
  visible: boolean;
};

type MenuEditorState = {
  formFields: any;
  timestamp: number;
  menuType: number;
};

function ignoreSelf(menus: Menu[], menuId: string): Menu[] {
  if (!_.isArray(menus)) {
    return [];
  }
  return menus
    .map((value) => {
      if (value.id === menuId) {
        return null;
      }
      if (_.isArray(value.children) && !_.isEmpty(value.children)) {
        return {
          ...value,
          children: ignoreSelf(value.children, menuId),
        };
      }
      return value;
    })
    .filter((v) => !!v);
}

class MenuEditor extends React.PureComponent<MenuEditorProps, MenuEditorState> {
  formRef = React.createRef<FormInstance>();

  state: MenuEditorState = {
    formFields: {},
    timestamp: 0,
  };

  static getDerivedStateFromProps(nextProps: MenuEditorProps, prevState: MenuEditorState) {
    const { entity, timestamp } = nextProps;
    const { timestamp: prevTimestamp } = prevState;
    if (timestamp !== prevTimestamp) {
      if (!entity.parentId) {
        entity.parentId = 'root';
      }
      if (!_.isNumber(entity.type)) {
        entity.type = 1;
      }
      if (!_.isNumber(entity.sortNumber)) {
        entity.sortNumber = 0;
      }
      if (!_.isNumber(entity.visible)) {
        entity.visible = 1;
      }
      if (!_.isNumber(entity.status)) {
        entity.status = 1;
      }
      return {
        formFields: entity,
        menuType: entity.type,
        timestamp,
      };
    }
    return null;
  }

  generateCode = () => {
    if (this.formRef.current) {
      this.formRef.current.setFieldsValue({
        code: `ME_${Date.now().toString(36).toUpperCase()}`,
      });
    }
  };

  onOk = () => {
    if (this.formRef.current && this.formRef.current.validateFields()) {
      const fieldsValue = this.formRef.current.getFieldsValue();
      if (_.isObject(fieldsValue) && fieldsValue.parentId === 'root') {
        fieldsValue.parentId = null;
      }
      const { dispatch, entity } = this.props;
      _.merge(fieldsValue, _.pick(entity, 'id'));
      dispatch({
        type: `menuEditor/submit`,
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
    dispatch({ type: 'menuEditor/save', payload: { visible: false } });
  };

  onFieldsChange = (changedFields: any) => {
    if (_.isArray(changedFields)) {
      const nextState = {};
      changedFields.forEach((item) => {
        if (_.isArray(item.name) && item.name.indexOf('type') >= 0) {
          nextState.menuType = item.value;
        }
      });
      if (Object.keys(nextState).length > 0) {
        this.setState(nextState);
      }
    }
  };

  renderPageForm = () => {
    return (
      <>
        <Row gutter={8}>
          <Col span={24}>
            <Form.Item
              label="路由类型"
              labelCol={{ span: 4 }}
              wrapperCol={{ span: 20 }}
              name="routeType"
              rules={[{ required: true }]}
            >
              <Radio.Group style={{ width: '100%' }}>
                <Radio value={1}>系统</Radio>
                <Radio value={2}>模块</Radio>
                <Radio value={3}>外链</Radio>
              </Radio.Group>
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <Form.Item
              label="路由地址"
              labelCol={{ span: 4 }}
              wrapperCol={{ span: 20 }}
              name="path"
              rules={[{ required: true }]}
            >
              <Input />
            </Form.Item>
          </Col>
        </Row>
      </>
    );
  };

  render() {
    const { tree, type, visible } = this.props;
    const { formFields, menuType } = this.state;
    let treeData = [];
    if (visible) {
      treeData = [{ key: 'root', title: '根目录', children: ignoreSelf(tree, formFields.id) }];
    }
    return (
      <Modal
        bodyStyle={{ minHeight: 300 }}
        destroyOnClose
        maskClosable={false}
        title={`${type === 'create' ? '新建' : '修改'}菜单`}
        visible={visible}
        width={600}
        onCancel={this.onCancel}
        onOk={this.onOk}
      >
        <Form
          labelCol={{ span: 4 }}
          wrapperCol={{ span: 20 }}
          initialValues={formFields}
          layout="horizontal"
          name="menu"
          ref={this.formRef}
          onFieldsChange={this.onFieldsChange}
        >
          <Row>
            <Col span={24}>
              <Form.Item label="上级菜单" name="parentId" rules={[{ required: true }]}>
                <TreeSelect allowClear treeData={treeData} treeDefaultExpandedKeys={['root']} />
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item label="菜单类型" name="type" rules={[{ required: true }]}>
                <Radio.Group style={{ width: '100%' }} disabled={type === 'update'}>
                  <Radio value={1}>目录</Radio>
                  <Radio value={2}>页面</Radio>
                </Radio.Group>
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={8}>
            <Col span={12}>
              <Form.Item
                label="菜单编码"
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
                label="菜单名称"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="name"
                rules={[{ required: true }, { type: 'string', max: 127 }]}
              >
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={8}>
            <Col span={12}>
              <Form.Item
                label="菜单图标"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="iconName"
              >
                <IconSelect />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="显示排序"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="sortNumber"
                rules={[{ required: true }, { type: 'integer' }]}
              >
                <InputNumber style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={8}>
            <Col span={12}>
              <Form.Item
                label="显示状态"
                labelCol={{ span: 8 }}
                wrapperCol={{ span: 16 }}
                name="visible"
                rules={[{ required: true }]}
              >
                <Radio.Group style={{ width: '100%' }}>
                  <Radio value={1}>显示</Radio>
                  <Radio value={2}>隐藏</Radio>
                </Radio.Group>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label="菜单状态"
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
          </Row>
          {menuType === 2 && this.renderPageForm()}
        </Form>
      </Modal>
    );
  }
}

export default connect(({ menuManagement, menuEditor, loading }: ConnectState) => ({
  loading: loading.effects['menuEditor/initialize'],
  entity: menuEditor.menu,
  tree: menuManagement.list,
  timestamp: menuEditor.timestamp,
  type: menuEditor.type,
  visible: menuEditor.visible,
}))(MenuEditor);
