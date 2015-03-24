angular.module('riot').config(function($stateProvider, $urlRouterProvider) {
  $stateProvider.state('app.rules', {
    abstract: true,
    url: '/rules',
    templateUrl: 'views/app/rules/rules.html'
  });
  $urlRouterProvider.when('/app/rules', '/app/rules/list');
});

angular.module('riot').controller('RulesCtrl',function($scope){


});