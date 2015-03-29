angular.module('riot').config(function(RestangularProvider) {
  RestangularProvider.setBaseUrl('/api/v1');
  RestangularProvider.setRequestInterceptor(function(elem, operation) {
    if (operation === "remove") {
      return undefined;
    }
    return elem;
  });
});

angular.module('riot').config(function(RestangularProvider) {
  //REST response interceptor: extract pagination header data
  RestangularProvider.addResponseInterceptor(function(data, operation, what, url, response, deferred) {
    if (operation === 'getList') {
      data.pagination = {
        offset: response.headers('offset'),
        limit: response.headers('limit'),
        total: response.headers('total')
      };
    }

    return data;
  });
});
