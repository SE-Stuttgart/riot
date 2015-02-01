angular.module('riot').factory('User', function(Restangular) {
  var resource = Restangular.service('users');

  resource.self = function() {
    return resource.one('self').get();
  };

  Restangular.extendModel('users', function(model) {
    model.addRole = function(roleID) {
      return model.one('roles', roleID).put();
    };

    model.removeRole = function(roleID) {
      return model.one('roles', roleID).remove();
    };

    return model;
  });

  return resource;
});
