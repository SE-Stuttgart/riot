angular.module('riot').directive("thingsHierarchy", function(RecursionHelper) {
  return {
    restrict: "A",
    templateUrl: 'commons/directives/things-hierarchy/things-hierarchy.html',
    replace: true,
    scope: {
      thing: '=thingsHierarchy'
    },
    controller: function($scope) {
      var init = function() {

      };

      init();
    },
    compile: function(element) {
        return RecursionHelper.compile(element, function(scope, iElement, iAttrs, controller, transcludeFn){
            // Define your normal link function here.
            // Alternative: instead of passing a function,
            // you can also pass an object with 
            // a 'pre'- and 'post'-link function.
        });
    }
  };
});
