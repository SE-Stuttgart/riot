angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.authorization.role', {
    url: '/role/:roleid',
    templateUrl: 'views/admin/authorization/role-detail/role-detail.html'
  });
});

angular.module('riot').controller('RoleDetailCtrl',function($scope, $stateParams, Role, Permission){
  var alertId = null;

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
    return role.removePermission(permission.id).then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated role');
        $scope.getRole();
      }, function(reason) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update role: ' + reason);
        $scope.getRole();
      });
  };

  $scope.addRolePermission = function(role, permission) {
    if (role && permission) {
      return role.addPermission(permission.id).then(function() {
          $scope.alerts.close(alertId);
          alertId = $scope.alerts.showSuccess('Successfully updated role');
          $scope.selectedPermission = null;
          $scope.getRole();
        }, function(reason) {
          $scope.alerts.close(alertId);
          alertId = $scope.alerts.showError('Couldn\'t update role: ' + reason);
          $scope.selectedPermission = null;
          $scope.getRole();
        });
    }
  };

  $scope.save = function() {
    return $scope.roleDetail.put().then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated role');
        $scope.getRole();
      }, function(reason) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update role: ' + reason);
        $scope.getRole();
      });
  };

  init();
});
