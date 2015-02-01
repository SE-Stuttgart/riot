angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.authorization.permission', {
    url: '/permission/:permissionid',
    templateUrl: 'views/admin/authorization/permission-detail/permission-detail.html'
  });
});

angular.module('riot').controller('PermissionDetailCtrl', function($scope, $stateParams, Permission){
  var init = function() {
    $scope.getPermission();
  };

  $scope.getPermission = function() {
    Permission.one($stateParams.permissionid).get().then(function(permission) {
      $scope.permissionDetail = permission;
    });
  };

  $scope.save = function() {
    $scope.permissionDetail.put().then(function() {
      $scope.getPermission();
    });
  };

  init();
});
