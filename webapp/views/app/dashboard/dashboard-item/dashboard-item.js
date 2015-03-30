angular.module('riot').directive('dashboardItem', function() {
  return {
    restrict: 'EA',
    replace: true,
    scope: {
      icon: "@",
      headingkey: "@",
      viewMoreState: "@",
      maxItems: "@", 
      items: "=" //expected format: [{name: "Item1", state:"app.things.detail", stateParams:{thingid: 1}, type:"warning"}]
    },
    templateUrl: 'views/app/dashboard/dashboard-item/dashboard-item.html',
    link: function(scope, element, attrs, fn) {
      scope.limit = 5;

      angular.forEach(scope.items, function (item) {
        switch(item.type) {
          case "ok":
            item.class = "fa-check-circle";
            break;
          case "warning":
            item.class = "fa-info-circle";
            break;
          case "error":
            item.class = "fa-exclamation-circle";
            break;
          default:
            item.class = "";
        }
      });
    },
    controller: function($scope, $state) {
      $scope.redirect = function(item) {
        $state.go(item.state, item.stateParams);
      };
      
      $scope.viewMore = function() {
        $state.go($scope.viewMoreState);
      };
      
      $scope.getSpacerHeight = function() {
        return ($scope.maxItems - $scope.items.length) * 37;
      };
    }
  };
});
