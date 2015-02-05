angular.module('riot', ['ngLocalize', 'ngLocalize.Config', 'ngLocalize.InstalledLanguages', 'ui.bootstrap', 'ui.utils', 'ui.router', 'ngAnimate', 'restangular', 'LocalStorageModule'])
.value('localeConf', {
  basePath: 'languages',
  defaultLocale: 'en', // Note that this has no region suffix
  sharedDictionary: 'common',
  fileExtension: '.lang.json',
  persistSelection: true,
  cookieName: 'COOKIE_LOCALE_LANG',
  observableAttrs: new RegExp('^data-(?!ng-|i18n)'),
  delimiter: '::'
})
.value('localeSupported', [
  'de', 'en'
])
.value('localeFallbacks', {
  'en': 'en', // This maps 'en-*' to 'en'
  'de': 'de' // This maps 'de-*' to 'de'
});

angular.module('riot').run(function($injector, $window, $rootScope, $state, $sce, Auth, locale, localeConf) {
  
  // Workaround for bug in angular-localization.
  // Note: When removing this workaround, also remove the '$injector', '$window', 'locale' and 'localeConf' parameters of this function, if they are not needed anymore.
  // https://github.com/doshprompt/angular-localization/commit/8c438d103d1b48b32859cfddba9c541a819d2b50#diff-c4b0761c7fda8e32bbb08e9de5b78627R275
  var cookieStore;
  if (localeConf.persistSelection && $injector.has('$cookieStore')) {
    cookieStore = $injector.get('$cookieStore');
  }
  locale.setLocale(cookieStore ? cookieStore.get(localeConf.cookieName) : ($window.navigator.userLanguage || $window.navigator.language || localeConf.defaultLocale));
  
  //check authorization on state change
  $rootScope.$on('$stateChangeStart', function(e, state) {
    state.data = state.data || {};
    var auth = state.data.auth;
    if (auth) {
      var roles = auth.roles;
      var premissions = auth.premissions;
      var authenticated = (auth.authentication) ? true : false;
      var grant = false;

      //check roles
      if (roles) {
        for (var i = 0; i < roles.length; i++) {
          grant = grant || $rootScope.hasRole(roles[i]);
        }
      }
      //check permissions
      else if (premissions) {
        for (var j = 0; j < roles.length; j++) {
          grant = grant || $rootScope.hasPermission(roles[j]);
        }
      }
      //check authentication
      else if (authenticated) {
        grant = $rootScope.isAuthenticated();
      }

      if (!grant) {
        e.preventDefault();
        $state.go('landing.home');
      }
    }
  });

  //dialog
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
