angular.module('riot').directive('thingsValue', function() {
  return {
    restrict: 'A',
    templateUrl: 'commons/directives/things-value/things-value.html',
    replace: true,
    scope: {
      value: '=',
      description: '='
    },
    link: function(scope, element, attrs, fn) {
      console.log(scope);
    }
  };
});
