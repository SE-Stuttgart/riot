angular.module('riot', ['ngLocalize', 
                        'ngLocalize.Config', 
                        'ngLocalize.InstalledLanguages', 
                        'ui.bootstrap', 
                        'ui.utils', 
                        'ui.router', 
                        'ui.calendar', 
                        'ui.bootstrap-slider', 
                        'ngAnimate', 
                        'restangular', 
                        'LocalStorageModule',
                        'frapontillo.bootstrap-switch',
                        'ngAnimate',
                        'toggle-switch',
                        'angular-bootstrap-select',
                        'nya.bootstrap.select',
                        'ngWebSocket',
                        'infinite-scroll',
						'btford.markdown'])
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

angular.module('riot').run(function($rootScope, $injector, $window, locale, localeConf) {
  // Workaround for bug in angular-localization.
  // Note: When removing this workaround, also remove the '$injector', '$window', 'locale' and 'localeConf' parameters of this function, if they are not needed anymore.
  // https://github.com/doshprompt/angular-localization/commit/8c438d103d1b48b32859cfddba9c541a819d2b50#diff-c4b0761c7fda8e32bbb08e9de5b78627R275
  var cookieStore;
  if (localeConf.persistSelection && $injector.has('$cookieStore')) {
    cookieStore = $injector.get('$cookieStore');
  }
  locale.setLocale(cookieStore ? cookieStore.get(localeConf.cookieName) : ($window.navigator.userLanguage || $window.navigator.language || localeConf.defaultLocale));
});
