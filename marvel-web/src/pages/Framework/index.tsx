import React from 'react';
import { Col, Layout, Space, Row } from 'antd';
import { MenuUnfoldOutlined, MenuFoldOutlined } from '@ant-design/icons';
import LoggedInUserInfo from './LoginUserInfo';
import SystemLogo from './SystemLogo';
import SiderMenu from './SiderMenu';
import GlobalSearcher from './GlobalSearcher';
import TabsPageLayout from './TabsPageLayout';
import styles from './style.less';

const { Header, Sider, Content } = Layout;

class Framework extends React.Component {
  state = {
    collapsed: false,
  };

  toggle = () => {
    this.setState({
      collapsed: !this.state.collapsed,
    });
  };

  render() {
    const { collapsed } = this.state;
    const IconComponent = collapsed ? MenuUnfoldOutlined : MenuFoldOutlined;
    return (
      <Layout className={styles['marvel-framework']}>
        <Sider trigger={null} collapsible collapsed={collapsed}>
          <SystemLogo collapsed={collapsed} />
          <SiderMenu />
        </Sider>
        <Layout>
          <Header className={styles['marvel-framework-global-header']}>
            <Row>
              <Col span={12}>
                <div className={styles['marvel-framework-global-header-left']}>
                  <Space align="center">
                    <IconComponent
                      className={styles['marvel-framework-sider-trigger']}
                      onClick={this.toggle}
                    />
                  </Space>
                </div>
              </Col>
              <Col span={12} style={{ textAlign: 'right' }}>
                <div className={styles['marvel-framework-global-header-right']}>
                  <Space align="center" size={24}>
                    <GlobalSearcher />
                    <LoggedInUserInfo />
                  </Space>
                </div>
              </Col>
            </Row>
          </Header>
          <Content className={styles['marvel-framework-content']}>
            <TabsPageLayout />
          </Content>
        </Layout>
      </Layout>
    );
  }
}

export default Framework;
