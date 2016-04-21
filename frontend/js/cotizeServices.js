//  Licence Public Barmic
//  copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
var cotizeServices = cotizeModule.service('cotizeServices', []);

cotizeServices.factory('cotizeProjectService', ['$http', function ($http) {
    var serverBaseUrl = window.document.URL.split(':')[0] + '://' + window.document.URL.split('/')[2] + '/api/';

    var service = {};
    service.createProject = function (project) {
        var query = serverBaseUrl + 'project';
        return $http.post(query, project);
    };
    service.loadProject = function (projectID) {
        var query = serverBaseUrl + 'project/' + projectID;
        return $http.get(query);
    };
    service.loadProjectAdmin = function (projectID, adminPass) {
        var query = serverBaseUrl + 'project/' + projectID + '/admin/' + adminPass;
        return $http.get(query);
    };
    service.contribute = function (projectID, contribution) {
        var query = serverBaseUrl + 'project/' + projectID + '/contribution';
        return $http.post(query, contribution);
    };
    service.loadContribution = function (projectID, contributionId) {
        var query = serverBaseUrl + 'project/' + projectID + '/contribution/' + contributionId;
        return $http.get(query);
    };
    service.updateContribution = function (projectID, contributionId, contribution) {
        var query = serverBaseUrl + 'project/' + projectID + '/contribution/' + contributionId;
        return $http.post(query, contribution);
    };
    service.removeContribution = function (projectId, contributionId) {
        var query = serverBaseUrl + 'project/' + projectId + '/contribution/' + contributionId;
        return $http.delete(query);
    };
    service.payedContribution = function (projectId, contributionId) {
        var query = serverBaseUrl + 'project/' + projectId + '/contribution/' + contributionId + '/payed';
        return $http.post(query);
    };
    service.remindContribution = function (projectId, contributionId) {
        var query = serverBaseUrl + 'project/' + projectId + '/contribution/' + contributionId + '/remind';
        return $http.post(query);
    };
    service.allProjects = function (rootSecret, contributionId) {
        var query = serverBaseUrl + 'admin/' + rootSecret + '/project';
        return $http.get(query);
    };
    service.removeProject = function (rootSecret, projectId) {
        var query = serverBaseUrl + 'admin/' + rootSecret + '/project/' + projectId;
        return $http.delete(query);
    };
    service.updateProject = function (projectId, adminPass, fieldName, oldValue, newValue) {
        var query = serverBaseUrl + 'project/' + projectId + '/admin/' + adminPass;
        var data = {
            field : fieldName,
            oldValue : oldValue,
            newValue : newValue
        };
        return $http.post(query, data);
    };
    service.delOutgoing = function (projectId, adminPass, outgoing) {
        var query = serverBaseUrl + 'project/' + projectId + '/admin/' + adminPass + '/outgoing/del';
        return $http.post(query, outgoing);
    };
    service.newOutgoing = function (projectId, adminPass, outgoing) {
        var query = serverBaseUrl + 'project/' + projectId + '/admin/' + adminPass + '/outgoing';
        return $http.post(query, outgoing);
    }
    return service;
    }
]);