angular.module('riot').factory('User', function(Restangular) {
  var resource = Restangular.service('users');

  resource.self = function() {
    return resource.one('self').get();
  };

  return resource;
});
