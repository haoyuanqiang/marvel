import React from 'react';
import { Dropdown, Menu, Space } from 'antd';
import classNames from 'classnames';
// import { RiFlagFill } from "@react-icons/all-files/ri/RiFlagFill";
// import { TiArrowSortedDown } from '@react-icons/all-files/ti/TiArrowSortedDown';
import { CaretDownOutlined, FlagFilled } from '@ant-design/icons';
import styles from './index.less';

interface SelectLanguageProps {
  className?: string;
}

class SelectLanguage extends React.PureComponent<SelectLanguageProps> {

  render() {
    const { className } = this.props;
    const menu = (
      <Menu>
        <Menu.Item key="1" icon={<FlagFilled />}>
          简体中文
        </Menu.Item>
        <Menu.Item key="2" icon={<FlagFilled />}>
          English
        </Menu.Item>
      </Menu>
    );
    return (
      <Dropdown
        overlay={menu}
        overlayClassName={styles['marvel-selectLang-overlay']}
        trigger={['click']}
      >
        <div
          className={classNames(styles['marvel-selectLang'], className)}
        >
          <FlagFilled />
          <span>Language</span>
          <CaretDownOutlined />
        </div>
      </Dropdown>
    )
  }
}

export default SelectLanguage;
