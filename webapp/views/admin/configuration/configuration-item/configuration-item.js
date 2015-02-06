angular.module('riot').directive('configurationItem', function() {
  return {
    restrict: 'E',
    replace: true,
    scope: {
      configItem: "=config"
    },
    templateUrl: 'views/admin/configuration/configuration-item/configuration-item.html',
    controller: function($scope, locale) {
      $scope.isTypeNumber = function () {
        var type = $scope.configItem.dataType.toLowerCase();
        if(type === 'integer' || type === 'long' || type === 'double' || type === 'float') {
          return true;
        }
        return false;
      };

      $scope.isTypeBoolean = function () {
        if($scope.configItem.dataType.toLowerCase() === 'boolean') {
          return true;
        }
        return false;
      };

      //add the description text to the configItem
      locale.ready('configuration').then(function() {
        $scope.configItem.description = locale.getString('configuration.' + $scope.configItem.configKey + '_description');
      });
    }
  };
});
