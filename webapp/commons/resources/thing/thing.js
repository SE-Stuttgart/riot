angular.module('riot').factory('Thing', function(Restangular) {
  var resource = Restangular.service('things');

  return resource;
});
