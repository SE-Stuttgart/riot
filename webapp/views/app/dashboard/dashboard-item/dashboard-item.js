angular.module('riot').directive('dashboardItem', function() {
  return {
    restrict: 'EA',
    replace: true,
    scope: {
      icon: "@",
      heading: "@",
      items: "=" //expected format: [{name: "Item1", link="link/to/item/1", type:"warning"},{name: "Item2", link="link/to/item/2", type:"error"}]
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
    }
  };
});
