//  Licence Public Barmic
//  copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
var cotizeModule = angular.module('cotizeApp', [
  'ngRoute',
  'ui.bootstrap',
  'toggle-switch',
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
      when('/project/:projectId/contribution/:contributionId', {
        templateUrl: 'partials/contribution.html',
        controller: 'cotizeContribution'
      }).
      when('/project/:projectId/admin/:passAdmin', {
        templateUrl: 'partials/admin.html',
        controller: 'cotizeAdmin'
      }).
      when('/admin/:rootSecret/project', {
        templateUrl: 'partials/root.html',
        controller: 'cotizeRoot'
      }).
      otherwise({
        redirectTo: '/index'
      });
  }]);