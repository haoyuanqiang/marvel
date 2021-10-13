import React from 'react';
import { Inspector } from 'react-dev-inspector';
import Authorized from '@/components/Authorized';

const InspectorWrapper = process.env.NODE_ENV === 'development' ? Inspector : React.Fragment;

const AuthorizedLayout: React.FC = ({ children }) => {
  return (
    <InspectorWrapper>
      <Authorized>{children}</Authorized>
    </InspectorWrapper>
  );
};

export default AuthorizedLayout;
