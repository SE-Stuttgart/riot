angular.module('riot').directive('user', function() {
  return {
    restrict: 'A',
    replace: true,
    templateUrl: 'views/navigation/navbar/user/user.html',
    link: function(scope, element, attrs, fn) {

    }
  };
});
