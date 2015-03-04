angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('app.things', {
    abstract: true,
    url: '/things',
    templateUrl: 'views/app/things/things.html'
  });
  $urlRouterProvider.when('/app/things', '/app/things/list');
});

angular.module('riot').controller('ThingsUserCtrl',function($scope){
	console.log("things user");
});
