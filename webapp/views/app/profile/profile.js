angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.profile', {
    url: '/profile',
    templateUrl: 'views/app/profile/profile.html'
  });
});

angular.module('riot').controller('ProfileCtrl', function($scope, User) {
  $scope.editUser = User.self().$object;
  $scope.save = function() {
    $scope.editUser.put();
    var obj = User.self();
    $scope.user = obj.$object;
    obj.then(function(user) {
      $scope.editUser = user;
    });
  };
});
