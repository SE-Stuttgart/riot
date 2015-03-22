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

    model.addPermission = function(permissionID) {
      return model.one('permissions', permissionID).put();
    };

    model.removePermission = function(permissionID) {
      return model.one('permissions', permissionID).remove();
    };

    return model;
  });

  return resource;
});
