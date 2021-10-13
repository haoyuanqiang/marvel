import React from 'react';
import { Card, Layout, Tabs, Typography } from 'antd';
import { connect } from 'umi';
import type { Dispatch } from 'umi';
import PersonalInfo from './PersonalInfo';
import BasicInfoModifier from './BasicInfoModifier';
import PasswordModifier from './PasswordModifier';
import styles from './styles.less';

const { Sider, Content } = Layout;
const { Title } = Typography;
const { TabPane } = Tabs;

type PersonalCenterProps = {
  dispatch: Dispatch;
};

class PersonalCenter extends React.PureComponent<PersonalCenterProps> {
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'userInfo/fetch',
    });
  }

  render() {
    return (
      <Layout className={styles['marvel-layout']}>
        <Sider theme="light" width={320}>
          <PersonalInfo />
        </Sider>
        <Content>
          <Card className={styles['marvel-card-shadow']} title={<Title level={5}>基本资料</Title>}>
            <Tabs defaultActiveKey="1">
              <TabPane tab="基础资料" key="1">
                <BasicInfoModifier />
              </TabPane>
              <TabPane tab="修改密码" key="2">
                <PasswordModifier />
              </TabPane>
            </Tabs>
          </Card>
        </Content>
      </Layout>
    );
  }
}

export default connect()(PersonalCenter);
