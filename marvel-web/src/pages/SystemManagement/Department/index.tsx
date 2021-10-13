import { Button, Divider, Input, Layout, Space, Tag, Table } from 'antd';
import moment from 'moment';
import React from 'react';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import type { ConnectState, Department } from './models/connect';
import Editor from './Editor';

const { Content } = Layout;

type DepartmentManagementProps = {
  dispatch: Dispatch;
  list: Department[];
};

type DepartmentManagementState = {
  name: string;
};

function findDepartments(departments: Department[], departmentName: string): Department[] {
  const list: Department[] = [];
  const traceTree = (department: Department) => {
    if (department.name.indexOf(departmentName) >= 0) {
      list.push(_.cloneDeep(_.omit(department, 'children')));
    }
    if (_.isArray(department.children)) {
      department.children.forEach((item) => {
        traceTree(item);
      });
    }
  };
  if (_.isArray(departments)) {
    departments.forEach((item) => {
      traceTree(item);
    });
  }
  return list;
}

class DepartmentManagement extends React.PureComponent<
  DepartmentManagementProps,
  DepartmentManagementState
> {
  columns = [
    {
      dataIndex: 'name',
      ellipsis: true,
      title: '部门名称',
      width: 120,
    },
    {
      dataIndex: 'code',
      ellipsis: true,
      title: '部门编码',
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
                this.onCreate({ parentId: record.id });
              }}
            >
              新建
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
            <Divider type="vertical" style={{ margin: 0 }} />
            <Button
              danger
              type="link"
              size="small"
              onClick={() => {
                this.onDeleteSingle(record.id);
              }}
            >
              删除
            </Button>
          </Space>
        );
      },
    },
  ];

  state: DepartmentManagementState = {
    name: '',
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'departments/fetch' });
  }

  onCreate = (params) => {
    const { dispatch } = this.props;
    dispatch({ type: 'departments/edit', payload: { ...params, isCreate: true } });
  };

  onEdit = (id) => {
    const { dispatch } = this.props;
    dispatch({ type: 'departments/edit', payload: { isCreate: false, id } });
  };

  onDeleteSingle = (id) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'departments/deleteSingle',
      payload: {
        keys: [id],
      },
    });
  };

  onDelete = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'departments/delete',
    });
  };

  onRowSelectionChange = (selectedRowKeys: string[]) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'departments/save',
      payload: {
        selectedRowKeys,
      },
    });
  };

  onSearch = (value) => {
    this.setState({ name: value });
  };

  render() {
    const { list, loading, selectedRowKeys } = this.props;
    const { name } = this.state;
    let dataSource = list;
    if (_.isString(name) && !_.isEmpty(name)) {
      dataSource = findDepartments(list, name);
    }
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
              <Input.Search placeholder="请输入" onSearch={this.onSearch} />
            </Space>
          </div>
          <Table
            bordered={false}
            columns={this.columns}
            dataSource={dataSource}
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
        </Content>
      </Layout>
    );
  }
}

export default connect(({ departments, loading }: ConnectState) => ({
  loading: loading.effects['departments/fetch'],
  list: departments.list,
  selectedRowKeys: departments.selectedRowKeys,
}))(DepartmentManagement);
