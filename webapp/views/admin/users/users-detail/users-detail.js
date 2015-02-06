angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.users.detail', {
    url: '/detail/:userid',
    templateUrl: 'views/admin/users/users-detail/users-detail.html'
  });
});

angular.module('riot').controller('UsersDetailCtrl', function($scope, $stateParams, User, Role){
  var alertId = null;

  var init = function() {
    $scope.getRoles();
    $scope.getUser();
  };

  $scope.getRoles = function() {
    Role.getList().then(function(roles) {
      $scope.roles = roles;
    });
  };

  $scope.getUser = function() {
    $scope.loading = true;
    User.one($stateParams.userid).get().then(function(user) {
      user.password1 = '';
      user.password2 = '';
      $scope.userDetail = user;
    });
  };

  $scope.addUserRole = function(user, role) {
    if (user && role) {
      return $scope.userDetail.addRole(role.id).then(function() {
          $scope.alerts.close(alertId);
          alertId = $scope.alerts.showSuccess('Successfully updated user');
          $scope.getUser();
          $scope.selectedRole = null;
        }, function(reason) {
          $scope.alerts.close(alertId);
          alertId = $scope.alerts.showError('Couldn\'t update user: ' + reason);
          $scope.getUser();
          $scope.selectedRole = null;
        });
    }
  };

  $scope.removeUserRole = function(user, role) {
    return $scope.userDetail.removeRole(role.id).then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated user');
        $scope.getUser();
      }, function(reason) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update user: ' + reason);
        $scope.getUser();
      });
  };

  $scope.save = function() {
    if ($scope.userDetail.password1 === $scope.userDetail.password2 && $scope.userDetail.password1 !== '') {
      $scope.userDetail.password = $scope.userDetail.password1;
    }

    return $scope.userDetail.put().then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated user');
        $scope.getUser();
      }, function(reason) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update user: ' + reason);
        $scope.getUser();
      });
  };

  init();
});
