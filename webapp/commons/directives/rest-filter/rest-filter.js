angular.module('riot').directive('restFilter', function() {
  return {
    restrict: 'A',
    replace: true,
    scope: {
      "filters": "=restFilter",
      "properties": "="
    },
    templateUrl: 'commons/directives/rest-filter/rest-filter.html',
    controller: function($scope) {
      $scope.operators = [
        {
          key: 'EQ',
          name: 'equals'
        },
        {
          key: 'NE',
          name: 'not equals'
        },
        {
          key: 'LT',
          name: 'lower than'
        },
        {
          key: 'LE',
          name: 'lower than or equals'
        },
        {
          key: 'GT',
          name: 'greater than'
        },
        {
          key: 'GE',
          name: 'greater than or equals'
        }
      ];

      $scope.addFilter = function() {
        $scope.filters = $scope.filters || [];
        $scope.filters.push({
          property: $scope.properties[0].key,
          operator: $scope.operators[2].key,
          value: ''
        });
      };

      $scope.removeFilter = function(index) {
        $scope.filters.splice(index, 1);
      };
    },
    link: function(scope, element, attrs, fn) {

    }
  };
});
