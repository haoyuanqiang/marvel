import { Button, Divider, Input, Layout, Space, Tag, Table, Select } from 'antd';
import moment from 'moment';
import React from 'react';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import type { ConnectState, Role } from './models/connect';
import Editor from './Editor';
import RolePermission from './PermissionEditor';

const { Content } = Layout;

type RoleManagementProps = {
  dispatch: Dispatch;
  list: Role[];
  searchKey: string;
  searchValue: string;
};

class RoleManagement extends React.PureComponent<RoleManagementProps> {
  columns = [
    {
      dataIndex: 'name',
      ellipsis: true,
      title: '角色名称',
      width: 120,
    },
    {
      dataIndex: 'code',
      ellipsis: true,
      title: '角色编码',
      width: 120,
    },
    {
      dataIndex: 'status',
      ellipsis: true,
      title: '状态',
      width: 80,
      render: (value) => {
        if (value === 1) {
          return <Tag color="processing">正常</Tag>;
        }
        if (value === 2) {
          return <Tag color="default">停用</Tag>;
        }
        return <Tag color="warning">未知</Tag>;
      },
    },
    {
      dataIndex: 'modifyTime',
      ellipsis: true,
      title: '更新时间',
      width: 80,
      render: (value) => {
        if (_.isNumber(value)) {
          return moment(value).format('YYYY-MM-DD HH:mm:ss');
        }
        return '';
      },
    },
    {
      align: 'center',
      dataIndex: 'operations',
      title: '操作',
      width: 80,
      render: (value, record) => {
        return (
          <Space size={8}>
            <Button
              type="link"
              size="small"
              onClick={() => {
                this.onAssignPermission(record.id);
              }}
            >
              权限
            </Button>
            <Divider type="vertical" style={{ margin: 0 }} />
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

  componentDidMount() {
    this.fetchData();
  }

  fetchData = () => {
    const { dispatch } = this.props;
    dispatch({ type: 'roles/fetch', payload: { pageSize: 20, current: 1 } });
  };

  onCreate = (params) => {
    const { dispatch } = this.props;
    dispatch({ type: 'roles/edit', payload: { ...params, isCreate: true } });
  };

  onEdit = (id) => {
    const { dispatch } = this.props;
    dispatch({ type: 'roles/edit', payload: { isCreate: false, id } });
  };

  onDelete = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'roles/delete',
    });
  };

  onAssignPermission = (id: string) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'rolePermission/fetchData',
      payload: id,
    });
  };

  onRowSelectionChange = (selectedRowKeys: string[]) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'roles/save',
      payload: {
        selectedRowKeys,
      },
    });
  };

  onSearchKeyChange = (value) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'roles/save',
      payload: {
        searchKey: value,
      },
    });
  };

  onSearchValueChange = (event) => {
    const { value } = event.target;
    const { dispatch } = this.props;
    dispatch({
      type: 'roles/save',
      payload: {
        searchValue: value,
      },
    });
  };

  render() {
    const { list, loading, selectedRowKeys, searchKey, searchValue } = this.props;
    return (
      <Layout style={{ backgroundColor: 'white', height: '100%' }}>
        <Content style={{ height: '100%', padding: 16 }}>
          <div style={{ marginBottom: 8 }}>
            <Space size="small" direction="horizontal">
              <Button type="primary" icon={<PlusOutlined />} onClick={this.onCreate}>
                新建
              </Button>
              <Button danger type="default" icon={<DeleteOutlined />} onClick={this.onDelete}>
                删除
              </Button>
            </Space>
            <Space size="small" direction="horizontal" style={{ float: 'right' }}>
              <Input.Search
                defaultValue={searchValue}
                placeholder="请输入"
                addonBefore={
                  <Select defaultValue={searchKey} onChange={this.onSearchKeyChange}>
                    <Select.Option value="code">角色编码</Select.Option>
                    <Select.Option value="name">角色名称</Select.Option>
                  </Select>
                }
                onChange={this.onSearchValueChange}
                onSearch={this.fetchData}
              />
            </Space>
          </div>
          <Table
            bordered={false}
            columns={this.columns}
            dataSource={list}
            pagination={false}
            loading={loading}
            rowSelection={{
              checkStrictly: false,
              selectedRowKeys,
              onChange: this.onRowSelectionChange,
            }}
            scroll={{ y: 'calc(100vh - 95px)' }}
          />
          <Editor />
          <RolePermission />
        </Content>
      </Layout>
    );
  }
}

export default connect(({ roles, loading }: ConnectState) => ({
  loading: loading.effects['roles/fetch'],
  list: roles.list,
  selectedRowKeys: roles.selectedRowKeys,
  searchKey: roles.searchKey,
  searchValue: roles.searchValue,
}))(RoleManagement);
