angular.module('riot').config(function(RestangularProvider) {
  RestangularProvider.setBaseUrl('/api/v1');
  RestangularProvider.setRequestInterceptor(function(elem, operation) {
    if (operation === "remove") {
       return undefined;
    }
    return elem;
  });
});
