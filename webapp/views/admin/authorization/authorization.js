angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('admin.authorization', {
    abstract: true,
    url: '/authorization',
    templateUrl: 'views/admin/authorization/authorization.html'
  });
  $urlRouterProvider.when('/admin/authorization', '/admin/authorization/list');
});

angular.module('riot').controller('AuthorizationCtrl',function($scope, Role, Permission){

});
