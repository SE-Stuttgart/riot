angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('admin.status', {
    url: '/status',
    templateUrl: 'views/admin/status/status.html'
  });
});

angular.module('riot').controller('StatusCtrl',function($scope){


});
