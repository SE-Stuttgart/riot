angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.authorization.list', {
    url: '/list',
    templateUrl: 'views/admin/authorization/authorization-list/authorization-list.html'
  });
});

angular.module('riot').controller('AuthorizationListCtrl',function($scope, Role, Permission){
  var init = function() {
    $scope.rolesList = {};
    $scope.rolesList.current = 1;
    $scope.rolesList.limit = 10;
    $scope.rolesList.total = 10;
    $scope.rolesList.filter = null;

    $scope.permissionsList = {};
    $scope.permissionsList.current = 1;
    $scope.permissionsList.limit = 10;
    $scope.permissionsList.total = 10;
    $scope.permissionsList.filter = null;

    $scope.getRoles();
    $scope.getPermissions();
  };

  $scope.getRoles = function() {
    Role.getList({limit: $scope.rolesList.limit, offset: ($scope.rolesList.current - 1) * $scope.rolesList.limit}).then(function(roles) {
      $scope.rolesList.limit = roles.pagination.limit;
      $scope.rolesList.total = roles.pagination.total;
      $scope.roles = roles;
    });
  };

  $scope.getPermissions = function() {
    Permission.getList({limit: $scope.permissionsList.limit, offset: ($scope.permissionsList.current - 1) * $scope.permissionsList.limit}).then(function(permissions) {
      $scope.permissionsList.limit = permissions.pagination.limit;
      $scope.permissionsList.total = permissions.pagination.total;
      $scope.permissions = permissions;
    });
  };

  $scope.$watch('rolesList.limit', function() {
    $scope.getRoles();
  });

  $scope.$watch('permissionsList.limit', function() {
    $scope.getPermissions();
  });

  init();
});
