angular.module('riot').directive('notificationIcon', function() {
	return {
		restrict: 'EA',
		replace: true,
		scope: {
		    severity: '='
		},
		templateUrl: 'commons/directives/notificationIcon/notificationIcon.html'
	};
});
