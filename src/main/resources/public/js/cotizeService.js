cotizeModule.service('cotizeService', ['$http', function ($http) {
    var serverBaseUrl = 'http://' + window.document.URL.split('/')[2] + '/api/';

    return {
        'createProject': function (project) {

            var query = serverBaseUrl + 'project';

            return $http.post(query, project);
        };
        'loadProject' : function (projectID) {

            var query = serverBaseUrl + 'project/' + projectID;

            return $http.get(query);
        };
    };
}]);