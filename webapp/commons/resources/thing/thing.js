angular.module('riot').factory('Thing', function(Restangular) {
  var resource = Restangular.service('things');

  Restangular.extendModel('things', function(model) {
    model.getDescription = function(roleID) {
      return model.one('description').get();
    };

    return model;
  });

  return resource;
});
