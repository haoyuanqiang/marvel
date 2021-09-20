import React from 'react';
import { checkPageAuthorization } from '@/services/system';
import PageLoading from '@/components/PageLoading';
import _ from 'lodash';
import NotAuthorized from './NotAuthorized';

// eslint-disable-next-line @typescript-eslint/ban-types
type AuthorizedProps = {}

type AuthorizedState = {
  loading: boolean;
  isAuthorized: boolean;
  permissions: any;
}

class Authorized extends React.PureComponent<AuthorizedProps, AuthorizedState> {
  state: AuthorizedState = {
    isAuthorized: false,
    loading: true
  }

  componentDidMount() {
    let url = window.location.hash;
    if (_.isString(url)) {
      const position = url.indexOf('?');
      if (position > 0) {
        url = url.slice(0, position);
      }
      const path = url.replace('#', '');
      // 向后台校验页面权限
      checkPageAuthorization(path).then(response => {
        const { code, result } = response;
        this.setState({
          loading: false,
          isAuthorized: code === 0 && !!result.isAuthorized
        });
      }).catch(() => {
        this.setState({
          loading: false,
          isAuthorized: false
        })
      })
    }
  }


  render() {
    const { children } = this.props;
    const { isAuthorized, loading } = this.state;
    if (loading) {
      return <PageLoading />;
    } 
    if (isAuthorized) {
      return children;
    } 
    return <NotAuthorized />;
  }
}

export default Authorized;
