angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.users.list', {
    url: '/list',
    templateUrl: 'views/admin/users/users-list/users-list.html'
  });
});

angular.module('riot').controller('UsersListCtrl',function($scope, User, locale){
  var init = function() {
    $scope.pagination = {
      current: 1,
      limit: 10,
      total: 0
    };

    $scope.filter = {
      filters : [],
      properties: [
        {
          key: 'username',
          name: locale.getString('user.username')
        }
      ]
    };

    $scope.getUsers();
  };

  $scope.getUsers = function() {
    //pagination
    var parameters = {
      limit: $scope.pagination.limit,
      offset: ($scope.pagination.current - 1) * $scope.pagination.limit
    };

    //filters
    for (var i = 0; i < $scope.filter.filters.length; i++) {
      var filter = $scope.filter.filters[i];
      parameters[filter.property + '_' + filter.operator] = filter.value;
    }

    User.getList(parameters).then(function(users) {
      $scope.pagination.limit = users.pagination.limit;
      $scope.pagination.total = users.pagination.total;
      $scope.users = users;
    });
  };

  locale.ready('user').then(init);
});
