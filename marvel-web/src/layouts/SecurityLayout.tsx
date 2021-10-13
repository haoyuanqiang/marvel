import React from 'react';
import { isEmpty, join } from 'lodash';
import type { ConnectProps } from 'umi';
import { Redirect } from 'umi';
import { stringify } from 'querystring';
import { Inspector } from 'react-dev-inspector';
import PageLoading from '@/components/PageLoading';
import type { CurrentUser } from '@/models/system/user';
import { getAuthority } from '@/utils/utils';

const InspectorWrapper = process.env.NODE_ENV === 'development' ? Inspector : React.Fragment;

interface SecurityLayoutProps extends ConnectProps {
  loading?: boolean;
  currentUser?: CurrentUser;
}

interface SecurityLayoutState {
  isReady: boolean;
}

class SecurityLayout extends React.Component<SecurityLayoutProps, SecurityLayoutState> {
  state: SecurityLayoutState = {
    isReady: false,
  };

  componentDidMount() {
    this.setState({
      isReady: true,
    });
  }

  render() {
    const { isReady } = this.state;
    const { children } = this.props;

    // 你可以把它替换成你自己的登录认证规则（比如判断 token 是否存在）
    const authority = join(getAuthority(), ';');
    const isLogin = !isEmpty(authority);

    const queryString = stringify({
      redirect: window.location.href,
    });

    if (!isReady) {
      return <PageLoading />;
    }
    if (!isLogin && window.location.pathname !== '/user/login') {
      return <Redirect to={`/user/login?${queryString}`} />;
    }
    return <InspectorWrapper>{children}</InspectorWrapper>;
  }
}

export default SecurityLayout;
