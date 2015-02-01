angular.module('riot').factory('Permission', function(Restangular) {
  var resource = Restangular.service('permissions');

  return resource;
});