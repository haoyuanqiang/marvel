import React from 'react';
import type { MouseEvent, KeyboardEvent } from 'react';
import type { ConnectProps, Dispatch } from 'umi';
import { connect } from 'umi';
import { Tabs } from 'antd';
import _, { isString } from 'lodash';
import type { ConnectState, IframeTabPane } from '@/models/connect';
import styles from './style.less';

const { TabPane } = Tabs;
declare type onTabsEdit = (e: MouseEvent | KeyboardEvent | string, action: 'add' | 'remove') => void;

interface TabLayoutProps extends ConnectProps {
  activeTabPane: string;
  dispatch: Dispatch;
  tabPanes: IframeTabPane[];
}

interface TabLayoutState {
  tabAnimated?: { inkBar: boolean, tabPane: boolean }
}

class TabLayout extends React.PureComponent<TabLayoutProps, TabLayoutState> {
  state: TabLayoutState = {}

  onTabEdit: onTabsEdit = (e: MouseEvent | KeyboardEvent | string, action: 'add' | 'remove') => {
    if (isString(e) && action === 'remove') {
      const { dispatch } = this.props;
      dispatch({
        type: 'openedTabs/remove',
        payload: { key: e }
      });
    }
  }

  onTabChange = (activeKey: string) => {
    const { dispatch } = this.props;
    dispatch({
      type: 'openedTabs/active',
      payload: { key: activeKey }
    })
  }

  combineIframeUrl = (srcPath: string) => {
    if (typeof srcPath === 'string') {
      if (srcPath.startsWith('/module', 0) || srcPath.startsWith('/welcome', 0)) {
        return `${window.location.origin}/#${srcPath}`;
      }
      return `${window.location.origin}${srcPath}`;
    }
    return null;
  }

  render(): React.ReactNode {
    const { tabAnimated } = this.state;
    const { activeTabPane, tabPanes } = this.props;
    return (
      <Tabs
        activeKey={activeTabPane}
        animated={tabAnimated || { inkBar: true, tabPane: false }}
        className={styles['marvel-framework-tabs-page']}
        hideAdd={true}
        size="small"
        tabBarStyle={{ margin: 0 }}
        type="editable-card"
        onChange={this.onTabChange}
        onEdit={this.onTabEdit}
      >
        {tabPanes.map(tabPane => (
          <TabPane
            closable={tabPane.key !== 'HOME'}
            key={tabPane.key} 
            style={{ backgroundColor: 'white', height: '100%' }}
            tab={tabPane.name}
          >
            <iframe
              className={styles['marvel-framework-tabs-page-iframe']}
              src={this.combineIframeUrl(tabPane.path)}
            />
          </TabPane>
        ))}
      </Tabs>
    )
  }
}

export default connect(({ openedTabs }: ConnectState) => ({
  tabPanes: openedTabs.tabPanes,
  activeTabPane: openedTabs.activeTabPane
}))(TabLayout);
