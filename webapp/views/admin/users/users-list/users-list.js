angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.users.list', {
    url: '/list',
    templateUrl: 'views/admin/users/users-list/users-list.html'
  });
});

angular.module('riot').controller('UsersListCtrl',function($scope, User){
  $scope.users = User.getList().$object;
});
