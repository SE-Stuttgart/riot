angular.module('riot').controller('NavigationCtrl',function($scope, $state) {
  $scope.isActive = function(state) {
    return $state.is(state);
  };
});