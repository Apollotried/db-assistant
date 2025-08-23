import { Routes } from '@angular/router';
import {Login} from './pages/login/login';
import {Register} from './pages/register/register';
import {DataSourcesManagement} from './pages/data-sources/data-sources-management/data-sources-management';
import {LlmPage} from './pages/llm/llm-page/llm-page';
import {authGuard} from './services/guard/auth-guard';
import {QueryHistory} from './pages/query-history/query-history/query-history';

export const routes: Routes = [
  {
    path: 'login',
    component: Login
  },
  {
    path: 'register',
    component: Register
  },
  {
    path: '',
    component: DataSourcesManagement,
    canActivate: [authGuard]
  },
  {
    path: 'llm',
    component: LlmPage,
    canActivate: [authGuard]
  },
  {
    path: 'history',
    component: QueryHistory,
    canActivate: [authGuard]
  }
];
