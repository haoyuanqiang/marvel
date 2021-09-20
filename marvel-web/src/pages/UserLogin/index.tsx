import React from 'react';
import { Card, Layout, Space } from 'antd';
import classNames from 'classnames';
import { CodeSandboxOutlined } from '@ant-design/icons';
import SelectLanguage from '@/components/SelectLanguage';
import UserLogin from './UserLogin';
import styles from './style.less';

const { Content } = Layout;

const UserLoginLayout: React.FC = () => (
  <Layout className={styles['marvel-login-layout']}>
    <Content className={styles['marvel-login-background']}>
      <Card
        className={styles['marvel-loginbox']}
        bodyStyle={{ padding: 0 }}
        bordered={false}
      >
        <div className={styles['marvel-loginbox-title']}>
          <SelectLanguage className={styles['marvel-login-title-selectLang']} />
          <div
            className={
              classNames(styles['marvel-login-title-effects'], styles['marvel-login-title-content'])
            }
          >
            <Space direction="horizontal">
              <CodeSandboxOutlined />
              Marvel
            </Space>
            
          </div>
          <div
            className={
              classNames(styles['marvel-login-title-effects'], styles['marvel-login-title-desc'])
            }
          >
            —— Marvel后台管理系统
          </div>
        </div>
        <div className={styles['marvel-loginbox-content']}>
          <UserLogin />
        </div>
      </Card>
      <div className={styles['marvel-login-footer']}>
        <span className={styles['marvel-login-copyright-content']}>
          Powered by Marvel | Copyright © marvel.com
        </span>
      </div>
    </Content>
  </Layout>
)

export default UserLoginLayout;
