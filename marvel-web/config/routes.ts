import type { IRoute } from 'umi-types';

const routes: IRoute = [
  {
    name: 'login',
    path: '/user/login',
    component: './UserLogin'
  },
  {
    name: 'personal-center',
    path: '/module/personal-center',
    component: './SystemManagement/PersonalCenter'
  },
  {
    path: '/',
    component: '../layouts/SecurityLayout',
    routes: [
      {
        path: '/',
        redirect: '/main'
      },
      {
        path: '/main',
        name: 'framework',
        component: './Framework'
      },
      
      {
        path: '/welcome',
        name: 'welcome',
        component: './HomePage'
      },
      {
        path: '/module',
        component: '../layouts/AuthorizedLayout',
        routes: [
          {
            path: '/module/user-management',
            name: 'menu-management',
            component: './SystemManagement/User'
          },
          {
            path: '/module/menu-management',
            name: 'menu-management',
            component: './SystemManagement/Menu'
          },
          {
            path: '/module/department-management',
            name: 'department-management',
            component: './SystemManagement/Department'
          },
          {
            path: '/module/position-management',
            name: 'position-management',
            component: './SystemManagement/Position'
          },
          {
            path: '/module/role-management',
            name: 'role-management',
            component: './SystemManagement/Role'
          },
          {
            component: './404'
          }
        ]
      },
      
      {
        component: './404'
      }
    ],
  },
  {
    component: './404'
  }
];

export default routes;
