angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.authorization.permission', {
    url: '/permission/:permissionid',
    templateUrl: 'views/admin/authorization/permission-detail/permission-detail.html'
  });
});

angular.module('riot').controller('PermissionDetailCtrl', function($scope, $state, $stateParams, Permission){
  var alertId = null;

  var init = function() {
    $scope.getPermission();
  };

  $scope.getPermission = function() {
    if ($stateParams.permissionid === '') {
      $scope.permissionDetail = {};

      return;
    }

    Permission.one($stateParams.permissionid).get().then(function(permission) {
      $scope.permissionDetail = permission;
    });
  };

  $scope.updatePermission = function() {
    return $scope.permissionDetail.put().then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated permission');
        $scope.getPermission();
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update permission: ' + response.data);
        $scope.getPermission();
      });
  };

  $scope.createPermission = function() {
    return Permission.post($scope.permissionDetail).then(function(permission) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully created permission');
        $state.go('admin.authorization.permission', {permissionid: permission.id});
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update permission: ' + response.data);
        $scope.getPermission();
      });
  };

  $scope.removePermission = function() {
    return $scope.permissionDetail.remove().then(function() {
        $state.go('admin.authorization.list');
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t remove permission: ' + response.data);
      });
  };

  $scope.save = function() {
    if ($scope.permissionDetail.id === undefined) {
      return $scope.createPermission();
    }
    else {
      return $scope.updatePermission();
    }
  };
  init();
});
