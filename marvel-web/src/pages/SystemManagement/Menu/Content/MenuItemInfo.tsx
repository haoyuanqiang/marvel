import React from 'react';
import type { Dispatch } from 'umi';
import { connect } from 'umi';
import { Descriptions, Typography, Form, Row, Col, Input, Button, Space, Avatar, Tag } from 'antd';
import { CloseOutlined, EditOutlined, SaveOutlined, QuestionOutlined } from '@ant-design/icons';
import * as AntdIcons from '@ant-design/icons';
import type { MenuItem } from '../models/menus';

const { Text } = Typography;

interface MenuItemInfoProps {
  dispatch: Dispatch;
  menu: MenuItem;
}

interface MenuItemInfoState {
  defaultValue?: MenuItem;
  readonly: boolean;
  value?: MenuItem;
}

// interface MenuInfoFormProps {
//   readonly: boolean;
//   value: MenuItem;
//   onChange: (value) => void;
// }

function renderIcon(icon: string): React.ReactNode {
  if (AntdIcons[icon]) {
    return React.createElement(AntdIcons[icon]);
  }
  return <QuestionOutlined />;
}

class MenuItemInfo extends React.PureComponent<MenuItemInfoProps, MenuItemInfoState> {
  state: MenuItemInfoState = {
    readonly: true,
  }

  static getDerivedStateFromProps(nextProps: MenuGroupProps, prevState: MenuGroupState) {
    const nextState: MenuGroupState = {};
    const { menu } = nextProps;
    const { defaultValue } = prevState;
    if (_.isObject(menu) && !_.isEqual(menu, defaultValue)) {
      nextState.defaultValue = menu;
      nextState.value = _.cloneDeep(nextState.defaultValue);
    }
    if (!_.isEmpty(nextState)) {
      return nextState;
    }
    return null;
  }

  enableEdit = (): void => {
    this.setState({ readonly: false });
  }

  onChange = (changedValues) => {
    const { value } = this.state;
    const nextValue = _.cloneDeep(_.merge(value, changedValues));
    this.setState({ value: nextValue });
  }

  onSave = () => {
    const { value } = this.state;
    const payload = _.pick(value, [
      'id', 'code', 'name', 'icon', 'parentId', 'sortNumber', 'type', 'path'
    ]);
    const { dispatch } = this.props;
    dispatch({
      type: 'menuManagement/modifyMenu',
      payload
    });
    this.setState({ readonly: true })
  }

  onCancel = () => {
    const { defaultValue } = this.state;
    this.setState({ readonly: true, value: defaultValue });
  }

  renderMenuType = (value) => {
    if (value === 1) {
      return <Tag color="magenta">目录</Tag>
    }
    if (value === 2) {
      return <Tag color="gold">页面</Tag>
    }
    return null;
  }

  renderMenuVisible = (value) => {
    if (value === 1) {
      return <Tag color="geekblue">显示</Tag>
    }
    if (value === 2) {
      return <Tag color="orange">隐藏</Tag>
    }
    return null;
  }

  renderMenuStatus = (value) => {
    if (value === 1) {
      return <Tag color="geekblue">正常</Tag>
    }
    if (value === 2) {
      return <Tag color="orange">停用</Tag>
    }
    return null;
  }

  renderRouteType = (value) => {
    if (value === 1) {
      return <Tag color="geekblue">系统</Tag>
    }
    if (value === 2) {
      return <Tag color="orange">模块</Tag>
    }
    if (value === 3) {
      return <Tag color="volcano">外链</Tag>
    }
    return null;
  }

  render(): React.ReactNode {
    const { value } = this.state;
    return (
      <>
        <div style={{ height: 32, lineHeight: '32px' }}>
          <Space>
            <Text strong>菜单信息</Text>
          </Space>
        </div>
        <div style={{ border: '1px solid #ccc', padding: '8px' }}>
          <div style={{ verticalAlign: 'middle' }}>
            <div 
              style={{ 
                display: 'inline-block', 
                height: 80,
                lineHeight: '72px',
                textAlign: 'center', 
                verticalAlign: 'middle',
                width: 120 
              }}
            >
              <Avatar 
                icon={renderIcon(value.iconName)} 
                size={64}
                style={{ backgroundColor: '#87d068' }}
              />
            </div>
            <div style={{ display: 'inline-block', verticalAlign: 'top', width: 'calc(100% - 120px)' }}>
              <Descriptions size="small">
                <Descriptions.Item label={<Text strong>菜单编码</Text>}>
                  {_.get(value, 'code', '')}
                </Descriptions.Item>
                <Descriptions.Item label={<Text strong>菜单名称</Text>}>
                  {_.get(value, 'name', '')}
                </Descriptions.Item>
                <Descriptions.Item label={<Text strong>菜单类型</Text>}>
                  {this.renderMenuType(_.get(value, 'type', 0))}
                </Descriptions.Item>
                <Descriptions.Item label={<Text strong>显示状态</Text>}>
                  {this.renderMenuVisible(_.get(value, 'visible', 0))}
                </Descriptions.Item>
                <Descriptions.Item label={<Text strong>菜单状态</Text>}>
                  {this.renderMenuStatus(_.get(value, 'status', 0))}
                </Descriptions.Item>
                <Descriptions.Item label={<Text strong>显示排序</Text>}>
                  {_.get(value, 'sortNumber', '')}
                </Descriptions.Item>
                {_.get(value, 'type', 0) === 2 && (
                  <>
                    <Descriptions.Item label={<Text strong>路由类型</Text>}>
                      {this.renderRouteType(_.get(value, 'routeType', 0))}
                    </Descriptions.Item>
                    <Descriptions.Item label={<Text strong>路由地址</Text>}>
                      {_.get(value, 'path', '')}
                    </Descriptions.Item>
                  </>
                )}
              </Descriptions>
            </div>
          </div>
        </div>
      </>
    );
  }
}

export default connect()(MenuItemInfo);
