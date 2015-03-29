angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.authorization.list', {
    url: '/list',
    templateUrl: 'views/admin/authorization/authorization-list/authorization-list.html'
  });
});

angular.module('riot').controller('AuthorizationListCtrl',function($scope, Role, Permission, locale){
  var init = function() {
    $scope.rolesPagination = {
      current: 1,
      limit: 10,
      total: 0
    };
    $scope.rolesFilter = {
      filters: [],
      properties: [
        {
          key: 'roleName',
          name: locale.getString('user.roleName')
        }
      ]
    };

    $scope.permissionsPagination = {
      current: 1,
      limit: 10,
      total: 0
    };
    $scope.permissionsFilter = {
      filters: [],
      properties: [
        {
          key: 'permissionValue',
          name: locale.getString('user.permission')
        }
      ]
    };

    $scope.getRoles();
    $scope.getPermissions();
  };

  $scope.getRoles = function() {
    //pagination
    var parameters = {
      limit: $scope.rolesPagination.limit,
      offset: ($scope.rolesPagination.current - 1) * $scope.rolesPagination.limit
    };

    //filters
    for (var i = 0; i < $scope.rolesFilter.filters.length; i++) {
      var filter = $scope.rolesFilter.filters[i];
      parameters[filter.property + '_' + filter.operator] = filter.value;
    }

    Role.getList(parameters).then(function(roles) {
      $scope.rolesPagination.limit = roles.pagination.limit;
      $scope.rolesPagination.total = roles.pagination.total;
      $scope.roles = roles;
    });
  };

  $scope.getPermissions = function() {
    //pagination
    var parameters = {
      limit: $scope.permissionsPagination.limit,
      offset: ($scope.permissionsPagination.current - 1) * $scope.permissionsPagination.limit
    };

    //filters
    for (var i = 0; i < $scope.permissionsFilter.filters.length; i++) {
      var filter = $scope.permissionsFilter.filters[i];
      parameters[filter.property + '_' + filter.operator] = filter.value;
    }

    Permission.getList(parameters).then(function(permissions) {
      $scope.permissionsPagination.limit = permissions.pagination.limit;
      $scope.permissionsPagination.total = permissions.pagination.total;
      $scope.permissions = permissions;
    });
  };

  locale.ready('user').then(init);
});
