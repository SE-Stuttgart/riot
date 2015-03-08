/*
  options         [Object]

    - data        [Array]       List of data entries to display

    - selection   [Object]      Contains the currently selected entry

    - pagination  [Object]
      - current   [Number]      Current page
      - limit     [Number]      Limit of displayed entries
      - total     [Number]      Total number of data entries on server

    - filter      [Object]      TODO

    - disabled    [Array]       List of data entries which will be disabled in the dropdown

    - update      [Function]    A function which will be called when the current page changes
                                You should request the new data entries from the server according to the limit and
                                offset values and update the total data entries

    - toString    [Function]    A function which will transform a data entry into a string which will be displayed in to view.
                                This might be necessary because a single data entry is usually a object with various properties.
 */
angular.module('riot').directive('complexDropdown', function() {
  return {
    restrict: 'A',
    templateUrl: 'commons/directives/complex-dropdown/complex-dropdown.html',
    scope: {
      options: '=complexDropdown'
    },
    controller: function($scope) {
      var init = function() {
        $scope.options.update();
      };

      $scope.isDisabled = function(dataEntry) {
        if ($scope.options.disabled) {
          for (var i = 0; i < $scope.options.disabled.length; i++) {
            if ($scope.options.disabled[i].id == dataEntry.id) {
              return true;
            }
          }
        }

        return false;
      };

      init();
    },
    link: function(scope, element, attrs, fn) {

    }
  };
});
