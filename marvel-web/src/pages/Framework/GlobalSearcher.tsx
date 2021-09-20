import React from 'react';
import { SearchOutlined } from '@ant-design/icons';
import styles from './style.less';

const GlobalSearcher: React.FC = () => {
  return (
    <SearchOutlined className={styles['marvel-framework-sider-trigger']} />
  )
}

export default GlobalSearcher;
