angular.module('riot', ['ui.bootstrap','ui.utils','ui.router','ngAnimate']);

angular.module('riot').config(function($stateProvider, $urlRouterProvider) {

    $stateProvider.state('dashboard', {
        url: '/dashboard',
        templateUrl: 'views/dashboard/dashboard.html'
    });
    $stateProvider.state('calendar', {
        url: '/calendar',
        templateUrl: 'views/calendar/calendar.html'
    });
    $stateProvider.state('devices', {
        url: '/devices',
        templateUrl: 'views/devices/devices.html'
    });
    $stateProvider.state('admin', {
        url: '/admin',
        templateUrl: 'views/admin/admin.html'
    });

    $urlRouterProvider.otherwise('/dashboard');

});

angular.module('riot').run(function($rootScope) {

    $rootScope.safeApply = function(fn) {
        var phase = $rootScope.$$phase;
        if (phase === '$apply' || phase === '$digest') {
            if (fn && (typeof(fn) === 'function')) {
                fn();
            }
        } else {
            this.$apply(fn);
        }
    };

});
