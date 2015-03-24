angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('app.rules.list', {
    url: '/list',
    templateUrl: 'views/app/rules/rules-list/rules-list.html',
    controller: 'RulesListCtrl'
  });
});

angular.module('riot').controller('RulesListCtrl',function($scope, $state, Rule){
  $scope.current = 0;
  $scope.limit = 10;
  $scope.total = 0;
  $scope.filters = null;
  $scope.filterProperties = [];
  
  Rule.getList({limit: $scope.limit, offset: $scope.current * $scope.limit}).then(function(rules) {
    $scope.rules = rules;
    $scope.limit = rules.pagination.limit;
    $scope.total = rules.pagination.total;
  });
});