import React from 'react';
import type { Dispatch } from 'umi';
import { connect } from 'umi';
import { Divider, Empty } from 'antd';
import type { ConnectState } from '../models/connect';
import type { MenuItem } from '../models/menus';
import PermissionList from './PermissionList';
import MenuItemInfo from './MenuItemInfo';
import styles from '../style.less';

interface MenuInfoProps {
  dispatch: Dispatch;
  menus: MenuItem[];
  selectedKeys: stringk[];
}

interface MenuInfoState {
  menu: MenuItem;
}

function findMenuItem(menus: MenuItem[], key: string | undefined): MenuItem | null {
  if (_.isArray(menus) && _.isString(key)) {
    for (let i = 0; i < menus.length; i += 1) {
      const menu = menus[i];
      if (menu.id === key) {
        return menu;
      }
      const menuItem = findMenuItem(menu.children, key);
      if (_.isObject(menuItem)) {
        return menuItem;
      }
    }
  }
  return null;
}

const EmptyStatus: React.FC = (): React.ReactNode => {
  return (
    <Empty 
      className={styles['marvel-empty-status']}
      description={false}
    />
  )
}

class MenuItemEditor extends React.PureComponent<MenuInfoProps> {
  state: MenuInfoState = {
    menu: null
  }

  static getDerivedStateFromProps(nextProps: MenuInfoProps) {
    const nextState: MenuInfoState = { menu: null };
    const { menus, selectedKeys } = nextProps;
    if (_.isArray(menus) && !_.isEmpty(selectedKeys)) {
      const menuItem: MenuItem = findMenuItem(menus, selectedKeys[0]);
      if (_.isObject(menuItem)) {
        nextState.menu = menuItem;
      }
    }
    return nextState;
  }

  render(): React.ReactNode {
    const { menu } = this.state;
    if (!_.isObject(menu)) {
      // null
      return <EmptyStatus />
    }
    // page
    return (
      <>
        <MenuItemInfo menu={menu} />
        <Divider style={{ borderColor: 'transparent',  margin: '8px 0 0' }} />
        { menu.type === 2 && <PermissionList menu={menu} />}
      </>
    );
  }
}

export default connect(({ menuManagement }: ConnectState) => ({
  menus: menuManagement.list,
  selectedKeys: menuManagement.selectedKeys
}))(MenuItemEditor);