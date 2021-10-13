import React from 'react';
import { Button, Divider, Space, Table, Tag } from 'antd';
import moment from 'moment';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import type { ConnectState, User, Pagination } from '../models/connect';
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import UserEditor from './UserEditor';
import UserPassword from './UserPassword';

type UserListProps = {
  dispatch: Dispatch;
  list: User[];
  pagination: Pagination;
  selectedKeys: React.Key[];
  loading: boolean;
};

class UserList extends React.PureComponent<UserListProps> {
  columns = [
    {
      dataIndex: 'code',
      ellipsis: true,
      title: '用户编码',
      width: 100,
    },
    {
      dataIndex: 'name',
      ellipsis: true,
      title: '用户名称',
      width: 100,
    },
    {
      dataIndex: 'nickname',
      ellipsis: true,
      title: '用户昵称',
      width: 100,
    },
    {
      dataIndex: 'loginName',
      ellipsis: true,
      title: '登录名称',
      width: 100,
    },
    {
      dataIndex: 'sex',
      ellipsis: true,
      title: '性别',
      width: 80,
      render: (value) => {
        if (value === 1) {
          return <Tag color="processing">男</Tag>;
        }
        if (value === 2) {
          return <Tag color="default">女</Tag>;
        }
        return <Tag color="warning">保密</Tag>;
      },
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
      width: 100,
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
      width: 140,
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
            <Divider type="vertical" style={{ margin: 0 }} />
            <Button
              type="link"
              size="small"
              onClick={() => {
                this.onUpdatePassword(record);
              }}
            >
              更新密码
            </Button>
          </Space>
        );
      },
    },
  ];

  componentDidMount() {
    const { dispatch, pagination } = this.props;
    dispatch({
      type: 'users/fetch',
      payload: {
        ..._.pick(pagination, ['pageSize', 'current']),
      },
    });
  }

  onCreate = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'userEditor/initialize',
      payload: { isCreate: true },
    });
  };

  onEdit = (id: string) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'userEditor/initialize',
      payload: { isCreate: false, id },
    });
  };

  onDelete = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'users/delete',
    });
  };

  onUpdatePassword = (user) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'userPassword/save',
      payload: {
        user,
        visible: true,
      },
    });
  };

  onRowSelectionChange = (selectedRowKeys: React.Key[]) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'users/save',
      payload: {
        selectedKeys: selectedRowKeys,
      },
    });
  };

  render() {
    const { list, pagination, selectedKeys } = this.props;
    return (
      <>
        <div style={{ marginBottom: 8 }}>
          <Space>
            <Button icon={<PlusOutlined />} type="primary" onClick={this.onCreate}>
              新建
            </Button>
            <Button
              danger
              disabled={_.isEmpty(selectedKeys)}
              icon={<DeleteOutlined />}
              type="default"
              onClick={this.onDelete}
            >
              删除
            </Button>
          </Space>
          <UserEditor />
          <UserPassword />
        </div>
        <Table
          columns={this.columns}
          dataSource={list}
          pagination={{
            ...pagination,
            showSizeChanger: true,
          }}
          rowSelection={{
            onChange: this.onRowSelectionChange,
          }}
          scroll={{ y: 'calc(100vh - 95px)' }}
        />
      </>
    );
  }
}

export default connect(({ users, loading }: ConnectState) => ({
  list: users.list,
  pagination: users.pagination,
  selectedKeys: users.selectedKeys,
  loading: loading.effects['users/fetch'],
}))(UserList);
