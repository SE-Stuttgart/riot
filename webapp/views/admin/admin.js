angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('admin', {
    abstract: true,
    url: '/admin',
    templateUrl: 'views/admin/admin.html',
    data: {
      auth: {
        roles: [
          'admin'
        ]
      }
    }
  });
  $urlRouterProvider.when('/admin', '/admin/config');
});

angular.module('riot').controller('AdminCtrl',function($scope){


});
