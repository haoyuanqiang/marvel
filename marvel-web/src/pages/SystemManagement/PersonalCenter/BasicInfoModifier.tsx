import React from 'react';
import { Col, Row, Form, Input, Button, Space } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import type { ConnectState, UserModelState } from './models/connect';
import type { FormInstance } from 'antd';

type BasicInfoModifierProps = {
  dispatch: Dispatch;
  loading: boolean;
  userInfo: UserModelState;
}

type BasicInfoModifierState = {
  formFields: any;
  isChanged: boolean;
}


class BasicInfoModifier extends React.PureComponent<BasicInfoModifierProps, BasicInfoModifierState> {
  formRef = React.createRef<FormInstance>();

  state: BasicInfoModifierState = {
    formFields: {},
    isChanged: false
  }

  onOk = () => {
    if (this.formRef.current && this.formRef.current.validateFields()) {
      const fieldsValue = this.formRef.current.getFieldsValue();
      const { dispatch } = this.props;
      dispatch({
        type: `userInfo/updateUserInfo`,
        payload: {
          nickname: !_.isEmpty(fieldsValue.nickname) ? fieldsValue.nickname : undefined,
          telephone: !_.isEmpty(fieldsValue.telephone) ? fieldsValue.telephone : undefined,
          email: !_.isEmpty(fieldsValue.email) ? fieldsValue.email : undefined
        },
        callback: (code) => {
          if (code === 0) {
            this.formRef.current.setFieldsValue({ nickname: '', telephone: '', email: '' });
            this.setState({ isChanged: false })
          }
        }
      })
    }
  }

  onReset = () => {
    if (this.formRef.current) {
      this.formRef.current.setFieldsValue({ nickname: '', telephone: '', email: '' });
    }
  }

  render() {
    const { isChanged } = this.state;
    return (
      <Row>
        <Col span={12}>
          <Form 
            labelCol={{ span: 8 }} 
            wrapperCol={{ span: 16 }} 
            size="large"
            ref={this.formRef}
            onValuesChange={() => { this.setState({ isChanged: true })}}
          >
            <Form.Item name="nickname" label="用户昵称">
              <Input size="large" placeholder="请输入用户昵称" />
            </Form.Item>
            <Form.Item name="telephone" label="手机号码">
              <Input size="large" placeholder="请输入手机号码" />
            </Form.Item>
            <Form.Item name="email" label="邮箱">
              <Input size="large" placeholder="请输入邮箱" />
            </Form.Item>
          </Form>
        </Col>
        <Col span={12} offset={4}>
          <Space>
            <Button type="primary" onClick={this.onOk} disabled={!isChanged}>保存</Button>
            <Button type="default" onClick={this.onReset} disabled={!isChanged}>重置</Button>
          </Space> 
        </Col>
      </Row>
    );
  }
}

export default connect(({ userInfo, loading }: ConnectState) => ({
  loading: loading.effects['userInfo/fetch'],
  userInfo,
}))(BasicInfoModifier);
