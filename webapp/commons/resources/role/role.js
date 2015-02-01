angular.module('riot').factory('Role', function(Restangular) {
  var resource = Restangular.service('roles');

  Restangular.extendModel('roles', function(model) {
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
