angular.module('riot').controller('SidebarCtrl',function($scope, $state) {
  $scope.isActive = function(state) {
    return $state.is(state);
  };
});