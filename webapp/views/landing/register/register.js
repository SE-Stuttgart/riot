angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('landing.register', {
    url: '/register',
    templateUrl: 'views/landing/register/register.html'
  });
});

angular.module('riot').controller('RegisterCtrl', function($scope) {

});
