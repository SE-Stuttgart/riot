angular.module('riot').controller('LoginFormCtrl', function($scope, Auth) {
  $scope.username = 'Yoda';
  $scope.password = 'YodaPW';

  $scope.submit = function(username, password) {
    Auth.login(username, password);
  };
});
