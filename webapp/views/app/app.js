angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('app', {
    abstract: true,
    url: '/app',
    templateUrl: 'views/app/app.html',
    data: {
      auth: {
        authentication: true
      }
    }
  });
  $urlRouterProvider.when('/app', '/app/dashboard');
});

angular.module('riot').controller('AppCtrl', function($scope, $rootScope, $state, $location, $anchorScroll) {
  $rootScope.$on('auth-logout', function(e, args) {
    $state.go('landing.home');
  });
});
