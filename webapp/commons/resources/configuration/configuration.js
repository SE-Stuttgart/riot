angular.module('riot').factory('Configuration', function(Restangular) {
    var resource = Restangular.service('config');

    resource.getKey = function() {
        return resource.one('key').get();
    };

    resource.deleteKey = function() {
        return resource.one('key').delete();
    };

    return resource;
});
