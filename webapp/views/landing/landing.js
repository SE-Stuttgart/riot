angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('landing', {
    abstract: true,
    url: '/landing',
    templateUrl: 'views/landing/landing.html'
  });
  $urlRouterProvider.when('/landing', '/landing/home');
  $urlRouterProvider.otherwise('/landing');
});

angular.module('riot').controller('LandingCtrl', function($scope, $state) {

});
