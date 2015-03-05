angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.users.list', {
    url: '/list',
    templateUrl: 'views/admin/users/users-list/users-list.html'
  });
});

angular.module('riot').controller('UsersListCtrl',function($scope, User){
  var init = function() {
    $scope.current = 1;
    $scope.limit = 10;
    $scope.total = 0;
    $scope.filters = null;
    $scope.filterProperties = [
      {
        key: 'username',
        name: 'Username'
      }
    ];

    $scope.getUsers();
  };

  $scope.getUsers = function() {
    User.getList({limit: $scope.limit, offset: ($scope.current - 1) * $scope.limit}).then(function(users, u) {
      $scope.limit = users.pagination.limit;
      $scope.total = users.pagination.total;
      $scope.users = users;
    });
  };

  $scope.$watch('limit', function() {
    $scope.getUsers();
  });

  init();
});
