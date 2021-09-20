import React from 'react';
import { Layout } from 'antd';
import SiderDepartmentTree from './Sider';
import ContentUserList from './Content'
import styles from './style.less';

const { Sider, Content } = Layout;

const UserManagement: React.FC = (): React.ReactNode => {

  return (
    <Layout className={styles['marvel-layout']}>
      <Sider theme="light" width={240}>
        <SiderDepartmentTree />
      </Sider>
      <Content>
        <ContentUserList />
      </Content>
    </Layout>
  )
}

export default UserManagement;
