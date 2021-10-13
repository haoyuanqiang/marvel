import React from 'react';
import { Space } from 'antd';
import { CodeSandboxOutlined } from '@ant-design/icons';
import styles from './style.less';

type SystemLogoProps = {
  collapsed?: boolean;
};

const SystemLogo: React.FC<SystemLogoProps> = (props: SystemLogoProps) => {
  const { collapsed } = props;
  return (
    <div className={styles['marvel-framework-logo']}>
      <Space
        align="center"
        style={{
          margin: 'auto',
          padding: !collapsed ? '0 24px' : undefined,
          width: !collapsed ? '100%' : undefined,
        }}
      >
        <CodeSandboxOutlined />
        {!collapsed ? 'Marvel' : null}
      </Space>
    </div>
  );
};

export default SystemLogo;
