angular.module('riot').directive('uiElement', function() {
  return {
    restrict: 'A',
    templateUrl: 'commons/directives/ui-element/ui-element.html',
    replace: true,
    scope: {
      value: '=',
      valuename: '=',
      description: '=',
      disabled: '='
    },
    controller: function($scope, Thing) {     
      if($scope.description.uiHint.type === 'ThingReferenceDropDown') {
        var reqiredPermissions = [];
        var selectedId = $scope.value[$scope.valuename];
        Thing.one('find').get({ type: $scope.description.uiHint.targetType}).then(function(thingInfos) {
          $scope.thingInfos = thingInfos;
          angular.forEach(thingInfos, function(thingInfo) {
           if(thingInfo.id === selectedId) {
             $scope.value[$scope.valuename] = thingInfo;
           } 
          });
        });
      }
    }
  };
});
