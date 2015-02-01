angular.module('riot').config(function(RestangularProvider) {
  RestangularProvider.setBaseUrl('https://localhost:8181/riot/api/v1');
  RestangularProvider.setRequestInterceptor(function(elem, operation) {
    if (operation === "remove") {
       return undefined;
    }
    return elem;
  });
});
