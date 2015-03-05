angular.module('riot').run(function($rootScope, $sce) {
  $rootScope.dialog = {
    header: '',
    body: '',
    footer: '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>',
    show: function() {
      $('#dialog').modal('show');
    },
    hide: function() {
      $('#dialog').modal('hide');
    }
  };

  $rootScope.$watch('dialog.header', function(newValue, oldValue) {
    if (typeof newValue === 'string') {
      $rootScope.dialog.header = $sce.trustAsHtml(newValue || '');
    }
  });

  $rootScope.$watch('dialog.body', function(newValue, oldValue) {
    if (typeof newValue === 'string') {
      $rootScope.dialog.body = $sce.trustAsHtml(newValue || '');
    }
  });

  $rootScope.$watch('dialog.footer', function(newValue, oldValue) {
    if (typeof newValue === 'string') {
      $rootScope.dialog.footer = $sce.trustAsHtml(newValue || '');
    }
  });
});
