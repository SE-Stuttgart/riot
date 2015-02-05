angular.module('riot').controller('LoginFormCtrl', function($scope, $state, Auth) {
  $scope.username = 'Yoda';
  $scope.password = 'YodaPW';
  $scope.login = false;

  $scope.submit = function() {
    $scope.login = true;
    Auth.login($scope.username, $scope.password).then(
      function() {
        $scope.login = false;
      },
      function(reason) {
        $scope.login = false;
        $scope.dialog.header = '<i class="fa fa-warning"></i> Login failed';
        $scope.dialog.body = '<strong>' + reason + '</strong>';
        $scope.dialog.show();
      });
  };
});
