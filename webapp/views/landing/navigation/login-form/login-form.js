angular.module('riot').controller('LoginFormCtrl', function($scope, $state, Auth) {
  $scope.username = 'Yoda';
  $scope.password = 'YodaPW';

  $scope.submit = function() {
    Auth.login($scope.username, $scope.password).then(
      function() {
      	$state.go('app.dashboard');
      },
      function(reason) {
      	console.error(reason);
      });
  };
});
