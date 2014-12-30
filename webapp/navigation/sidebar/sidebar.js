angular.module('riot').controller('SidebarCtrl',function($scope, $location){
  $scope.isActive = function(route) {
    return route === $location.path();
  };
});