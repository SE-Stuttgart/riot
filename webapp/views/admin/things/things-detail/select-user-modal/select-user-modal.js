angular.module('riot').controller('SelectUserModalCtrl', function($scope, $modalInstance, User){

  $scope.limit = 10;
  $scope.filter = "";
  $scope.current = 1;
  $scope.selectedUser = {};
  
  User.getList({limit: 10, offset: 0}).then(function(users, u) {
    $scope.users = users;
  });
  
  $scope.setSelectedUser = function(user) {
    $scope.selectedUser = user;
  };
  
  $scope.ok = function () {
    $modalInstance.close($scope.selectedUser);
//    $modalInstance.close($scope.users[0]);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});