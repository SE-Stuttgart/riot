angular.module('riot').directive('loadingButton', function() {
  return {
    restrict: 'EA',
    replace: true,
    transclude: true,
    scope: {
      submit: '&' //needs to return a promise
    },
    templateUrl: 'commons/directives/loading-button/loading-button.html',
    link: function(scope, element, attrs, fn) {
      scope.internalSubmit = function() {
        var l = Ladda.create(document.querySelector('#loading-btn'));
        l.start();
        scope.submit().then(function() {
          l.stop();
        });
      };
    }
  };
});
