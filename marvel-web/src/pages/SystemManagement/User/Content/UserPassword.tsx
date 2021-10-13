import React from 'react';
import { Form, Input, Modal } from 'antd';
import type { FormInstance } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import type { ConnectState, User } from '../models/connect';

type UserEditorProps = {
  dispatch: Dispatch;
  visible: boolean;
  user: User;
};

type UserEditorState = {
  passwordFirst: string;
  passwordSecond: string;
  isPlaintext: boolean;
};

class UserEditor extends React.PureComponent<UserEditorProps> {
  formRef = React.createRef<FormInstance>();

  state: UserEditorState = {
    passwordFirst: '',
    passwordSecond: '',
    isPlaintext: false,
  };

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
      const { dispatch, user } = this.props;
      const userId = _.get(user, 'id', '');
      const password = _.get(fieldsValue, 'password', '');
      if (!_.isEmpty(userId) && !_.isEmpty(password)) {
        dispatch({
          type: `userPassword/submit`,
          payload: {
            userId,
            password,
          },
          callback: (code) => {
            if (code === 0) {
              this.onCancel();
            }
          },
        });
      } else {
        this.onCancel();
      }
    }
  };

  onCancel = () => {
    const { dispatch } = this.props;
    dispatch({ type: 'userPassword/save', payload: { visible: false } });
  };

  onPasswordFirstChange = (event) => {
    const { value } = event.target;
    this.setState({ passwordFirst: value });
  };

  render() {
    const { user, visible } = this.props;
    return (
      <Modal
        destroyOnClose
        maskClosable={false}
        title={`修改密码 - ${_.get(user, 'name', '')}`}
        visible={visible}
        width={400}
        onCancel={this.onCancel}
        onOk={this.onOk}
      >
        <Form
          name="updatePassword"
          labelCol={{ span: 6 }}
          layout="horizontal"
          wrapperCol={{ span: 18 }}
          ref={this.formRef}
        >
          {/* <Form.Item
            name="newPassword"
            label="新密码"
            validateStatus={number.validateStatus}
            help={number.errorMsg || tips}
            rules={[{ required: true, message: '密码不可为空' }]}
          >
            <Input.Password value={passwordFirst} onChange={this.onPasswordFirstChange} />
          </Form.Item>
          <Form.Item
            label="确认密码"
            validateStatus={number.validateStatus}
            help={number.errorMsg || tips}
          >
            <Input.Password value={passwordFirst} onChange={this.onPasswordFirstChange} />
          </Form.Item> */}
          <Form.Item
            name="password"
            label="新密码"
            rules={[
              {
                required: true,
                message: '请输入密码',
              },
            ]}
            hasFeedback
          >
            <Input.Password />
          </Form.Item>

          <Form.Item
            name="confirm"
            label="确认密码"
            dependencies={['password']}
            hasFeedback
            rules={[
              {
                required: true,
                message: '请确认密码',
              },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                },
              }),
            ]}
          >
            <Input.Password />
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}

export default connect(({ userPassword }: ConnectState) => ({
  user: userPassword.user,
  visible: userPassword.visible,
}))(UserEditor);
