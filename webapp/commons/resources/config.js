angular.module('riot').config(function(RestangularProvider) {
  RestangularProvider.setBaseUrl('http://localhost:8080/riot/api/v1');
});
