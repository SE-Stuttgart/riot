angular.module('riot').directive('loadingButton', function() {
  return {
    restrict: 'A',
    scope: {
      submit: '&'
    },
    link: function(scope, element, attrs, fn) {
      /* jshint ignore:start */
      element.addClass('ladda-button');
      element.attr('data-style', 'expand-left');
      element.append('<span class="ladda-label" ng-transclude></span>');
      element.on('click', function() {
        var q = scope.submit();
        if (q) {
          var l = Ladda.create(element[0]);
          l.start();
          q.then(function() {
            l.stop();
          });
        }
      });
      /* jshint ignore:end */
    }
  };
});
