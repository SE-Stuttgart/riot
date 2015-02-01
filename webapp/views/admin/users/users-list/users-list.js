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
    $scope.total = 100;
    $scope.filter = null;

    $scope.getUsers();
  };

  $scope.getUsers = function() {
    User.getList({limit: $scope.limit, offset: ($scope.current - 1) * $scope.limit}).then(function(users) {
      $scope.users = users;
    });
  };

  $scope.$watch('limit', function() {
    $scope.getUsers();
  });

  init();
});
