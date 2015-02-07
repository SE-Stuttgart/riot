angular.module('riot').directive('dashboardLogItem', function() {
	return {
		restrict: 'EA',
		replace: true,
		scope: {
            text: "=",
            time: "=",
            important: "=",
            icon: "="
		},
		templateUrl: 'views/app/dashboard/dashboard-log-item/dashboard-log-item.html',
		link: function(scope, element, attrs, fn) {
		}
	};
});
