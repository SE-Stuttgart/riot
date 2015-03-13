angular.module('riot').directive('confirmDialog', function() {
  return {
    restrict: 'EA',
    replace: true,
    scope: {
      confirm: '&',
      title: '=',
      text: '='
    },
    transclude: true,
    template: '<div ng-click="openDialog()" ng-transclude></div>',
    controller: function($scope, $modal) {

      $scope.openDialog = function() {
        var modalInstance = $modal.open({
          templateUrl: 'commons/directives/confirm-dialog/confirm-dialog.html',
          controller: 'ConfirmDialogCtrl',
          resolve: {
            title: function() {
              return $scope.title;
            },
            text: function() {
              return $scope.text;
            }
          }
        });

        modalInstance.result.then(function() {
          $scope.confirm();
        });
      };
    }
  };
});

angular.module('riot').controller('ConfirmDialogCtrl', function($scope, $modalInstance, title, text) {
  $scope.title = title;
  $scope.text = text;

  $scope.confirm = function() {
    $modalInstance.close();
  };

  $scope.cancel = function() {
    $modalInstance.dismiss();
  };
});
