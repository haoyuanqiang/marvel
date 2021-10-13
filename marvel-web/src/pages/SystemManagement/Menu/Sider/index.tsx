import React from 'react';
import { Typography } from 'antd';
import MenuTree from './MenuTree';
import MenuToolBar from './ToolBar';
import MenuEditor from './MenuEditor';
import styles from '../style.less';

const { Text } = Typography;

const MenuTreeEditor: React.FC = (): React.ReactNode => (
  <>
    <div className={styles['marvel-sider-title']}>
      <Text strong>菜单列表</Text>
    </div>
    <MenuToolBar />
    <div className={styles['marvel-sider-children']}>
      <MenuTree />
    </div>
    <MenuEditor />
  </>
);

export default MenuTreeEditor;
