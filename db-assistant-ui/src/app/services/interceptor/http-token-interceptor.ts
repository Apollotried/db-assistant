import {HttpHeaders, HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {Token} from '../token/token';

export const httpTokenInterceptor: HttpInterceptorFn = (req, next) => {
  const tokenService = inject(Token);
  const token = tokenService.token;

  const excludedEndpoints = [
    '/auth/authenticate',
    '/auth/register'
  ]

  const shouldSkipToken = excludedEndpoints.some(endpoint =>
    req.url.includes(endpoint)
  );

  if (shouldSkipToken) {
    return next(req);
  }

  if(token) {
    const authReq = req.clone({
      headers: new HttpHeaders({
        Authorization: `Bearer ${token}`
      })
    })

    return next(authReq);
  }

  return next(req);
};
