import { HttpInterceptorFn } from '@angular/common/http';
import { tap, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const loggingInterceptor: HttpInterceptorFn = (req, next) => {
  const started = Date.now();
  
  console.log(`[HTTP] ${req.method} ${req.url}`);

  return next(req).pipe(
    tap(() => {
      const elapsed = Date.now() - started;
      console.log(`[HTTP] ${req.method} ${req.url} - ${elapsed}ms`);
    }),
    catchError(error => {
      const elapsed = Date.now() - started;
      console.error(`[HTTP] ${req.method} ${req.url} - Error after ${elapsed}ms`, error);
      return throwError(() => error);
    })
  );
};
