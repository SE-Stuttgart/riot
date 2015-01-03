angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.dashboard', {
    url: '/dashboard',
    templateUrl: 'views/app/dashboard/dashboard.html'
  });
});

angular.module('riot').controller('DashboardCtrl', function($scope) {

});
