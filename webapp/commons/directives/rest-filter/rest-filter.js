angular.module('riot').directive('restFilter', function($timeout, locale) {
  return {
    restrict: 'A',
    replace: true,
    scope: {
      "filters": "=",
      "properties": "=",
      "update": "&"
    },
    templateUrl: 'commons/directives/rest-filter/rest-filter.html',
    controller: function($scope) {
      var init = function() {
        locale.ready('common').then(function() {
          $scope.operators = [
            {
              key: 'CT',
              name: locale.getString('common.filter_operator_ct')
            },
            {
              key: 'EQ',
              name: locale.getString('common.filter_operator_eq')
            },
            {
              key: 'NE',
              name: locale.getString('common.filter_operator_ne')
            },
            {
              key: 'LT',
              name: locale.getString('common.filter_operator_lt')
            },
            {
              key: 'LE',
              name: locale.getString('common.filter_operator_le')
            },
            {
              key: 'GT',
              name: locale.getString('common.filter_operator_gt')
            },
            {
              key: 'GE',
              name: locale.getString('common.filter_operator_ge')
            }
          ];
        });
      };

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
        setTimeout($scope.update, 100); //hack for executing update after filters are applayed in other scope
      };

      init();
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
