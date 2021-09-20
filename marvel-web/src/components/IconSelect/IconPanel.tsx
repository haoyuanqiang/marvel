import React from 'react';
import * as AntdIcons from '@ant-design/icons';
import { Button, Radio, Spin, Tooltip } from 'antd';
import classNames from 'classnames';
import styles from './index.less';

const allIcons: Record<string, any> = AntdIcons;

type IconPanelProps = {
  value: string;
  onSelect: (value: string) => void;
}

type IconPanelState = {
  currentTheme: 'Outlined' | 'Filled' | 'TwoTone';
  icons: Record<string, string[]>;
  loading: boolean;
  value?: string;
}

function getThemeFormIconName(icon: string): string {
  if (_.isString(icon)) {
    const words: string[] = _.words(icon);
    return words[words.length - 1];
  }
  return '';
}

class IconPanel extends React.PureComponent<IconPanelProps, IconPanelState> {
  state: IconPanelState = {
    currentTheme: 'Outlined',
    icons: {},
    loading: true,
    value: '',
  }

  // static getDerivedStateFromProps(nextProps: IconPanelProps, prevState: IconPanelState) {
  //   const nextState: IconPanelState = {};
  //   const { value } = nextProps;
  //   if (typeof value === 'string') {
  //     const theme = getThemeFormIconName(value);
  //     const { currentTheme, value: prevValue } = prevState;
  //     if (theme !== currentTheme) {
  //       nextState.currentTheme = theme || 'Outlined';
  //     }
  //     if (value !== prevValue) {
  //       nextState.value = value;
  //     }
  //   }
  //   if (Object.keys(nextState).length > 0) {
  //     return nextState;
  //   }
  //   return null;
  // }

  options = [
    { label: '线框风格', value: 'Outlined' },
    { label: '实底风格', value: 'Filled' },
    { label: '双色风格', value: 'TwoTone' },
  ]

  componentDidMount() {
    const nextState: IconPanelState = {};
    const { value } = this.props;
    if (typeof value === 'string') {
      const theme = getThemeFormIconName(value);
      nextState.currentTheme = theme || 'Outlined';
      nextState.value = value;
    }
    this.setState(nextState);
    setTimeout(() => {
      const icons = {};
      const iconNames: string[] = Object.keys(allIcons).filter(iconName => iconName[0] >= 'A' && iconName[0] <= 'Z');
      icons.Outlined = iconNames.filter(iconName => iconName.includes('Outlined'));
      icons.Filled = iconNames.filter(iconName => iconName.includes('Filled'));
      icons.TwoTone = iconNames.filter(iconName => iconName.includes('TwoTone'));
      this.setState({ icons, loading: false });
    }, 100);
  }

  handleThemeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = e.target.value as ThemeType;
    this.setState({ currentTheme: value });
  }

  onClick = (event) => {
    const { onSelect } = this.props;
    if (typeof onSelect === 'function') {
      const { name } = event.currentTarget;
      onSelect(name);
    }
  }

  render() {
    const { currentTheme, icons, loading } = this.state;
    const { value } = this.props;
    if (loading) {
      return (
        <Spin>
          <div className={styles['marvel-icons-container']} />
        </Spin>
      )
    }
    const visibleIconList = icons[currentTheme];
    return (
      <div className={styles['marvel-icons-container']}>
        <div className={styles['marvel-icons-switcher']}>
          <Radio.Group
            options={this.options}
            onChange={this.handleThemeChange}
            value={currentTheme}
            optionType="button"
            buttonStyle="solid"
            size="small"
          />
        </div>
        <div className={styles['marvel-icons-list']}>
          {
            visibleIconList && visibleIconList.map(iconName => {
              const Component = allIcons[iconName];
              return (
                <div className={styles['marvel-icons-list-item']} key={iconName}>
                  <Tooltip title={iconName}>
                    <Button
                      name={iconName}
                      className={classNames(styles['marvel-icons-list-item-icon'], {
                        [styles['marvel-icons-list-item-icon-selected']]: value === iconName
                      })}
                      icon={<Component />}
                      size="large"
                      onClick={this.onClick}
                    />
                  </Tooltip>
                </div>
              );
            })
          }
        </div>
        
      </div>
    );
  }
}

export default IconPanel;
