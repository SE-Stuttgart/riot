angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.authorization.permission', {
    url: '/permission/:permissionid',
    templateUrl: 'views/admin/authorization/permission-detail/permission-detail.html'
  });
});

angular.module('riot').controller('PermissionDetailCtrl', function($scope, $stateParams, Permission){
  var alertId = null;

  var init = function() {
    $scope.getPermission();
  };

  $scope.getPermission = function() {
    Permission.one($stateParams.permissionid).get().then(function(permission) {
      $scope.permissionDetail = permission;
    });
  };

  $scope.save = function() {
    return $scope.permissionDetail.put().then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated permission');
        $scope.getPermission();
      }, function(reason) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update permission: ' + reason);
        $scope.getPermission();
      });
  };

  init();
});
