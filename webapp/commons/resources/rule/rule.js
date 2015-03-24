angular.module('riot').factory('Rule', function(Restangular) {
  return Restangular.service('rules');
});