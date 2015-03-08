angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.authorization.role', {
    url: '/role/:roleid',
    templateUrl: 'views/admin/authorization/role-detail/role-detail.html'
  });
});

angular.module('riot').controller('RoleDetailCtrl',function($scope, $state, $stateParams, Role, Permission){
  var alertId = null;

  var init = function() {
    $scope.permissions = {
      data: [],
      selection: null,
      pagination: {
        current: 1,
        limit: 10,
        total: 0
      },
      filter: null,
      disabled: [],
      update: $scope.getPermissions,
      toString: function(dataEntry) {
        return dataEntry['permissionValue'];
      }
    };
    $scope.getRole();
  };

  $scope.getPermissions = function() {
    Permission.getList({limit: $scope.permissions.pagination.limit, offset: ($scope.permissions.pagination.current - 1) * $scope.permissions.pagination.limit}).then(function(permissions) {
      $scope.permissions.pagination.limit = permissions.pagination.limit;
      $scope.permissions.pagination.total = permissions.pagination.total;
      $scope.permissions.data = permissions;
    });
  };

  $scope.getRole = function() {
    if ($stateParams.roleid === '') {
      $scope.roleDetail = {};

      return;
    }

    Role.one($stateParams.roleid).get().then(function(role) {
      $scope.roleDetail = role;
      $scope.permissions.disabled = role.permissions;
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
