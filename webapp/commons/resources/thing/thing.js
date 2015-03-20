angular.module('riot').factory('Thing', function(Restangular) {
  var resource = Restangular.service('things');

  Restangular.extendModel('things', function(model) {
    model.getDescription = function() {
      return model.one('description').get();
    };
    
    model.getState = function() {
      return model.one('state').get();
    };

    return model;
  });

  return resource;
});
