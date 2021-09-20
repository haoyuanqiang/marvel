// https://umijs.org/config/
import { defineConfig } from 'umi';
import type { IConfig } from 'umi-types';
import proxy from './proxy';
import routes from './routes';
import theme from './theme';

const { REACT_APP_ENV } = process.env;

const config: IConfig = {
  antd: {
    compact: true,
    dark: false
  },
  dva: {
    hmr: true,
  },
  dynamicImport: {
    loading: '@/components/PageLoading/index',
  },
  esbuild: {},
  hash: true,
  history: {
    type: 'hash',
  },
  ignoreMomentLocale: true,
  locale: {
    // default zh-CN
    default: 'zh-CN',
    antd: true,
    // default true, when it is true, will use `navigator.language` overwrite default
    baseNavigator: true,
  },
  manifest: {
    basePath: '/',
  },
  proxy: proxy[REACT_APP_ENV || 'dev'],
  // umi routes: https://umijs.org/docs/routing
  routes,
  targets: {
    ie: 11,
  },
  theme,
  title: false,
}

export default defineConfig(config);
