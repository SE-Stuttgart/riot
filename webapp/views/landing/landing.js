angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('landing', {
    abstract: true,
    url: '/landing',
    templateUrl: 'views/landing/landing.html'
  });
  $urlRouterProvider.when('/landing', '/landing/home');
  $urlRouterProvider.otherwise('/landing');
});

angular.module('riot').controller('LandingCtrl', function($scope, $rootScope, $state) {
  $rootScope.$on('auth-login', function(e, args) {
    $state.go('app.dashboard');
  });
});
