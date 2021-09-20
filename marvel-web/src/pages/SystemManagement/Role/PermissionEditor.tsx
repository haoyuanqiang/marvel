import React from 'react';
import { Modal, Spin, Tree, Tag } from 'antd';
import type { FormInstance } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { FileOutlined, KeyOutlined } from '@ant-design/icons';
import type { ConnectState, Menu, Permission, RolePermissions } from './models/connect';
import styles from './style.less';

type RolePermissionProps = {
  dispatch: Dispatch;
  menus: Menu[];
  permissions: Permission[];
  data: RolePermissions;
  visible: boolean;
  timestamp: number;
}

type RolePermissionState = {
  roleId?: string;
  roleName?: string;
  menuIds?: string[];
  permissionIds?: string[];
  selectedKeys: string[];
  timestamp: number;
  halfCheckedMenuIds: string[];
}

const TAG_COLOR_POOL = {
  GET: '#67C23A',
  POST: '#8CC5FF',
  PUT: '#E6A23C',
  DELETE: '#F56C6C'
}

class RolePermission extends React.PureComponent<RolePermissionProps> {
  formRef = React.createRef<FormInstance>();

  state: RolePermissionState = {
    selectedKeys: [],
    halfCheckedMenuIds: [],
    timestamp: 0
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { data, timestamp } = nextProps;
    const { timestamp: prevTimestamp } = prevState;
    if (timestamp !== prevTimestamp) {
      return {
        ...data,
        selectedKeys: [
          ..._.get(data, 'menuIds', []),
          ..._.get(data, 'permissionIds', [])
        ],
        timestamp
      }
    }
    return null;
  }

  onOk = () => {
    const data = _.pick(this.state, ['roleId', 'menuIds', 'permissionIds', 'halfCheckedMenuIds']);
    const { dispatch } = this.props;
    dispatch({
      type: 'rolePermission/submit',
      payload: data
    })
  }

  onCancel = () => {
    const { dispatch } = this.props;
    dispatch({ type: 'rolePermission/save', payload: { visible: false } })
  }

  onCheck = (checkedKeys, { checkedNodes, halfCheckedKeys }) => {
    const menuIds: string[] = [];
    const permissionIds: string[] = [];
    if (_.isArray(checkedNodes)) {
      _.forEach<Menu | Permission>(checkedNodes, node => {
        if (node.isPermission) {
          permissionIds.push(node.id);
        } else {
          menuIds.push(node.id);
        }
      })
    }
    this.setState({
      selectedKeys: checkedKeys,
      halfCheckedMenuIds: halfCheckedKeys,
      menuIds,
      permissionIds
    });
  }

  renderIcon = (props) => {
    const { data } = props;
    if (_.isObject(data)) {
      const { iconName } = data;
      if (iconName === 'FileOutlined') {
        return <FileOutlined color="rgba(0, 0, 0, 0.85)" />
      }
      if (iconName === 'KeyOutlined') {
        return <KeyOutlined color="rgba(0, 0, 0, 0.85)" />
      }
    }
    return undefined;
  }

  renderTreeTitle = (nodeData) => {
    if (nodeData.isPermission && nodeData.method) {
      return (
        <>
          <Tag 
            color={TAG_COLOR_POOL[nodeData.method] || '#87d068'} 
            size="small"
          >
            {nodeData.method}
          </Tag>
          {nodeData.title}
        </>
      )
    }
    return _.get(nodeData, 'title', '--')
  }

  render() {
    const { loading, menus, visible } = this.props;
    const { roleName, selectedKeys } = this.state;
    return (
      <Modal
        destroyOnClose
        maskClosable={false}
        title={`分配权限 - ${roleName || ''}`}
        visible={visible}
        onCancel={this.onCancel}
        onOk={this.onOk}
      >
        <Spin spinning={loading}>
          <div style={{ border: '1px solid #DCDFE6', height: 340, overflow: 'auto' }}>
            {!loading && <Tree
              className={styles['marvel-tree']}
              defaultCheckedKeys={selectedKeys}
              checkable
              icon={this.renderIcon}
              selectable={false}
              showIcon
              titleRender={this.renderTreeTitle}
              treeData={menus}
              onCheck={this.onCheck}
            />}
          </div>
        </Spin>
      </Modal>
    )
  }
}

export default connect(({ rolePermission, loading }: ConnectState) => ({
  loading: loading.effects['rolePermission/fetchData'] || loading.effects['rolePermission/fetchMenusAndPermissions'],
  menus: rolePermission.menus,
  permissions: rolePermission.permissions,
  data: rolePermission.data,
  timestamp: rolePermission.timestamp,
  visible: rolePermission.visible
}))(RolePermission);
