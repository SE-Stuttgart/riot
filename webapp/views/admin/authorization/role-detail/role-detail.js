angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.authorization.role', {
    url: '/role/:roleid',
    templateUrl: 'views/admin/authorization/role-detail/role-detail.html'
  });
});

angular.module('riot').controller('RoleDetailCtrl',function($scope, $stateParams, Role, Permission){
  var init = function() {
    $scope.getPermissions();
    $scope.getRole();
  };

  $scope.getPermissions = function() {
    Permission.getList().then(function(permissions) {
      $scope.permissions = permissions;
    });
  };

  $scope.getRole = function() {
    Role.one($stateParams.roleid).get().then(function(role) {
      $scope.roleDetail = role;
    });
  };

  $scope.removeRolePermission = function(role, permission) {
    role.removePermission(permission.id).then(function() {
      $scope.getRole();
    });
  };

  $scope.addRolePermission = function(role, permission) {
    if (role, permission) {
      role.addPermission(permission.id).then(function() {
        $scope.getRole();
        $scope.selectedPermission = null;
      });
    }
  };

  $scope.save = function() {
    $scope.roleDetail.put().then(function() {
      $scope.getRole();
    });
  };

  init();
});
