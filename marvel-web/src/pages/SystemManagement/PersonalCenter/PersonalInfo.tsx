import React from 'react';
import { Card, Image, List, Space, Typography } from 'antd';
import { connect } from 'umi';
import moment from 'moment';
import type { ConnectState, UserModelState } from './models/connect';
import styles from './styles.less';
import {
  ApartmentOutlined,
  FieldTimeOutlined,
  MailOutlined,
  MobileOutlined,
  UserOutlined,
} from '@ant-design/icons';

const { Title, Text } = Typography;

type PersonalInfoProps = {
  loading: boolean;
  userInfo: UserModelState;
};

class PersonalInfo extends React.PureComponent<PersonalInfoProps> {
  renderUserInfo = () => {
    const { userInfo } = this.props;
    return (
      <List bordered={false}>
        <List.Item>
          <Space>
            <UserOutlined />
            <span>
              <Text strong>用户名称</Text>
            </span>
          </Space>
          <span style={{ float: 'right' }}>{_.get(userInfo, 'name', '')}</span>
        </List.Item>
        <List.Item>
          <Space>
            <MobileOutlined />
            <span>
              <Text strong>手机号码</Text>
            </span>
          </Space>
          <span style={{ float: 'right' }}>{_.get(userInfo, 'telephone', '')}</span>
        </List.Item>
        <List.Item>
          <Space>
            <MailOutlined />
            <span>
              <Text strong>用户邮箱</Text>
            </span>
          </Space>
          <span style={{ float: 'right' }}>{_.get(userInfo, 'email', '')}</span>
        </List.Item>
        <List.Item>
          <Space>
            <ApartmentOutlined />
            <span>
              <Text strong>所属部门</Text>
            </span>
          </Space>
          <span style={{ float: 'right' }}>{_.get(userInfo, 'departmentName', '')}</span>
        </List.Item>
        <List.Item>
          <Space>
            <FieldTimeOutlined />
            <span>
              <Text strong>更新时间</Text>
            </span>
          </Space>
          <span style={{ float: 'right' }}>
            {moment(_.get(userInfo, 'modifyTime', Date.now())).format('YYYY-MM-DD HH:mm:ss')}
          </span>
        </List.Item>
      </List>
    );
  };

  render() {
    return (
      <Card className={styles['marvel-card-shadow']} title={<Title level={5}>个人信息</Title>}>
        <div className={styles['marvel-image-container']}>
          <Image
            className={styles['marvel-image-portrait']}
            width={120}
            height={120}
            src="/jkjgkE.png"
            placeholder={true}
          />
        </div>
        {this.renderUserInfo()}
      </Card>
    );
  }
}

export default connect(({ userInfo, loading }: ConnectState) => ({
  loading: loading.effects['userInfo/fetch'],
  userInfo,
}))(PersonalInfo);
