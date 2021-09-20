import React from 'react';
import { Form, Input, Button, Checkbox } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi'
import { FaUserCircle } from '@react-icons/all-files/fa/FaUserCircle';
import { FaKey } from '@react-icons/all-files/fa/FaKey';
import type { ConnectState } from './models/connect';

type UserLoginProps = {
  dispatch: Dispatch;
  userLogin: StateType;
  submitting?: boolean;
}

class UserLogin extends React.PureComponent<UserLoginProps> {
  formRef = React.createRef<FormInstance>();

  onSubmit = () => {
    if (this.formRef.current) {
      this.formRef.current.validateFields().then(values => {
        // console.log(values);
        const { dispatch } = this.props;
        dispatch({
          type: 'userLogin/login',
          payload: { ...values },
        });
      }).catch(() => {})
    }
  }

  render() {
    return (
      <Form
        labelCol={{ span: 0 }}
        wrapperCol={{ span: 24 }}
        name="login"
        initialValues={{ remember: false }}
        ref={this.formRef}
      >
        <Form.Item
          name="username"
          rules={[{ required: true, message: '请输入登录账号' }]}
        >
          <Input
            size="large"
            placeholder="登录账号"
            prefix={<FaUserCircle color="rgba(0,0,0,0.35)" fontSize={20} />}
          />
        </Form.Item>
        <Form.Item
          name="password"
          rules={[{ required: true, message: '请输入密码' }]}
        >
          <Input.Password
            size="large"
            placeholder="密码"
            prefix={<FaKey color="rgba(0,0,0,0.35)" fontSize={19} />}
          />
        </Form.Item>
        <Form.Item style={{ marginBottom: 8 }}>
          <Form.Item
            name="remember"
            valuePropName="checked"
            style={{ display: 'inline-block', textAlign: 'left', width: 'calc(50% - 8px)', marginBottom: 0 }}
          >
            <Checkbox>记住密码</Checkbox>
          </Form.Item>
          <Form.Item
            name="month"
            style={{ display: 'inline-block', textAlign: 'right', width: 'calc(50% - 8px)', marginBottom: 0 }}
          >
            <Button type="link">忘记密码</Button>
          </Form.Item>
        </Form.Item>
        <Form.Item wrapperCol={{ offset: 0, span: 24 }}>
          <Button type="primary" style={{ borderRadius: 4, width: '100%' }} onClick={this.onSubmit}>
            登录
          </Button>
        </Form.Item>
      </Form>
    )
  }
}

export default connect(({ userLogin, loading }: ConnectState) => ({
  userLogin,
  submitting: loading.effects['login/login'],
}))(UserLogin);
