import { Avatar, Divider, Dropdown, Menu } from 'antd';
import React from 'react';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { LogoutOutlined, UserOutlined } from '@ant-design/icons';
import type { ConnectState } from './models/connect';

type LoggedInUserInfoProps = {
  dispatch: Dispatch;
  name: string;
};

class LoggedInUserInfo extends React.PureComponent<LoggedInUserInfoProps> {
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'loginUser/fetch',
    });
  }

  logout = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'userLogin/logout',
    });
  };

  onMenuClick = ({ key }) => {
    if (key === 'logout') {
      this.logout();
    } else if (key === 'personalCenter') {
      const { dispatch } = this.props;
      dispatch({
        type: 'openedTabs/add',
        payload: {
          id: 'personal-center',
          name: '个人中心',
          locale: 'system.personal-center',
          path: '/module/personal-center',
        },
      });
    }
  };

  renderDropdownMenu = () => {
    return (
      <Menu selectedKeys={[]} onClick={this.onMenuClick}>
        <Menu.Item key="personalCenter" icon={<UserOutlined />}>
          个人中心
        </Menu.Item>
        <Divider style={{ margin: '4px 0' }} />
        <Menu.Item key="logout" icon={<LogoutOutlined />}>
          退出登录
        </Menu.Item>
      </Menu>
    );
  };

  render() {
    return (
      <Dropdown overlay={this.renderDropdownMenu}>
        <Avatar icon={<UserOutlined />} size={44} shape="square" />
      </Dropdown>
    );
  }
}

export default connect(({ loading, loginUser }: ConnectState) => ({
  loading: loading.effects['loginUser/fetch'],
  name: loginUser.nickname || loginUser.name || loginUser.loginName,
}))(LoggedInUserInfo);
