angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('admin.users', {
  	abstract: true,
    url: '/users',
    templateUrl: 'views/admin/users/users.html'
  });
  $urlRouterProvider.when('/admin/users', '/admin/users/list');
});

angular.module('riot').controller('UsersCtrl',function($scope){

});
