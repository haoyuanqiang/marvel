import React from 'react';
import { Layout } from 'antd';
import MenuTreeEditor from './Sider';
import MenuItemEditor from './Content';
import styles from './style.less';

const { Sider, Content } = Layout;

const MenuManagement: React.FC = (): React.ReactNode => {
  return (
    <Layout className={styles['marvel-layout']}>
      <Sider theme="light" width={300}>
        <MenuTreeEditor />
      </Sider>
      <Content>
        <MenuItemEditor />
      </Content>
    </Layout>
  );
};

export default MenuManagement;
