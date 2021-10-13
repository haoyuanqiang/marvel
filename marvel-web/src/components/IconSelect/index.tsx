import React from 'react';
import classNames from 'classnames';
import styles from './index.less';
import { Dropdown } from 'antd';
// import { SearchOutlined } from '@ant-design/icons';
import * as AntdIcons from '@ant-design/icons';
import IconPanel from './IconPanel';

type IconSelectProps = {
  className?: string;
  defaultValue?: string;
  value?: string;
  onChange?: (value: string) => void;
};

type IconSelectState = {
  value?: string;
  type: 'Outlined' | 'Filled' | 'TwoTone';
};

class IconSelect extends React.PureComponent<IconSelectProps, IconSelectState> {
  state: IconSelectState = {
    type: 'Outlined',
    value: undefined,
  };

  componentDidMount() {
    const { defaultValue } = this.props;
    this.setState({ value: defaultValue });
  }

  onSelect = (value: string) => {
    const { onChange } = this.props;
    if (typeof onChange === 'function') {
      this.state.value = value;
      onChange(value);
    } else {
      this.setState({ value });
    }
  };

  renderOverlay = () => {
    const value = this.props.value || this.state.value;
    return (
      <IconPanel
        value={typeof value !== 'undefined' ? value : this.state.value}
        onSelect={this.onSelect}
      />
    );
  };

  renderValue = () => {
    const value = this.props.value || this.state.value;
    if (value) {
      return (
        <span className={styles['marvel-icon-select-placeholder']} style={{ color: 'unset' }}>
          {React.createElement(AntdIcons[value])}
          {value ? ' ' : ''}
          {value}
        </span>
      );
    }
    return <span className={styles['marvel-icon-select-placeholder']}>请输入</span>;
  };

  render() {
    const { className } = this.props;
    return (
      <Dropdown overlay={this.renderOverlay} trigger={['click']}>
        <div className={classNames(styles['marvel-icon-select'], className)}>
          {this.renderValue()}
          {React.createElement(AntdIcons.SearchOutlined, {
            className: styles['marvel-icon-select-suffix'],
          })}
        </div>
      </Dropdown>
    );
  }
}

export default IconSelect;
