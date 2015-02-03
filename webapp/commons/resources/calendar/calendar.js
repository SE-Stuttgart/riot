angular.module('riot').factory('Calendar',function(Restangular) {
	var resource = Restangular.service('calendar');
	
	return resource;
});