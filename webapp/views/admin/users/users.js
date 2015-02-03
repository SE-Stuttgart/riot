angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('admin.users', {
    url: '/users',
    templateUrl: 'views/admin/users/users.html'
  });
});

angular.module('riot').controller('UsersCtrl',function($scope){

});
