angular.module('riot').run(function($rootScope) {
  $rootScope.alerts = {};
  $rootScope.alerts.list = [];
  $rootScope.alerts.id = 0;

  $rootScope.alerts.close = function(id) {
    for (var i = 0; i < $rootScope.alerts.list.length; i++) {
      if ($rootScope.alerts.list[i].id === id) {
        $rootScope.alerts.list.splice(i, 1);
      }
    }
  };

  $rootScope.alerts.showSuccess = function(msg) {
    return $rootScope.alerts.show('success', msg);
  };

  $rootScope.alerts.showInfo = function(msg) {
    return $rootScope.alerts.show('info', msg);
  };

  $rootScope.alerts.showWarning = function(msg) {
    return $rootScope.alerts.show('warning', msg);
  };

  $rootScope.alerts.showError = function(msg) {
    return $rootScope.alerts.show('danger', msg);
  };

  $rootScope.alerts.show = function(type, msg) {
    var id = $rootScope.alerts.id++;
    $rootScope.alerts.list.push({
      id: id,
      type: type,
      msg: msg
    });

    return id;
  };
});
