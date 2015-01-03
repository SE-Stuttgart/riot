angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('app', {
    abstract: true,
    url: '/app',
    templateUrl: 'views/app/app.html'
  });
  $urlRouterProvider.when('/app', '/app/dashboard');
});

angular.module('riot').controller('AppCtrl', function($scope) {

});
