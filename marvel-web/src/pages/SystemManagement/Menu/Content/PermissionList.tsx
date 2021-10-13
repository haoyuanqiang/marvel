import React from 'react';
import type { Dispatch } from 'umi';
import { connect } from 'umi';
import { Space, Typography, Table, Button, Tag, Modal } from 'antd';
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import type { Permission, Menu, ConnectState } from '../models/connect';
import PermissionEditor from './PermissionEditor';

const { Text } = Typography;

type PermissionListProps = {
  dispatch: Dispatch;
  menu: Menu;
  permissions?: Permission[];
  selectedKeys?: string[];
};

const TAG_COLOR_POOL = {
  GET: '#8CC5FF',
  POST: '#67C23A',
  PUT: '#E6A23C',
  DELETE: '#F56C6C',
};

class PermissionList extends React.PureComponent<PermissionListProps> {
  columns = [
    {
      align: 'center',
      dataIndex: 'serial',
      title: '序号',
      render: (value, record, index) => index + 1,
      width: 60,
    },
    {
      dataIndex: 'code',
      ellipsis: true,
      title: '权限编码',
      width: 120,
    },
    {
      dataIndex: 'name',
      ellipsis: true,
      title: '权限名称',
      width: 120,
    },
    {
      dataIndex: 'method',
      ellipsis: true,
      title: '请求方法',
      width: 90,
      render: (value) => {
        return <Tag color={TAG_COLOR_POOL[value] || '#87d068'}>{value}</Tag>;
      },
    },
    {
      dataIndex: 'route',
      ellipsis: true,
      title: '权限路径',
      width: 200,
    },
    {
      dataIndex: 'sortNumber',
      ellipsis: true,
      title: '排序码',
      width: 60,
    },
    {
      align: 'center',
      dataIndex: 'operations',
      title: '操作',
      width: 100,
      render: (value, record) => {
        return (
          <Space size={8}>
            <Button
              type="link"
              size="small"
              onClick={() => {
                this.onEdit(record.id);
              }}
            >
              修改
            </Button>
          </Space>
        );
      },
    },
  ];

  onCreate = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'permissionEditor/initialize',
      payload: { isCreate: true },
    });
  };

  onEdit = (id) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'permissionEditor/initialize',
      payload: { isCreate: false, id },
    });
  };

  onDelete = () => {
    Modal.confirm({
      title: '确定要删除吗？',
      onOk: () => {
        const { dispatch } = this.props;
        dispatch({
          type: 'permissions/delete',
        });
      },
    });
  };

  onTableSelectedChange = (selectedRowKeys) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'permissions/save',
      payload: {
        selectedKeys: selectedRowKeys,
      },
    });
  };

  renderHeader = () => {
    const { selectedKeys } = this.props;
    return (
      <Space>
        <Button icon={<PlusOutlined />} shape="circle" type="primary" onClick={this.onCreate} />
        <Button
          danger
          disabled={_.isEmpty(selectedKeys)}
          icon={<DeleteOutlined />}
          shape="circle"
          onClick={this.onDelete}
        />
      </Space>
    );
  };

  render(): React.ReactNode {
    const { permissions, selectedKeys } = this.props;
    return (
      <>
        <div style={{ height: 32, lineHeight: '32px' }}>
          <Space>
            <Text strong>菜单权限</Text>
          </Space>
        </div>
        <div style={{ height: 'calc(100vh - 230px)' }}>
          <Table
            columns={this.columns}
            dataSource={permissions}
            pagination={false}
            rowSelection={{
              columnWidth: 40,
              onChange: this.onTableSelectedChange,
              selectedRowKeys: selectedKeys,
            }}
            scroll={{ y: 'calc(100vh - 260px)' }}
            title={this.renderHeader}
          />
        </div>
        <PermissionEditor />
      </>
    );
  }
}

export default connect(({ permissions }: ConnectState) => ({
  permissions: permissions.list,
  selectedKeys: permissions.selectedKeys,
}))(PermissionList);
