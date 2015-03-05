angular.module('riot').directive('restFilter', function($timeout) {
  return {
    restrict: 'A',
    replace: true,
    scope: {
      "filters": "=restFilter",
      "properties": "=restFilterProperties",
      "update": "&restFilterUpdate"
    },
    templateUrl: 'commons/directives/rest-filter/rest-filter.html',
    controller: function($scope) {
      $scope.operators = [
        {
          key: 'CT',
          name: 'contains'
        },
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
          operator: $scope.operators[0].key,
          value: ''
        });
      };

      $scope.removeFilter = function(index) {
        $scope.filters.splice(index, 1);
      };

      $scope.applyFilter = function() {
        $scope.update();
      };

      $scope.resetFilter = function() {
        $scope.filters = [];
        $scope.update();
      };
    },
    link: function(scope, element, attrs, fn) {
      scope.$watchCollection('filters', function() {
        $timeout(function() {
          element.find('.selectpicker').selectpicker();
        }, 0);
      });

      var filterButton = element.find('> button');
      var popup = element.find('> .popup');

      popup.on('mousedown', function(e) {
        e.stopPropagation();
      });

      element.find('#applyButton, #resetButton').on('click', function(e) {
        popup.hide();
      });

      filterButton.on('mousedown', function(e) {
        popup.toggle();
        e.stopPropagation();

        if (popup.is(':visible')) {
          $('body').one('mousedown', function() {
            popup.hide();
          });
        }
      });
    }
  };
});
