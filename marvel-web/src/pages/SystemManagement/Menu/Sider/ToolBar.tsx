import React from 'react';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { Button, Space, Tooltip } from 'antd';
import type { ConnectState } from '../models/connect';
import { DeleteOutlined, DragOutlined, PlusOutlined, EditOutlined } from '@ant-design/icons';
import type { MenuItem } from '../models/menus';
import styles from '../style.less';

interface MenuToolBarProps {
  dispatch: Dispatch;
  menus?: MenuItem[];
  selectedKeys?: string[];
}

interface MenuToolBarState {
  disableEdit: boolean;
  disableDel: boolean;
  disableMove: boolean;
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

class ToolBar extends React.PureComponent<MenuToolBarProps, MenuToolBarState> {
  state: MenuToolBarState = {};

  static getDerivedStateFromProps(nextProps: MenuTreeEditorProps) {
    const nextState: MenuToolBarState = {
      disableEdit: true,
      disableDel: true,
      disableMove: true,
    };
    const { menus, selectedKeys } = nextProps;
    nextState.disableMove = _.isEmpty(menus);
    if (!_.isEmpty(selectedKeys)) {
      const menuItem: MenuItem = findMenuItem(menus, selectedKeys[0]);
      if (_.isObject(menuItem)) {
        nextState.disableEdit = false;
        nextState.disableDel = !_.isEmpty(menuItem.children);
      }
    }
    return nextState;
  }

  onMenuAdd = (): void => {
    const { dispatch } = this.props;
    dispatch({
      type: 'menuEditor/initialize',
      payload: { isCreate: true },
    });
  };

  onMenuModify = (): void => {
    const { dispatch } = this.props;
    dispatch({
      type: 'menuEditor/initialize',
      payload: { isCreate: false },
    });
  };

  onDelete = (): void => {
    const { dispatch } = this.props;
    dispatch({
      type: 'menuManagement/delete',
    });
  };

  render(): React.ReactNode {
    const { disableEdit, disableDel, disableMove } = this.state;
    return (
      <Space className={styles['marvel-sider-toolbox']}>
        <Tooltip title="新建">
          <Button icon={<PlusOutlined />} size="small" type="primary" onClick={this.onMenuAdd} />
        </Tooltip>
        <Tooltip title="修改">
          <Button
            disabled={disableEdit}
            icon={<EditOutlined />}
            size="small"
            style={{
              backgroundColor: '#87d068',
              borderColor: '#87d068',
              color: 'white',
            }}
            onClick={this.onMenuModify}
          />
        </Tooltip>
        <Tooltip title="删除">
          <Button
            danger
            disabled={disableDel}
            icon={<DeleteOutlined />}
            size="small"
            onClick={this.onDelete}
          />
        </Tooltip>
        <Tooltip title="排序">
          <Button
            disabled={disableMove}
            icon={<DragOutlined />}
            size="small"
            style={{
              backgroundColor: '#87d068',
              borderColor: '#87d068',
              color: 'white',
            }}
          />
        </Tooltip>
      </Space>
    );
  }
}

export default connect(({ menuManagement }: ConnectState) => ({
  menus: menuManagement.list,
  selectedKeys: menuManagement.selectedKeys,
}))(ToolBar);
