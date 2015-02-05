angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('admin.configuration', {
    url: '/config',
    templateUrl: 'views/admin/configuration/configuration.html'
  });
});

angular.module('riot').controller('ConfigCtrl', function($scope, $q, Configuration, locale) {

  var configItemsSave;

  //get all configuration entries over the REST interface
  Configuration.getList().then(function(config) {
    $scope.configItems = config;

    //convert to values to the correct types
    angular.forEach($scope.configItems, function(configItem, key) {
      if(configItem.dataType.toLowerCase() === 'string') {
        configItem.configValue = configItem.configValue.substring(1, configItem.configValue.length-1); //remove the leading and trailing "
      } else {
        configItem.configValue = JSON.parse(configItem.configValue.toLowerCase());
      }
    });

    //save the original configuration for later use
    configItemsSave = angular.copy($scope.configItems);
  });

  // locale.ready('common').then(function() {
    $scope.submit = function() {
      var toSubmit = [];
      for (var i = 0; i < $scope.configItems.length; i++) {
        if (!equalsConfigItems($scope.configItems[i], configItemsSave[i])) {
          toSubmit.push($scope.configItems[i]);
        }
      }

      //submit the changed items to the server
      var putPromises = [];
      angular.forEach(toSubmit, function(configItem, key) {
        putPromises.push(configItem.put());
      });

      return $q.all(putPromises).then(function() {
        var resourceName = locale.getString('common.resource_configuration');
        $scope.alert.showSuccess(locale.getString('common.resource_updateSuccess', {
          resource: resourceName
        }));
      }, function() {
        var resourceName = locale.getString('common.resource_configuration');
        $scope.alert.showError(locale.getString('common.resource_updateError', {
          resource: resourceName
        }));
      });
    };
  // });

  $scope.reset = function() {
    $scope.configItems = configItemsSave;
  };

  var equalsConfigItems = function(o1, o2) {
    return o1.configKey === o2.configKey && o1.configValue === o2.configValue && o1.dataType === o2.dataType ? true : false;
  };

});
