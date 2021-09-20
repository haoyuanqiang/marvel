import React from 'react';
import { Menu } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import * as AntdIcons from '@ant-design/icons';
import type { MenuInfo } from 'rc-menu/lib/interface';
import type { ConnectState, Menu as MenuType } from './models/connect';

type SiderMenuProps = {
  dispatch: Dispatch;
  menus: MenuType[];
}

type SiderMenuState = {

}

class SiderMenu extends React.PureComponent<SiderMenuProps, SiderMenuState> {
  state = {}

  componentDidMount() {
    this.fetchMenus();
    window.addEventListener('message', this.handleMessage);
  }

  componentWillUnmount() {
    window.removeEventListener('message', this.handleMessage);
  }

  handleMessage = (event) => {
    const { data } = event;
    if (data.key === 'refreshMenus') {
      this.fetchMenus();
    }
  }

  fetchMenus = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'frameworkMenus/fetch'
    })
  }

  onMenuClick = ({ keyPath }: MenuInfo) => {
    const { dispatch, menus } = this.props;
    if (_.isArray(keyPath)) {
      let menu: MenuType = null;
      for (let i = keyPath.length - 1; i >= 0; i -= 1) {
        if (_.isNull(menu)) {
          menu = menus.find(item => item.id === keyPath[i]);
        } else if (_.isArray(menu?.children)) {
          menu = menu.children.find(item => item.id === keyPath[i]);
        }
        if (!_.isObject(menu)) {
          break;
        }
      }
      if (_.isObject(menu)) {
        dispatch({
          type: 'openedTabs/add',
          payload: menu,
        });
      }
    }
  }

  renderMenu = (menu: MenuType) => {
    if (!_.isObject(menu)) {
      return null;
    }
    let children = null;
    if (_.isArray(menu.children)) {
      children = _.map(menu.children, item => this.renderMenu(item)).filter(v => !!v);
    }
    const IconComponent = AntdIcons[menu.iconName] || AntdIcons.BorderOutlined;
    const isValidIcon = !!AntdIcons[menu.iconName];
    if (!_.isEmpty(children)) {
      return (
        <Menu.SubMenu
          key={menu.id}
          icon={<IconComponent style={{ color: !isValidIcon ? 'transparent' : undefined }} />}
          title={menu.name}
        >
          {children}
        </Menu.SubMenu>
      );
    }
    return (
      <Menu.Item
        key={menu.id}
        icon={<IconComponent style={{ color: !isValidIcon ? 'transparent' : undefined }} />}
      >
        {menu.name}
      </Menu.Item>
    );
  }

  render() {
    const { menus } = this.props;
    return (
      <Menu theme="dark" mode="inline" onClick={this.onMenuClick}>
        {!_.isArray(menus) ? null : _.map(menus, menu => this.renderMenu(menu))}
      </Menu>
    )
  }
}

export default connect(({ frameworkMenus }: ConnectState) => ({
  menus: frameworkMenus.list
}))(SiderMenu);
