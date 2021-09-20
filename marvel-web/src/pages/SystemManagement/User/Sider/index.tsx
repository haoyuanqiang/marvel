import React from 'react';
import { Tree, Input } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import type { ConnectState, Department } from '../models/connect';
import styles from './sider.less';

const { Search } = Input;

const generateList = (data, dataList) => {
  for (let i = 0; i < data.length; i += 1) {
    const node = data[i];
    const { key, title } = node;
    dataList.push({ key, title });
    if (node.children) {
      generateList(node.children, dataList);
    }
  }
};

function getParentKey(key, tree) {
  let parentKey;
  for (let i = 0; i < tree.length; i += 1) {
    const node = tree[i];
    if (node.children) {
      if (node.children.some((item) => item.key === key)) {
        parentKey = node.key;
      } else if (getParentKey(key, node.children)) {
        parentKey = getParentKey(key, node.children);
      }
    }
  }
  return parentKey;
}

type SiderSearchTreeProps = {
  dispatch: Dispatch;
  departments: Department[];
  loading: boolean;
  // selectedKeys: string[];
};

type SiderSearchTreeState = {
  data: Department[];
  dataList: Department[];
  expandedKeys: string[];
  searchValue: string;
  autoExpandParent: boolean;
};

class SiderSearchTree extends React.PureComponent<SiderSearchTreeProps, SiderSearchTreeState> {
  state: SiderSearchTreeState = {
    expandedKeys: [],
    searchValue: '',
    autoExpandParent: true,
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    const nextState = {};
    const { departments } = nextProps;
    if (prevState.data !== departments) {
      nextState.data = departments;
      nextState.dataList = [];
      generateList(departments, nextState.dataList);
    }
    if (Object.keys(nextState).length > 0) {
      return nextState;
    }
    return null;
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'departmentTree/fetch',
    });
  }

  onExpand = (expandedKeys) => {
    this.setState({
      expandedKeys,
      autoExpandParent: false,
    });
  };

  onChange = (event) => {
    const { value } = event.target;
    const { data, dataList } = this.state;
    const expandedKeys = dataList
      .map((item) => {
        if (item.title.indexOf(value) > -1) {
          return getParentKey(item.key, data);
        }
        return null;
      })
      .filter((item, i, self) => item && self.indexOf(item) === i);
    this.setState({
      expandedKeys,
      searchValue: value,
      autoExpandParent: true,
    });
  };

  onSelect = selectedKeys => {
    const { dispatch } = this.props;
    dispatch({
      type: 'users/changeDepartment',
      payload: {
        departmentId: !_.isEmpty(selectedKeys) ? selectedKeys[0] : null
      }
    });
  }

  loop = (data) => { 
    const { searchValue } = this.state;
    return data.map((item) => {
      const index = item.title.indexOf(searchValue);
      const beforeStr = item.title.substr(0, index);
      const afterStr = item.title.substr(index + searchValue.length);
      const title = index > -1 ? (
        <span>
          {beforeStr}
          <span style={{ backgroundColor: '#409EFF', color: 'white' }}>{searchValue}</span>
          {afterStr}
        </span>
      ) : (
        <span>{item.title}</span>
      );
      if (item.children) {
        return { title, key: item.key, children: this.loop(item.children) };
      }
      return {
        title,
        key: item.key,
      };
    });
  }

  render() {
    // const { selectedKeys } = this.props;
    const { expandedKeys, autoExpandParent, data } = this.state;
    return (
      <div className={styles['marvel-sider']}>
        <Search 
          style={{ marginBottom: 8 }} 
          placeholder="请输入部门名称搜索"
          onChange={this.onChange} 
        />
        <Tree
          autoExpandParent={autoExpandParent}
          className={styles['marvel-sider-tree']}
          expandedKeys={expandedKeys}
          // selectedKeys={selectedKeys}
          treeData={this.loop(data)}
          onExpand={this.onExpand}
          onSelect={this.onSelect}
        />
      </div>
    );
  }
}

export default connect(({ departmentTree, loading }: ConnectState) => ({
  loading: loading.effects['departmentTree/fetch'],
  departments: departmentTree.list,
  // selectedKeys: departmentTree.selectedKeys
}))(SiderSearchTree);
