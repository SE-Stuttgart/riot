angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.users.detail', {
    url: '/detail',
    templateUrl: 'views/admin/users/users-detail/users-detail.html'
  });
});

angular.module('riot').controller('UsersDetailCtrl',function($scope){


});
