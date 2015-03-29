angular.module('riot').factory('Notification',function(Restangular) {
  return Restangular.service('notifications');
});