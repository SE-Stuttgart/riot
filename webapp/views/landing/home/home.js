angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('landing.home', {
    url: '/home',
    templateUrl: 'views/landing/home/home.html'
  });
});

angular.module('riot').controller('HomeCtrl', function($scope) {

});
