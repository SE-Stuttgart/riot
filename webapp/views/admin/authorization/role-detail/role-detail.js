angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.authorization.role', {
    url: '/role/:roleid',
    templateUrl: 'views/admin/authorization/role-detail/role-detail.html'
  });
});

angular.module('riot').controller('RoleDetailCtrl',function($scope, $state, $stateParams, Role, Permission){
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
    if ($stateParams.roleid === '') {
      $scope.roleDetail = {};

      return;
    }

    Role.one($stateParams.roleid).get().then(function(role) {
      $scope.roleDetail = role;
    });
  };

  $scope.removeRolePermission = function(role, permission) {
    return role.removePermission(permission.id).then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated role');
        $scope.getRole();
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update role: ' + response.data);
        $scope.getRole();
      });
  };

  $scope.addRolePermission = function(role, permission) {
    if (role && permission) {
      return role.addPermission(permission.id).then(function() {
          $scope.alerts.close(alertId);
          alertId = $scope.alerts.showSuccess('Successfully updated role');
          $scope.getRole();
        }, function(response) {
          $scope.alerts.close(alertId);
          alertId = $scope.alerts.showError('Couldn\'t update role: ' + response.data);
          $scope.getRole();
        });
    }
  };

  $scope.updateRole = function() {
    return $scope.roleDetail.put().then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated role');
        $scope.getRole();
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update role: ' + response.data);
        $scope.getRole();
      });
  };

  $scope.createRole = function() {
    return Role.post($scope.roleDetail).then(function(role) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully created role');
        $state.go('admin.authorization.role', {roleid: role.id});
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update role: ' + response.data);
        $scope.getRole();
      });
  };

  $scope.removeRole = function() {
    return $scope.roleDetail.remove().then(function() {
        $state.go('admin.authorization.list');
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t remove role: ' + response.data);
      });
  };

  $scope.save = function() {
    if ($scope.roleDetail.id === undefined) {
      return $scope.createRole();
    }
    else {
      return $scope.updateRole();
    }
  };

  init();
});
