angular.module('riot').controller('NavbarCtrl', function($scope, $rootScope, $state) {
  $scope.logout = function() {
    $rootScope.logout().then(
      function() {
        $state.go('landing.home');
      },
      function() {
        //TODO
      });
  };
});
