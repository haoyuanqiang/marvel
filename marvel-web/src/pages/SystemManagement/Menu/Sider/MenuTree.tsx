import React from 'react';
import { connect, FormattedMessage } from 'umi';
import type { Dispatch } from 'umi';
import { Spin, Tree } from 'antd';
import type { DataNode } from 'antd/es/tree';
import * as AntdIcon from '@ant-design/icons';
import type { Menu } from '../models/connect';
import type { ConnectState } from '../models/connect';
import styles from '../style.less';

interface MenuTreeProps {
  dispatch: Dispatch;
  loading?: boolean;
  menus?: Menu[];
  selectedKeys: string[];
}

interface MenuTreeState {
  menus?: Menu[];
  tree?: DataNode[];
}


class MenuTree extends React.PureComponent<MenuTreeProps, MenuTreeState> {
  state: MenuTreeState = {
    tree: []
  }

  static getDerivedStateFromProps(nextProps: MenuTreeProps, prevState: MenuTreeState) {
    const nextState: MenuTreeState = {};
    const { menus } = nextProps;
    if (menus !== prevState.menus) {
      nextState.menus = menus;
      nextState.tree = menus;
    }
    if (Object.keys(nextState).length > 0) {
      return nextState;
    }
    return null;
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'menuManagement/fetch' })
  }

  onSelect = (selectedKeys: (string | number)[]) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'menuManagement/save',
      payload: { selectedKeys: [...selectedKeys] }
    })
    if (!_.isEmpty(selectedKeys)) {
      dispatch({
        type: 'permissions/change',
        payload: {
          menuId: selectedKeys[0]
        }
      })
    }
  }

  render(): React.ReactNode {
    const { loading, selectedKeys } = this.props;
    if (loading) {
      return <Spin style={{ padding: 30, width: '100%' }} />
    }
    const { tree } = this.state;
    return (
      <Tree.DirectoryTree
        className={styles['marvel-tree']}
        defaultExpandAll
        multiple={false}
        selectedKeys={selectedKeys}
        size="large"
        treeData={tree}
        onSelect={this.onSelect}
      />
    )
  }
}

export default connect(({ menuManagement, loading }: ConnectState) => ({
  menus: menuManagement.list,
  selectedKeys: menuManagement.selectedKeys,
  loading: loading.effects['menuManagement/fetch']
}))(MenuTree);
