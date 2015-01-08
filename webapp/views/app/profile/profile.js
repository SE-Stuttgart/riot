angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.profile', {
    url: '/profile',
    templateUrl: 'views/app/profile/profile.html'
  });
});

angular.module('riot').controller('ProfileCtrl', function($scope, User, Restangular) {
  $scope.editUser = User.self().$object;

  $scope.save = function() {
    $scope.editUser.put();
    User.self().then(
      function(u) {
        $scope.editUser = u;
        $scope.user = Restangular.copy(u);
      });
  };
});
