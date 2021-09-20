import React from 'react';
import { Col, Row, Form, Input, Button, Space } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import type { ConnectState, UserModelState } from './models/connect';
import type { FormInstance } from 'antd';

type PasswordModifierProps = {
  dispatch: Dispatch;
  loading: boolean;
  userInfo: UserModelState;
}

type PasswordModifierState = {
  formFields: any;
  isChanged: boolean;
}

class PasswordModifier extends React.PureComponent<PasswordModifierProps, PasswordModifierState> {
  formRef = React.createRef<FormInstance>();

  state: PasswordModifierState = {
    formFields: {},
    isChanged: false
  }

  onOk = () => {
    if (this.formRef.current && this.formRef.current.validateFields()) {
      const fieldsValue = this.formRef.current.getFieldsValue();
      const { dispatch } = this.props;
      dispatch({
        type: `userInfo/updatePassword`,
        payload: {
          oldPassword: fieldsValue.oldPassword,
          newPassword: fieldsValue.password
        },
        callback: (code) => {
          if (code === 0) {
            this.formRef.current.setFieldsValue({ oldPassword: '', password: '', confirm: '' });
          }
        }
      })
    }
  }

  onReset = () => {
    if (this.formRef.current) {
      this.formRef.current.setFieldsValue({ oldPassword: '', password: '', confirm: '' });
    }
  }

  render() {
    return (
      <Row>
        <Col span={12}>
          <Form 
            labelCol={{ span: 8 }} 
            wrapperCol={{ span: 16 }} 
            size="large" 
            ref={this.formRef}
          >
            <Form.Item
              name="oldPassword"
              label="旧密码"
              rules={[
                {
                  required: true,
                  message: '请输入旧密码',
                },
              ]}
              hasFeedback
            >
              <Input.Password placeholder="请输入旧密码" size="large" />
            </Form.Item>

            <Form.Item
              name="password"
              label="新密码"
              rules={[
                {
                  required: true,
                  message: '请输入新密码!',
                },
              ]}
              hasFeedback
            >
              <Input.Password placeholder="请输入新密码" size="large" />
            </Form.Item>

            <Form.Item
              name="confirm"
              label="确认密码"
              dependencies={['password']}
              hasFeedback
              rules={[
                {
                  required: true,
                  message: '请确认新密码!',
                },
                ({ getFieldValue }) => ({
                  validator(_, value) {
                    if (!value || getFieldValue('password') === value) {
                      return Promise.resolve();
                    }
                    return Promise.reject(new Error('!'));
                  },
                }),
              ]}
            >
              <Input.Password placeholder="请再输入一次新密码" size="large" />
            </Form.Item>
          </Form>
        </Col>
        <Col span={24}>
          <Space>
            <Button type="primary" onClick={this.onOk}>保存</Button>
            <Button type="default" onClick={this.onReset}>重置</Button>
          </Space> 
        </Col>
      </Row>
    );
    
  }
}

export default connect(({ loading }: ConnectState) => ({
  loading: loading.effects['userInfo/fetch']
}))(PasswordModifier);
