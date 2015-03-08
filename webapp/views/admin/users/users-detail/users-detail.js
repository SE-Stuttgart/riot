angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.users.detail', {
    url: '/detail/:userid',
    templateUrl: 'views/admin/users/users-detail/users-detail.html'
  });
});

angular.module('riot').controller('UsersDetailCtrl', function($scope, $state, $stateParams, User, Role){
  var alertId = null;

  var init = function() {
    $scope.roles = {
      data: [],
      selection: null,
      pagination: {
        current: 1,
        limit: 10,
        total: 0
      },
      filter: null,
      disabled: [],
      update: $scope.getRoles,
      toString: function(dataEntry) {
        return dataEntry['roleName'];
      }
    };
    $scope.getRoles();
    $scope.getUser();
  };

  $scope.getRoles = function() {
    Role.getList({limit: $scope.roles.pagination.limit, offset: ($scope.roles.pagination.current - 1) * $scope.roles.pagination.limit}).then(function(roles) {
      $scope.roles.pagination.limit = roles.pagination.limit;
      $scope.roles.pagination.total = roles.pagination.total;
      $scope.roles.data = roles;
    });
  };

  $scope.getUser = function() {
    if ($stateParams.userid === '') {
      $scope.userDetail = {};

      return;
    }

    User.one($stateParams.userid).get().then(function(user) {
      user.password1 = '';
      user.password2 = '';
      $scope.userDetail = user;
      $scope.roles.disabled = user.roles;
    });
  };

  $scope.addUserRole = function(user, role) {
    if (user && role) {
      return $scope.userDetail.addRole(role.id).then(function() {
          $scope.alerts.close(alertId);
          alertId = $scope.alerts.showSuccess('Successfully updated user');
          $scope.getUser();
        }, function(response) {
          $scope.alerts.close(alertId);
          alertId = $scope.alerts.showError('Couldn\'t update user: ' + response.data);
          $scope.getUser();
        });
    }
  };

  $scope.removeUserRole = function(user, role) {
    return $scope.userDetail.removeRole(role.id).then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated user');
        $scope.getUser();
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update user: ' + response.data);
        $scope.getUser();
      });
  };

  $scope.updateUser = function() {
    if ($scope.userDetail.password1 === $scope.userDetail.password2 && $scope.userDetail.password1 !== '') {
      $scope.userDetail.password = $scope.userDetail.password1;
    }

    return $scope.userDetail.put().then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated user');
        $scope.getUser();
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update user: ' + response.data);
        $scope.getUser();
      });
  };

  $scope.createUser = function() {
    $scope.userDetail.password = $scope.userDetail.password || '';
    if ($scope.userDetail.password1 === $scope.userDetail.password2 && $scope.userDetail.password1 !== '') {
      $scope.userDetail.password = $scope.userDetail.password1;
      delete $scope.userDetail.password1;
      delete $scope.userDetail.password2;
    }

    return User.post($scope.userDetail).then(function() {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showSuccess('Successfully updated user');
        $scope.getUser();
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t update user: ' + response.data);
        $scope.getUser();
      });
  };

  $scope.removeUser = function() {
    return $scope.userDetail.remove().then(function() {
        $state.go('admin.users.list');
      }, function(response) {
        $scope.alerts.close(alertId);
        alertId = $scope.alerts.showError('Couldn\'t remove user: ' + response.data);
      });
  };

  $scope.save = function() {
    if ($scope.userDetail.id === undefined) {
      return $scope.createUser();
    }
    else {
      return $scope.updateUser();
    }
  };

  init();
});
