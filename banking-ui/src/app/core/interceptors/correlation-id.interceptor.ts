import { HttpInterceptorFn } from '@angular/common/http';
import { v4 as uuidv4 } from 'uuid';

export const correlationIdInterceptor: HttpInterceptorFn = (req, next) => {
  const correlationId = uuidv4();
  
  req = req.clone({
    setHeaders: {
      'X-Correlation-Id': correlationId
    }
  });

  return next(req);
};
