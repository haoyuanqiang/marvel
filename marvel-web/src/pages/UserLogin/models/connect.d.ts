import type { UserLoginModelType, UserLoginState } from './userLogin';

export { UserLoginModelType, UserLoginState };

export interface Loading {
  global: boolean;
  effects: Record<string, boolean | undefined>;
  models: {
    userLogin?: boolean;
  };
}

export interface ConnectState {
  loading: Loading;
  userLogin: UserLoginState;
}
