angular.module('riot').directive('alerts', function() {
  return {
    restrict: 'A',
    replace: true,
    templateUrl: 'views/app/navigation/navbar/alerts/alerts.html',
    link: function(scope, element, attrs, fn) {

    }
  };
});
