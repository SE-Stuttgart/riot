angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('landing.documentation', {
    url: '/documentation',
    templateUrl: 'views/landing/documentation/documentation.html'
  });
});

angular.module('riot').controller('DocumentationCtrl', function($scope) {


});
