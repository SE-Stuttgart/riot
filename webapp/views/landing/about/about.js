angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('landing.about', {
    url: '/about',
    templateUrl: 'views/landing/about/about.html'
  });
});

angular.module('riot').controller('AboutCtrl', function($scope) {


});
