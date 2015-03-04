angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('admin.things', {
    abstract: true,
    url: '/things',
    templateUrl: 'views/admin/things/things.html'
  });
  $urlRouterProvider.when('/admin/things', '/admin/things/list');
});

angular.module('riot').controller('ThingsAdminCtrl',function($scope){
  console.log("things admin");
});
