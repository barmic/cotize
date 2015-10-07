var cotizeModule = angular.module('cotizeApp', [
  'ngRoute',
  'cotizeControllers'
]);

cotizeModule.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/index', {
        templateUrl: 'partials/main.html',
        controller: 'cotizeCreateProject'
      }).
      when('/project/:projectId', {
        templateUrl: 'partials/project.html',
        controller: 'cotizeProject'
      }).
      otherwise({
        redirectTo: '/index'
      });
  }]);